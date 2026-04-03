package com.api.rest.conveniencestore.model;

import com.api.rest.conveniencestore.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_transactions")
@Getter
@NoArgsConstructor
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public LoyaltyTransaction(Long clientId, int points, TransactionType type, Long referenceId) {
        this.clientId = clientId;
        this.points = points;
        this.type = type;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
    }
}
