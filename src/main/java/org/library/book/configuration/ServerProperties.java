package org.library.book.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "book.library")
@Data
public class ServerProperties {

    private Path storage;

    public Path getStoragePath() {
        return storage.toAbsolutePath();
    }

    public Path getTmpStoragePath() {
        return storage.toAbsolutePath().resolve("tmp");
    }
}
