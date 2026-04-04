package com.api.rest.conveniencestore.reports.sales.dto;

import com.api.rest.conveniencestore.reports.sales.projection.SalesPeriodProjection;

public record SalesPeriodReportDto(
        String period,
        double totalRevenue,
        long totalSales,
        double averageTicket,
        long totalItemsSold
) {
    public SalesPeriodReportDto(SalesPeriodProjection projection) {
        this(
                projection.getPeriod(),
                projection.getTotalRevenue() != null ? projection.getTotalRevenue() : 0.0,
                projection.getTotalSales() != null ? projection.getTotalSales() : 0,
                projection.getAverageTicket() != null ? projection.getAverageTicket() : 0.0,
                projection.getTotalItemsSold() != null ? projection.getTotalItemsSold() : 0
        );
    }
}
