
package com.bookexchange.controller;

import com.bookexchange.dto.ExchangeHistoryResponse;
import com.bookexchange.model.ExchangeRequest;
import com.bookexchange.model.ExchangeStatus;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRequestRepository;
import com.bookexchange.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin/exchanges")
@RequiredArgsConstructor
public class AdminExchangeController {

    private final ExchangeRequestRepository exchangeRepo;
    private final BookRepository bookRepo;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<ExchangeHistoryResponse> getAll(@RequestParam(required = false) String status) {
        List<ExchangeRequest> exchanges = status == null
                ? exchangeRepo.findAll()
                : exchangeRepo.findAll().stream()
                .filter(e -> e.getStatus().name().equalsIgnoreCase(status))
                .toList();

        return exchanges.stream().map(e -> {
            ExchangeHistoryResponse dto = new ExchangeHistoryResponse();
            dto.setExchangeId(e.getId());
            dto.setStatus(e.getStatus().name());
            dto.setDirection("ADMIN");
            dto.setCompleted(e.getStatus() != ExchangeStatus.PENDING);
            dto.setCreatedAt(e.getCreatedAt());

            String offered = bookRepo.findById(e.getOfferedBookId()).map(Book::getTitle).orElse("Unknown");
            String requested = bookRepo.findById(e.getRequestedBookId()).map(Book::getTitle).orElse("Unknown");

            dto.setOfferedBookTitle(offered);
            dto.setRequestedBookTitle(requested);

            return dto;
        }).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteExchange(@PathVariable Long id) {
        exchangeRepo.deleteById(id);
    }
}

