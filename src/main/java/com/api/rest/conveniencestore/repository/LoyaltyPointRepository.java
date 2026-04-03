package com.api.rest.conveniencestore.repository;

import com.api.rest.conveniencestore.model.LoyaltyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {

    List<LoyaltyPoint> findByClientIdAndRemainingPointsGreaterThanOrderByCreatedAtAsc(Long clientId, int remainingPoints);

    List<LoyaltyPoint> findByExpirationDateBeforeAndRemainingPointsGreaterThan(LocalDateTime date, int remainingPoints);
}
