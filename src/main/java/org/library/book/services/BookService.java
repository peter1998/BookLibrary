package org.library.book.services;

import lombok.RequiredArgsConstructor;
import org.library.book.repositories.BookRepository;
import org.library.book.repositories.documents.Book;
import org.library.book.services.domain.BookModel;
import org.library.book.services.domain.NewBookModel;
import org.library.book.services.exceptions.BookException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;
    private final FileService fileService;

    public void registerBook(NewBookModel model) {
        if(repository.existsBookByTitle(model.getTitle())) {
            throw new BookException("Book with title "+model.getTitle() + " already exists");
        }
        var book = modelToDocument(model);
        model.getCover().persist();
        repository.save(book);
    }

    public Set<BookModel> getAllBooks() {
        return repository
                .findAll()
                .stream()
                .map(this::documentToModel).collect(Collectors.toSet());
    }

    private Book modelToDocument(NewBookModel model) {
        var book = new Book();
        book.setTitle(model.getTitle());
        book.setAuthor(model.getAuthor());
        book.setPublicationDate(model.getPublicationDate());
        book.setLanguage(model.getLanguage());
        book.setGenre(model.getGanre());
        book.setCoverId(model.getCover().getId());
        book.setNumberOfPages(model.getNumberOfPages());
        book.setNumberOfCopies(model.getNumberOfCopies());
        book.setNumberOfAvailableCopies(model.getNumberOfCopies());
        book.setRating(0);
        book.setDetails(model.getDetails());
        return book;
    }

    private BookModel documentToModel(Book document) {
        var model = new BookModel();
        model.setTitle(document.getTitle());
        model.setAuthor(document.getAuthor());
        model.setPublicationDate(document.getPublicationDate());
        model.setLanguage(document.getLanguage());
        model.setGanre(document.getGenre());
        model.setCover(fileService.getById(document.getCoverId()));
        model.setNumberOfPages(document.getNumberOfPages());
        model.setNumberOfCopies(document.getNumberOfCopies());
        model.setNumberOfAvailableCopies(document.getNumberOfCopies());
        model.setRating(0);
        model.setDetails(document.getDetails());
        return model;
    }
}
