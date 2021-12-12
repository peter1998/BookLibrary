package org.library.book.runners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.library.book.services.FileService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BibliomanAPI {

    private static final String BIBLIOMAN_URL = "https://biblioman.chitanka.info";

    private final FileService fileService;

    public static class BookModel {

        private final int id;

        private final Map<String, String> info;

        private final URL thumbnail;

        private String title;
        private String author;
        private String series;
        private String type;
        private String nationality;
        private String language;
        private String category;
        private Set<String> genre;
        private Set<String> themes;

        public BookModel(int id, Map<String, String> info, URL thumbnail) {
            this.id = id;
            this.info = info;
            this.thumbnail = thumbnail;
        }
    }

    public BookModel getBook(int id) {
        var data = getBookData(id);
        var thumbnailURL = getThumbnailURL(data);
        var bookInfo = getBookInformation(data);

        var thumbnail = fileService.downloadFile(thumbnailURL);

        return new BookModel(id, bookInfo, thumbnailURL);
    }

    private URL getThumbnailURL(Element data) {
        var picturePart = data.getElementsByClass("img-thumbnail").first();

        if(picturePart == null) {
            log.error("Cannot find thumbnail element in HTML document: {}", data.wholeText());
            throw new BibliomanAPIException("Cannot find thumbnail element in HTML document");
        }

        String path = picturePart.attr("src");

        if(path.isBlank()) {
            log.error("Cannot find thumbnail URL in thumbnail element: {}", picturePart.wholeText());
            throw new BibliomanAPIException("Cannot find thumbnail URL in thumbnail element");
        }

        return buildURL(path);
    }

    private Map<String, String> getBookInformation(Element data) {

        var dataTable = data.getElementsByTag("dl").first();

        if(dataTable == null) {
            log.error("Cannot find book information table element in HTML document: {}", data.wholeText());
            throw new BibliomanAPIException("Cannot find book information table element in HTML document");
        }

        return  dataTable
                .getElementsByClass("entity-field")
                .stream()
                .filter(e->e.tagName().equals("dd"))
                .collect(Collectors.toMap(this::getType, this::getText));
    }

    private String getType(Element e) {
        return e.classNames()
                .stream()
                .filter(s->s.contains("entity-field"))
                .filter(s->!s.equals("entity-field"))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private String getText(Element e) {
        if(e.hasClass("entity-field-annotation")) {
            return e
                    .childNodes()
                    .stream()
                    .map(Objects::toString)
                    .map(c->c.equals("<br>") ? System.lineSeparator() : c)
                    .collect(Collectors.joining(""))
                    .trim();
        }

        if(e.children().isEmpty()) {
            return e.ownText();
        }
        return e.children().stream().map(this::getText).collect(Collectors.joining("|"));
    }

    private Element getBookData(int id) {

        Document doc;
        try {
            doc = Jsoup.connect(getBookURL(id).toString()).get();
        } catch (IOException e) {
            log.error("Cannot retrieve book from URL", e);
            throw new BibliomanAPIException("Cannot retrieve book from URL", e);
        }

        var response = doc.connection().response();
        int code = response.statusCode();
        if(code != 200) {
            log.error("Connection returned status code: {} Message: {} ", code, response.statusMessage());
            throw new BibliomanAPIException(response);
        }

        var data = doc
                .body()
                .getElementsByAttributeValue("itemtype", "http://schema.org/Book")
                .first();

        if(data == null) {
            log.error("Cannot find book element in HTML document: {}", doc.wholeText());
            throw new BibliomanAPIException("Cannot find book element in HTML document");
        }

        return data;
    }

    private URL getBookURL(int id) {
        return buildURL(BIBLIOMAN_URL + "/books/" + id);
    }

    private URL buildURL(String raw) {
        try {
            return new URL(raw);
        } catch (MalformedURLException e) {
            log.error("Bad URL: {}", raw);
            throw new BibliomanAPIException("Bad URL: "+raw);
        }
    }
}
