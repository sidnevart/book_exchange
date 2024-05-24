
package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.model.ExchangeRequest;
import com.bookexchange.model.ExchangeStatus;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeHistoryRepository;
import com.bookexchange.repository.ExchangeRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("unit")
class ExchangeServiceTest {
    @Mock
    private ExchangeHistoryRepository exchangeHistoryRepository;
    @Mock
    private ExchangeRequestRepository exchangeRequestRepository;
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private ExchangeService exchangeService;

    private ExchangeRequest req1, req2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        req1 = new ExchangeRequest();
        req1.setId(1L);
        req1.setFromUserId(10L);
        req1.setToUserId(20L);
        req1.setOfferedBookId(100L);
        req1.setRequestedBookId(200L);
        req1.setStatus(ExchangeStatus.PENDING);
        req1.setCreatedAt(LocalDateTime.now());
        req2 = new ExchangeRequest();
        req2.setId(2L);
        req2.setFromUserId(20L);
        req2.setToUserId(10L);
        req2.setOfferedBookId(200L);
        req2.setRequestedBookId(100L);
        req2.setStatus(ExchangeStatus.ACCEPTED);
        req2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Предложение обмена")
    void proposeExchange() {
        when(exchangeRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ExchangeRequest result = exchangeService.proposeExchange(10L, 20L, 100L, 200L);
        assertThat(result.getFromUserId()).isEqualTo(10L);
        assertThat(result.getToUserId()).isEqualTo(20L);
        assertThat(result.getStatus()).isEqualTo(ExchangeStatus.PENDING);
    }

    @Test
    @DisplayName("Подтверждение обмена")
    void confirmExchange() {
        when(exchangeRequestRepository.findById(1L)).thenReturn(Optional.of(req1));
        when(exchangeRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ExchangeRequest result = exchangeService.respondToExchange(1L, true);
        assertThat(result.getStatus()).isEqualTo(ExchangeStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Отклонение обмена")
    void declineExchange() {
        when(exchangeRequestRepository.findById(1L)).thenReturn(Optional.of(req1));
        when(exchangeRequestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        ExchangeRequest result = exchangeService.respondToExchange(1L, false);
        assertThat(result.getStatus()).isEqualTo(ExchangeStatus.DECLINED);
    }

    @Test
    @DisplayName("История обменов для пользователя")
    void exchangeHistory() {
        when(exchangeRequestRepository.findAll()).thenReturn(Arrays.asList(req1, req2));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book()));
        var history = exchangeService.getExchangeHistory(10L);
        assertThat(history).hasSize(2);
    }

    @Test
    @DisplayName("Ошибка: несуществующий обмен")
    void notFoundExchange() {
        when(exchangeRequestRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> exchangeService.respondToExchange(99L, true))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Exchange not found");
    }

    @Test
    @DisplayName("Получение обмена по id")
    void getExchangeRequestById() {
        when(exchangeRequestRepository.findById(1L)).thenReturn(Optional.of(req1));
        ExchangeRequest found = exchangeService.getExchangeRequestById(1L);
        assertThat(found).isEqualTo(req1);
    }

    @Test
    @DisplayName("Сохранение обмена")
    void saveExchangeRequest() {
        when(exchangeRequestRepository.save(req1)).thenReturn(req1);
        ExchangeRequest saved = exchangeService.saveExchangeRequest(req1);
        assertThat(saved).isEqualTo(req1);
    }
}
