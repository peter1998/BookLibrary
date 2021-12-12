package org.library.book.web.resources;

import lombok.RequiredArgsConstructor;
import org.library.book.CoreUtil;
import org.library.book.services.BookService;
import org.library.book.services.FileService;
import org.library.book.services.domain.BookModel;
import org.library.book.services.domain.NewBookModel;
import org.library.book.web.resources.models.BookEditModel;
import org.library.book.web.resources.models.BookPreview;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookResource {

    private final FileService fileService;
    private final BookService bookService;
    private final CoreUtil coreUtil;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String addBook(BookEditModel bookEditModel) {
        var newBook = new NewBookModel();
        newBook.setTitle(bookEditModel.getTitle());
        newBook.setAuthor(bookEditModel.getAuthor());
        newBook.setPublicationDate(coreUtil.parseDate(bookEditModel.getPublicationDate()));
        newBook.setLanguage(bookEditModel.getLanguage());
        newBook.setGanre(bookEditModel.getGanre());
        newBook.setCover(fileService.createFileOperation(bookEditModel.getCover()));
        newBook.setNumberOfPages(bookEditModel.getNumberOfPages());
        newBook.setNumberOfCopies(bookEditModel.getNumberOfCopies());
        newBook.setDetails(bookEditModel.getDetails());

        bookService.registerBook(newBook);
        return "Success";

    }

    @GetMapping
    public Set<BookPreview> getBooks(UriComponentsBuilder uriComponentsBuilder) {
        Function<BookModel, BookPreview> convert = model -> this.convert(model, uriComponentsBuilder);
        return bookService.getAllBooks().stream().map(convert).collect(Collectors.toSet());
    }

    private BookPreview convert(BookModel model,UriComponentsBuilder uriComponentsBuilder) {
        var preview = new BookPreview();
        preview.setTitle(model.getTitle());
        preview.setAuthor(model.getAuthor());
        preview.setPublicationDate(model.getPublicationDate()+"");
        preview.setCover(model.getCover().getLocationURL(uriComponentsBuilder).toString());
        preview.setDetails(model.getDetails());
        return preview;
    }
}
