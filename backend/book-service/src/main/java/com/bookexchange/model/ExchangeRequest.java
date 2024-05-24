package com.bookexchange.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exchange_request")
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fromUserId;
    private Long toUserId;

    @Column(name="offered_book_id")
    private Long offeredBookId;
    @Column(name="requested_book_id")
    private Long requestedBookId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="offered_book_id", insertable = false, updatable = false)
    private Book offeredBook;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="requested_book_id", insertable = false, updatable = false)
    private Book requestedBook;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExchangeStatus status;

    private LocalDateTime createdAt;
}
