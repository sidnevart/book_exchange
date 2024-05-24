package com.bookexchange.repository;

import com.bookexchange.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByExchangeId(Long exchangeId);
    List<Review> findByRevieweeId(Long revieweeId);
} 