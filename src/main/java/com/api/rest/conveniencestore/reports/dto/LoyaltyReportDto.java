package com.api.rest.conveniencestore.reports.dto;

public record LoyaltyReportDto(
        long totalPointsGenerated,
        long totalPointsRedeemed,
        double totalDiscountGiven,
        long activeClients,
        long expiredPoints
) {
}
