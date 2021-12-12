package org.library.book.runners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;;
import org.jsoup.nodes.Element;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("populate")
public class PopulateRunner implements CommandLineRunner {

    private final BibliomanAPI biblioman;

    @Override
    public void run(String... args) throws Exception {
        var id = 16287;
        biblioman.getBook(id);
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

    private String getType(Element e) {
        return e.classNames()
                .stream()
                .filter(s->s.contains("entity-field"))
                .filter(s->!s.equals("entity-field"))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
