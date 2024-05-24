package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.util.JwtTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookServiceIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;

    private String userToken;
    private String adminToken;
    private final Long userId = 123L;
    private final Long adminId = 1L;
    private final String userEmail = "user@example.com";
    private final String adminEmail = "admin@example.com";

    @BeforeEach
    void setUp() {
        userToken = JwtTestUtil.generateToken(userId, userEmail, "USER");
        adminToken = JwtTestUtil.generateToken(adminId, adminEmail, "ADMIN");
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("USER может добавить и получить книгу")
    void userCanAddAndGetBook() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Test Genre");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Book"));

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    @DisplayName("Запрос без токена — 403")
    void requestWithoutTokenForbidden() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("USER не может удалить книгу через админский эндпоинт")
    void userCannotDeleteBookAsAdmin() throws Exception {
        Book book = new Book();
        book.setTitle("Book for delete");
        book.setAuthor("Author");
        book.setGenre("Genre");
        book.setOwnerId(userId);
        book = bookRepository.save(book);

        mockMvc.perform(delete("/api/admin/books/" + book.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN может удалить книгу через админский эндпоинт")
    void adminCanDeleteBook() throws Exception {
        Book book = new Book();
        book.setTitle("Book for delete");
        book.setAuthor("Author");
        book.setGenre("Genre");
        book.setOwnerId(userId);
        book = bookRepository.save(book);

        mockMvc.perform(delete("/api/admin/books/" + book.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
} 