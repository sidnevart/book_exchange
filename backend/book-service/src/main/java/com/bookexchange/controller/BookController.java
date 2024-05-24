
package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.security.CurrentUserUtil;
import com.bookexchange.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String genre,
                               @RequestParam(required = false) String author,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false, defaultValue = "false") boolean mine) {
        Long userId = CurrentUserUtil.get().getId();
        return bookService.getBooks(genre, author, title, mine, userId);
    }




    @PostMapping
    public Book addBook(@RequestBody Book book) {
        Long userId = CurrentUserUtil.get().getId();
        book.setOwnerId(userId);
        return bookService.addBook(book);
    }

}
