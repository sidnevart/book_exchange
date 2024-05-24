package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("unit")
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    private Book book1, book2, book3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Java");
        book1.setAuthor("Author1");
        book1.setGenre("IT");
        book1.setOwnerId(10L);
        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Spring");
        book2.setAuthor("Author2");
        book2.setGenre("IT");
        book2.setOwnerId(20L);
        book3 = new Book();
        book3.setId(3L);
        book3.setTitle("History");
        book3.setAuthor("Author1");
        book3.setGenre("Non-Fiction");
        book3.setOwnerId(10L);
    }

    @Test
    @DisplayName("Фильтрация по жанру")
    void filterByGenre() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        List<Book> result = bookService.getBooks("IT", null, null, false, 10L);
        assertThat(result).hasSize(2).extracting(Book::getTitle).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    @DisplayName("Фильтрация по автору")
    void filterByAuthor() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        List<Book> result = bookService.getBooks(null, "Author1", null, false, 10L);
        assertThat(result).hasSize(2).extracting(Book::getTitle).containsExactlyInAnyOrder("Java", "History");
    }

    @Test
    @DisplayName("Фильтрация только свои (mine)")
    void filterMine() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        List<Book> result = bookService.getBooks(null, null, null, true, 10L);
        assertThat(result).hasSize(2).extracting(Book::getOwnerId).containsOnly(10L);
    }

    @Test
    @DisplayName("Пустой список книг")
    void emptyList() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        List<Book> result = bookService.getBooks(null, null, null, false, 10L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Все параметры null")
    void allNullParams() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));
        List<Book> result = bookService.getBooks(null, null, null, false, 10L);
        assertThat(result).hasSize(3);
    }
}
