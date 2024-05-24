package com.bookexchange.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long exchangeId;
    private Long reviewerId;
    private Long revieweeId;
    private int rating;
    private String text;
    private LocalDateTime createdAt;
} 