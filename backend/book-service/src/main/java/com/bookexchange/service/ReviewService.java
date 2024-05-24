package com.bookexchange.service;

import com.bookexchange.model.Review;
import com.bookexchange.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review addReview(Long exchangeId, Long reviewerId, Long revieweeId, int rating, String text) {
        Review review = Review.builder()
                .exchangeId(exchangeId)
                .reviewerId(reviewerId)
                .revieweeId(revieweeId)
                .rating(rating)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForExchange(Long exchangeId) {
        return reviewRepository.findByExchangeId(exchangeId);
    }

    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepository.findByRevieweeId(userId);
    }
} 