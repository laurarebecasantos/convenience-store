package com.api.rest.conveniencestore.dto;

import com.api.rest.conveniencestore.enums.TransactionType;
import com.api.rest.conveniencestore.model.LoyaltyTransaction;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LoyaltyTransactionDto(
        Long id,
        int points,
        TransactionType type,
        Long referenceId,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime createdAt
) {
    public LoyaltyTransactionDto(LoyaltyTransaction t) {
        this(t.getId(), t.getPoints(), t.getType(), t.getReferenceId(), t.getCreatedAt());
    }
}
