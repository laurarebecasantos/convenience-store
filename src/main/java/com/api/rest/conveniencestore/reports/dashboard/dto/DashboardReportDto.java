package com.api.rest.conveniencestore.reports.dashboard.dto;

public record DashboardReportDto(
        double revenueToday,
        long salesToday,
        long lowStockProducts,
        long pointsEarnedToday
) {
}
