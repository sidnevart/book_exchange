
package com.bookexchange.repository;

import com.bookexchange.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByGenreContainingAndAuthorContainingAndTitleContaining(String genre, String author, String title);
}
