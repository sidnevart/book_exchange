package com.bookexchange.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeHistoryResponse {
    private Long exchangeId;
    private String direction;
    private String status;
    private boolean completed;
    private String offeredBookTitle;
    private String requestedBookTitle;
    private LocalDateTime createdAt;
    private Long fromUserId;
    private Long toUserId;
}
