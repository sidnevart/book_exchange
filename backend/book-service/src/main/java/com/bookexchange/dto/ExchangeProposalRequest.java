package com.bookexchange.dto;

import lombok.Data;

@Data
public class ExchangeProposalRequest {
    private Long fromUserId;
    private Long toUserId;
    private Long offeredBookId;
    private Long requestedBookId;
}
