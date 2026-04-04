package com.api.rest.conveniencestore.loyalty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points")
@Getter
@NoArgsConstructor
public class LoyaltyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private int points;

    @Column(name = "remaining_points", nullable = false)
    private int remainingPoints;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public LoyaltyPoint(Long clientId, int points) {
        this.clientId = clientId;
        this.points = points;
        this.remainingPoints = points;
        this.createdAt = LocalDateTime.now();
        this.expirationDate = this.createdAt.plusDays(90);
    }

    public void deductPoints(int amount) {
        this.remainingPoints -= amount;
    }

    public void expirePoints() {
        this.remainingPoints = 0;
    }
}
