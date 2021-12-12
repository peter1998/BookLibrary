package org.library.book.repositories;

import org.library.book.repositories.documents.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    boolean existsBookByTitle(String title);
}
