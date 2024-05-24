package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private com.bookexchange.repository.BookRepository bookRepository;

    private String jwtToken;
    private final Long testUserId = 123L;
    private final String testUserEmail = "test@example.com";
    private final String testUserRole = "USER";
    private final String jwtSecret = "D7vK1gE9ZqP4nW0sT3bLrY6xM2aQ8cUfH5eJiXpCtNhRvSzLmYgBqWdXeTkV9uJo";

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", testUserId);
        claims.put("email", testUserEmail);
        claims.put("role", testUserRole);
        jwtToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(testUserEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    @Test
    void addBook_andGetBooks() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setGenre("Test Genre");

        var addBookResult = mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.genre").value("Test Genre"))
                .andExpect(jsonPath("$.ownerId").value(testUserId))
                .andReturn();
        System.out.println("addBook response: " + addBookResult.getResponse().getContentAsString());

        var getBooksResult = mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andReturn();
        System.out.println("getBooks response: " + getBooksResult.getResponse().getContentAsString());

        var getMineResult = mockMvc.perform(get("/api/books?mine=true")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ownerId").value(testUserId))
                .andReturn();
        System.out.println("getBooks?mine=true response: " + getMineResult.getResponse().getContentAsString());

        var getGenreResult = mockMvc.perform(get("/api/books?genre=Test Genre")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].genre").value("Test Genre"))
                .andReturn();
        System.out.println("getBooks?genre=Test Genre response: " + getGenreResult.getResponse().getContentAsString());
    }
} 