package com.bookexchange.controller;

import com.bookexchange.dto.ExchangeHistoryResponse;
import com.bookexchange.model.ExchangeHistory;
import com.bookexchange.security.CurrentUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.bookexchange.model.ExchangeRequest;
import com.bookexchange.dto.ExchangeProposalRequest;
import com.bookexchange.service.ExchangeService;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping
    public ExchangeRequest proposeExchange(@RequestBody ExchangeProposalRequest req) {
        Long fromUserId = CurrentUserUtil.get().getId();
        return exchangeService.proposeExchange(
                fromUserId,
                req.getToUserId(),
                req.getOfferedBookId(),
                req.getRequestedBookId()
        );
    }

    @PostMapping("/{id}/confirm")
    public ExchangeRequest respond(@PathVariable Long id, @RequestParam boolean accept) {
        return exchangeService.respondToExchange(id, accept);
    }

    @PostMapping("/{id}/confirm-by-qr")
    public ExchangeRequest confirmByQr(@PathVariable Long id) {
        Long userId = com.bookexchange.security.CurrentUserUtil.get().getId();
        ExchangeRequest req = exchangeService.getExchangeRequestById(id);
        if (!req.getFromUserId().equals(userId) && !req.getToUserId().equals(userId)) {
            throw new RuntimeException("Вы не участник этого обмена");
        }
        if (req.getStatus() != com.bookexchange.model.ExchangeStatus.PENDING) {
            throw new RuntimeException("Обмен уже подтверждён или отклонён");
        }
        req.setStatus(com.bookexchange.model.ExchangeStatus.ACCEPTED);
        return exchangeService.saveExchangeRequest(req);
    }

    @GetMapping("/outgoing")
    public List<ExchangeRequest> getSent() {
        Long fromUserId = CurrentUserUtil.get().getId();
        return exchangeService.getSentRequests(fromUserId);
    }

    @GetMapping("/incoming")
    public List<ExchangeRequest> getIncoming() {
        Long toUserId = CurrentUserUtil.get().getId();
        return exchangeService.getUserRequests(toUserId);
    }

    @GetMapping("/history")
    public List<ExchangeHistoryResponse> getHistory() {
        Long userId = CurrentUserUtil.get().getId();
        return exchangeService.getExchangeHistory(userId);
    }


    @PatchMapping("/{id}/cancel")
    public ExchangeRequest cancelExchange(@PathVariable Long id) {
        Long userId = CurrentUserUtil.get().getId();
        return exchangeService.cancelExchange(id, userId);
    }







}
