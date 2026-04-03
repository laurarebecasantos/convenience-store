package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.model.LoyaltyTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {

    List<LoyaltyTransaction> findByClientIdOrderByCreatedAtDesc(Long clientId);

    Page<LoyaltyTransaction> findByClientIdOrderByCreatedAtDesc(Long clientId, Pageable pageable);

    List<LoyaltyTransaction> findByReferenceId(Long referenceId);
}
