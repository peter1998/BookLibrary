package org.library.book.web.resources.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPreview {

    private String title;
    private String author;
    private String cover;
    private String publicationDate;
    private String details;
}
