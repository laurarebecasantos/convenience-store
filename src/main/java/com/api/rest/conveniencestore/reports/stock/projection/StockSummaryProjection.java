package com.api.rest.conveniencestore.reports.stock.projection;

public interface StockSummaryProjection {
    Long getTotalProducts();
    Long getLowStockProducts();
    Long getOutOfStockProducts();
    Long getExpiringSoon();
    Long getExpired();
}
