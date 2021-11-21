package org.library.book.repositories;

import org.library.book.repositories.documents.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {
}
