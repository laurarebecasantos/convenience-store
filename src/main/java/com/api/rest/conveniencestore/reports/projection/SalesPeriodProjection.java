package com.api.rest.conveniencestore.reports.projection;

public interface SalesPeriodProjection {
    String getPeriod();
    Double getTotalRevenue();
    Long getTotalSales();
    Double getAverageTicket();
    Long getTotalItemsSold();
}
