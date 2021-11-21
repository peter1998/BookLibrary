package org.library.book.repositories.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document
@NoArgsConstructor
@Data
public class Book {

    @Id
    private String id;
    private String title;
    private String author;
    private UUID coverId;
    private Date publicationDate;
    private String details;
    private String language;
    private String genre;
    private int numberOfPages;
    private int numberOfCopies;
    private float rating;

    private List<Comment> comments;
}
