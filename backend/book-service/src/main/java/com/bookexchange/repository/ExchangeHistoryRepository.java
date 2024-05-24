
package com.bookexchange.repository;

import com.bookexchange.model.ExchangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, Long> {
    List<ExchangeHistory> findByFromUserIdOrToUserId(Long fromId, Long toId);

}
