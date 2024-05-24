package com.bookexchange.controller;

import com.bookexchange.model.Review;
import com.bookexchange.security.CurrentUserUtil;
import com.bookexchange.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        Long reviewerId = CurrentUserUtil.get().getId();
        return reviewService.addReview(
                review.getExchangeId(),
                reviewerId,
                review.getRevieweeId(),
                review.getRating(),
                review.getText()
        );
    }

    @GetMapping("/exchange/{exchangeId}")
    public List<Review> getReviewsForExchange(@PathVariable Long exchangeId) {
        return reviewService.getReviewsForExchange(exchangeId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsForUser(@PathVariable Long userId) {
        return reviewService.getReviewsForUser(userId);
    }
} 