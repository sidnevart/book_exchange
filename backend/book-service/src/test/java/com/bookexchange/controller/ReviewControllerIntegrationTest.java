package com.bookexchange.controller;

import com.bookexchange.model.Review;
import com.bookexchange.repository.ReviewRepository;
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
class ReviewControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReviewRepository reviewRepository;

    private String userToken;
    private final Long userId = 123L;
    private final String jwtSecret = "D7vK1gE9ZqP4nW0sT3bLrY6xM2aQ8cUfH5eJiXpCtNhRvSzLmYgBqWdXeTkV9uJo";

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", "user@example.com");
        claims.put("role", "USER");
        userToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("user@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    @Test
    @DisplayName("Добавление и получение отзыва")
    void addAndGetReview() throws Exception {
        Review review = new Review();
        review.setExchangeId(1L);
        review.setRevieweeId(2L);
        review.setRating(5);
        review.setText("Отличный обмен!");

        mockMvc.perform(post("/api/reviews")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rating").value(5));

        mockMvc.perform(get("/api/reviews/exchange/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Отличный обмен!"));

        mockMvc.perform(get("/api/reviews/user/2")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating").value(5));
    }
} 