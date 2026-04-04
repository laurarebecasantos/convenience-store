package com.api.rest.conveniencestore.reports.loyalty.dto;

public record LoyaltyReportDto(
        long totalPointsGenerated,
        long totalPointsRedeemed,
        double totalDiscountGiven,
        long activeClients,
        long expiredPoints
) {
}
