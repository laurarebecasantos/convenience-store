package com.api.rest.conveniencestore.loyalty.dto;

public record LoyaltySimulateResponseDto(
        int pointsToUse,
        double discount,
        double finalAmount,
        int pointsAfterPurchase
) {}
