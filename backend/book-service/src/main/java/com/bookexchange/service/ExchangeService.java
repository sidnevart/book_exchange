package com.bookexchange.service;

import com.bookexchange.dto.ExchangeHistoryResponse;
import com.bookexchange.model.ExchangeRequest;
import com.bookexchange.model.ExchangeStatus;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRequestRepository exchangeRepository;
    private final BookRepository bookRepository;


    public List<ExchangeRequest> getAllExchanges(Long userId) {
        return exchangeRepository.findAll();
    }

    public ExchangeRequest proposeExchange(Long fromUserId, Long toUserId, Long offeredBookId, Long requestedBookId) {
        ExchangeRequest request = new ExchangeRequest();
        request.setFromUserId(fromUserId);
        request.setToUserId(toUserId);
        request.setOfferedBookId(offeredBookId);
        request.setRequestedBookId(requestedBookId);
        request.setStatus(ExchangeStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        return exchangeRepository.save(request);
    }

    public ExchangeRequest respondToExchange(Long exchangeId, boolean accepted) {
        ExchangeRequest request = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        request.setStatus(accepted ? ExchangeStatus.ACCEPTED : ExchangeStatus.DECLINED);
        return exchangeRepository.save(request);
    }

    public List<ExchangeRequest> getUserRequests(Long userId) {
        return exchangeRepository.findByToUserId(userId);
    }

    public List<ExchangeRequest> getSentRequests(Long userId) {
        return exchangeRepository.findByFromUserId(userId);
    }

    public List<ExchangeHistoryResponse> getExchangeHistory(Long userId) {
        return exchangeRepository
            .findByFromUserIdOrToUserId(userId, userId)  
            .stream()
            .map(e -> {
                ExchangeHistoryResponse dto = new ExchangeHistoryResponse();
                dto.setExchangeId(e.getId());
                dto.setStatus(e.getStatus().name());
                dto.setCreatedAt(e.getCreatedAt());
                dto.setOfferedBookTitle(
                  bookRepository.findById(e.getOfferedBookId())
                    .map(b -> b.getTitle()).orElse("—")
                );
                dto.setRequestedBookTitle(
                  bookRepository.findById(e.getRequestedBookId())
                    .map(b -> b.getTitle()).orElse("—")
                );

                // Заполняем поля from/to для фронта
                dto.setFromUserId(e.getFromUserId());
                dto.setToUserId(e.getToUserId());

                boolean isOutgoing = Objects.equals(e.getFromUserId(), userId);
                dto.setDirection(isOutgoing ? "OUTGOING" : "INCOMING");
                dto.setCompleted(e.getStatus() != ExchangeStatus.PENDING);

                return dto;
            })
            .collect(Collectors.toList());
    }

    public ExchangeRequest cancelExchange(Long exchangeId, Long userId) {
        ExchangeRequest req = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

        if (!req.getFromUserId().equals(userId)) {
            throw new RuntimeException("You can cancel only your own requests");
        }

        if (req.getStatus() != ExchangeStatus.PENDING) {
            throw new RuntimeException("Only pending requests can be cancelled");
        }

        req.setStatus(ExchangeStatus.DECLINED); 
        return exchangeRepository.save(req);
    }

    public ExchangeRequest getExchangeRequestById(Long id) {
        return exchangeRepository.findById(id).orElseThrow(() -> new RuntimeException("Exchange not found"));
    }

    public ExchangeRequest saveExchangeRequest(ExchangeRequest req) {
        return exchangeRepository.save(req);
    }
}
