package com.bookexchange.repository;

import com.bookexchange.model.ExchangeHistory;
import com.bookexchange.model.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
    List<ExchangeRequest> findByToUserId(Long userId);
    List<ExchangeRequest> findByFromUserId(Long userId);
    List<ExchangeRequest> findByFromUserIdOrToUserId(Long from, Long to);

}
