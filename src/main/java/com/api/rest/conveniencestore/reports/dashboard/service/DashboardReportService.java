package com.api.rest.conveniencestore.reports.dashboard.service;

import com.api.rest.conveniencestore.reports.dashboard.dto.DashboardReportDto;
import com.api.rest.conveniencestore.reports.loyalty.repository.LoyaltyReportRepository;
import com.api.rest.conveniencestore.reports.sales.repository.SalesReportRepository;
import com.api.rest.conveniencestore.reports.stock.repository.StockReportRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardReportService {

    private final SalesReportRepository salesReportRepository;
    private final StockReportRepository stockReportRepository;
    private final LoyaltyReportRepository loyaltyReportRepository;

    public DashboardReportService(SalesReportRepository salesReportRepository,
                                  StockReportRepository stockReportRepository,
                                  LoyaltyReportRepository loyaltyReportRepository) {
        this.salesReportRepository = salesReportRepository;
        this.stockReportRepository = stockReportRepository;
        this.loyaltyReportRepository = loyaltyReportRepository;
    }

    public DashboardReportDto generateDashboard() {
        Object[] todaySales = salesReportRepository.findTodaySalesSummary();
        Object[] row = (Object[]) todaySales[0];

        double revenueToday = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
        long salesToday = row[1] != null ? ((Number) row[1]).longValue() : 0;

        long lowStockProducts = stockReportRepository.countLowStockProducts();
        long pointsEarnedToday = loyaltyReportRepository.countPointsEarnedToday();

        return new DashboardReportDto(revenueToday, salesToday, lowStockProducts, pointsEarnedToday);
    }
}
