package org.library.book.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.book.configuration.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ServerProperties properties;

    public static class FileOperation {

        private final UUID id;
        private final FileService service;
        private boolean inTemp = true;
        private Path location;

        private FileOperation(MultipartFile formFile, FileService service) {
            this.service = service;
            this.id = service.getUniqueID();
            var originalName = formFile.getOriginalFilename();
            var ext = getExtension(originalName);
            location = service.getTempDirectory().resolve(this.id.toString()+ext);
            try {
                Files.copy(formFile.getInputStream(), location);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private FileOperation(URL file, FileService service) {
            this.service = service;
            this.id = service.getUniqueID();

            var name = Paths.get(file.getFile()).getFileName().toString();
            name = URLDecoder.decode(name, StandardCharsets.UTF_8);

            var ext = getExtension(name);
            location = service.getTempDirectory().resolve(this.id.toString()+ext);
            try {
                Files.copy(file.openStream(), location);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private String getExtension(String name) {
            if(name != null) {
                var dotLocation = name.lastIndexOf('.');
                if (dotLocation!=-1) {
                    return name.substring(dotLocation);
                }
            }
            return "";
        }

        private FileOperation(UUID id, FileService service) {
            this.service = service;
            this.id = id;

            this.location =
                    service.findFile(id, service.getStorage())
                       .orElse(service.findFile(id, service.getStorage())
                               .orElseThrow(()->new IllegalStateException("File not found")));
        }

        public Path getLocation() {
            return location;
        }

        public URI getLocationURL(UriComponentsBuilder urlBuilder) {
            var path = ("/storage/" + service.getStorage().relativize(location))
                    .replace("\\", "/");
            return urlBuilder.path(path).build().toUri();
        }

        public UUID getId() {
            return id;
        }

        public void persist() {
            if(!inTemp) return;
            var newPath = service.getStorage().resolve(location.getFileName());
            try {
                Files.copy(location, newPath, StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(location);
                inTemp = false;
                location = newPath;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public boolean isTemp() {
            return inTemp;
        }
    }

    private Path getTempDirectory() {
        try {
            var temp = properties.getTmpStoragePath();
            Files.createDirectories(temp);
            return temp;
        } catch (IOException e) {
            log.error("Could not create temp directory", e);
            throw new IllegalStateException(e);
        }
    }

    private Optional<Path> findFile(UUID fileId, Path dir) {
        Predicate<Path> isMatch = file -> file.getFileName().toString().contains(fileId.toString());
        try {
            return Files.list(dir)
                    .filter(Files::isRegularFile)
                    .filter(isMatch)
                    .findFirst();

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Path getStorage() {
        try {
            var temp = properties.getStoragePath();
            Files.createDirectories(temp);
            return temp;
        } catch (IOException e) {
            log.error("Could not create storage directory", e);
            throw new IllegalStateException(e);
        }
    }

    private UUID getUniqueID() {
        try {
            var files = Stream.concat(
                    Files.list(getStorage()),
                    Files.list(getTempDirectory())
            ).filter(Files::isRegularFile)
             .map(Path::getFileName)
             .map(Path::toString)
             .distinct()
             .collect(Collectors.toList());

            var id = UUID.randomUUID();
            var idStr = id.toString();

            Function<String, Boolean> isUnique = s -> files
                    .stream().anyMatch(p->p.contains(s));

            while (isUnique.apply(idStr)) {
                id = UUID.randomUUID();
                idStr = id.toString();
            }

            return id;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public FileOperation createFileOperation(MultipartFile file) {
        return new FileOperation(file, this);
    }

    public FileOperation downloadFile(URL file) {
        return new FileOperation(file, this);
    }

    public FileOperation getById(UUID fileId) {
        return new FileOperation(fileId, this);
    }
}
