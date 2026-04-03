package com.api.rest.conveniencestore.dto;

public record LoyaltySimulateResponseDto(
        int pointsToUse,
        double discount,
        double finalAmount,
        int pointsAfterPurchase
) {}
