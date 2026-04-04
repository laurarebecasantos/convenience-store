package com.api.rest.conveniencestore.reports.dto;

import com.api.rest.conveniencestore.reports.projection.StockSummaryProjection;

public record StockReportDto(
        long totalProducts,
        long lowStockProducts,
        long outOfStockProducts,
        long expiringSoon,
        long expired
) {
    public StockReportDto(StockSummaryProjection projection) {
        this(
                projection.getTotalProducts() != null ? projection.getTotalProducts() : 0,
                projection.getLowStockProducts() != null ? projection.getLowStockProducts() : 0,
                projection.getOutOfStockProducts() != null ? projection.getOutOfStockProducts() : 0,
                projection.getExpiringSoon() != null ? projection.getExpiringSoon() : 0,
                projection.getExpired() != null ? projection.getExpired() : 0
        );
    }
}
