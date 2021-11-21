package org.library.book.repositories.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
public class Comment {

    @Id
    private String id;
    private String author;
    private Date publicationDate;
    private String text;
    private float rating;
}
