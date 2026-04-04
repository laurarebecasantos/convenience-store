package com.api.rest.conveniencestore.reports.dto;

public record DashboardReportDto(
        double revenueToday,
        long salesToday,
        long lowStockProducts,
        long pointsEarnedToday
) {
}
