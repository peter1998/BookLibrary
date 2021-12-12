package org.library.book.web.resources.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookEditModel {


    private String title;
    private String author;
    private String publicationDate;
    private MultipartFile cover;
    private String language;
    private String ganre;
    private int numberOfPages;
    private int numberOfCopies;
    private String details;
}
