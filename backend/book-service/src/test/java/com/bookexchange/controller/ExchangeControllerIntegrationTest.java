package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.model.ExchangeRequest;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class ExchangeControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ExchangeRequestRepository exchangeRequestRepository;

    private String user1Token;
    private String user2Token;
    private Long user1Id = 101L;
    private Long user2Id = 202L;
    private final String jwtSecret = "D7vK1gE9ZqP4nW0sT3bLrY6xM2aQ8cUfH5eJiXpCtNhRvSzLmYgBqWdXeTkV9uJo";

    private Long book1Id;
    private Long book2Id;

    @BeforeEach
    void setUp() {
        user1Token = generateToken(user1Id, "user1@example.com", "USER");
        user2Token = generateToken(user2Id, "user2@example.com", "USER");
        exchangeRequestRepository.deleteAll();
        bookRepository.deleteAll();
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setGenre("Genre 1");
        book1.setOwnerId(user1Id);
        book1Id = bookRepository.save(book1).getId();
        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setGenre("Genre 2");
        book2.setOwnerId(user2Id);
        book2Id = bookRepository.save(book2).getId();
    }

    private String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    @Test
    @DisplayName("Пользователь может предложить обмен и получить заявки")
    void proposeAndGetExchange() throws Exception {
        String reqJson = "{" +
                "\"toUserId\":" + user2Id + "," +
                "\"offeredBookId\":" + book1Id + "," +
                "\"requestedBookId\":" + book2Id + "}";
        var proposeResult = mockMvc.perform(post("/api/exchange")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromUserId").value(user1Id))
                .andExpect(jsonPath("$.toUserId").value(user2Id))
                .andReturn();
        System.out.println("proposeExchange response: " + proposeResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/exchange/outgoing")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fromUserId").value(user1Id));

        mockMvc.perform(get("/api/exchange/incoming")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].toUserId").value(user2Id));
    }

    @Test
    @DisplayName("Пользователь может подтвердить и отклонить обмен, а также получить историю")
    void confirmDeclineAndHistory() throws Exception {
        String reqJson = "{" +
                "\"toUserId\":" + user2Id + "," +
                "\"offeredBookId\":" + book1Id + "," +
                "\"requestedBookId\":" + book2Id + "}";
        var proposeResult = mockMvc.perform(post("/api/exchange")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andReturn();
        Long exchangeId = objectMapper.readTree(proposeResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/exchange/" + exchangeId + "/confirm?accept=true")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        var proposeResult2 = mockMvc.perform(post("/api/exchange")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andReturn();
        Long exchangeId2 = objectMapper.readTree(proposeResult2.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/exchange/" + exchangeId2 + "/confirm?accept=false")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        mockMvc.perform(get("/api/exchange/history")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].exchangeId").exists());
    }

    @Test
    @DisplayName("Админ может удалить обмен через админский эндпоинт")
    void adminCanDeleteExchange() throws Exception {
        String reqJson = "{" +
                "\"toUserId\":" + user2Id + "," +
                "\"offeredBookId\":" + book1Id + "," +
                "\"requestedBookId\":" + book2Id + "}";
        var proposeResult = mockMvc.perform(post("/api/exchange")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andReturn();
        Long exchangeId = objectMapper.readTree(proposeResult.getResponse().getContentAsString()).get("id").asLong();

        String adminToken = generateToken(1L, "admin@example.com", "ADMIN");
        mockMvc.perform(delete("/api/admin/exchanges/" + exchangeId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Подтверждение обмена по QR-коду")
    void confirmByQr() throws Exception {
        String reqJson = "{" +
                "\"toUserId\":" + user2Id + "," +
                "\"offeredBookId\":" + book1Id + "," +
                "\"requestedBookId\":" + book2Id + "}";
        var proposeResult = mockMvc.perform(post("/api/exchange")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andReturn();
        Long exchangeId = objectMapper.readTree(proposeResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/exchange/" + exchangeId + "/confirm-by-qr")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
} 