package org.library.book.services.domain;

import lombok.Data;
import org.library.book.services.FileService;

import java.util.Date;

@Data
public class BookModel {

    private String title;
    private String author;
    private Date publicationDate;
    private FileService.FileOperation cover;
    private String language;
    private String ganre;
    private int numberOfPages;
    private int numberOfCopies;
    private int numberOfAvailableCopies;
    private float rating;
    private String details;
}
