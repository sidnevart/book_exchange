package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooks(String genre, String author, String title, boolean mineOnly, Long currentUserId) {
        List<Book> books = bookRepository.findAll();
        System.out.println("BookService.getBooks: исходный тип списка: " + books.getClass());
        List<Book> result = books.stream()
                .filter(b -> genre == null || b.getGenre().equalsIgnoreCase(genre))
                .filter(b -> author == null || b.getAuthor().equalsIgnoreCase(author))
                .filter(b -> title == null || b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(b -> !mineOnly || (b.getOwnerId() != null && b.getOwnerId().equals(currentUserId)))
                .collect(java.util.stream.Collectors.toList());
        System.out.println("BookService.getBooks: возвращаемый тип списка: " + result.getClass());
        return result;
    }
}
