package com.api.rest.conveniencestore.reports.sales.projection;

public interface SalesPeriodProjection {
    String getPeriod();
    Double getTotalRevenue();
    Long getTotalSales();
    Double getAverageTicket();
    Long getTotalItemsSold();
}
