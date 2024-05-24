package com.bookexchange.service;

import com.bookexchange.model.Review;
import com.bookexchange.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("unit")
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;
    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Добавление отзыва")
    void addReview() {
        Review review = Review.builder()
                .exchangeId(1L)
                .reviewerId(2L)
                .revieweeId(3L)
                .rating(4)
                .text("Good!")
                .build();
        when(reviewRepository.save(any())).thenReturn(review);
        Review saved = reviewService.addReview(1L, 2L, 3L, 4, "Good!");
        assertThat(saved.getExchangeId()).isEqualTo(1L);
        assertThat(saved.getRating()).isEqualTo(4);
        verify(reviewRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Получение отзывов по exchangeId")
    void getReviewsForExchange() {
        Review r = Review.builder().exchangeId(1L).build();
        when(reviewRepository.findByExchangeId(1L)).thenReturn(List.of(r));
        List<Review> result = reviewService.getReviewsForExchange(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Получение отзывов по revieweeId")
    void getReviewsForUser() {
        Review r = Review.builder().revieweeId(3L).build();
        when(reviewRepository.findByRevieweeId(3L)).thenReturn(Arrays.asList(r));
        List<Review> result = reviewService.getReviewsForUser(3L);
        assertThat(result).hasSize(1);
    }
} 