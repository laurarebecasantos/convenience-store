package com.api.rest.conveniencestore.reports.loyalty.service;

import com.api.rest.conveniencestore.reports.loyalty.dto.LoyaltyReportDto;
import com.api.rest.conveniencestore.reports.loyalty.projection.LoyaltySummaryProjection;
import com.api.rest.conveniencestore.reports.loyalty.repository.LoyaltyReportRepository;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyReportService {

    private final LoyaltyReportRepository loyaltyReportRepository;

    public LoyaltyReportService(LoyaltyReportRepository loyaltyReportRepository) {
        this.loyaltyReportRepository = loyaltyReportRepository;
    }

    public LoyaltyReportDto generateLoyaltyReport() {
        LoyaltySummaryProjection summary = loyaltyReportRepository.findLoyaltySummary();
        double totalDiscountGiven = loyaltyReportRepository.findTotalDiscountGiven();
        long activeClients = loyaltyReportRepository.countActiveClients();

        return new LoyaltyReportDto(
                summary.getTotalPointsGenerated(),
                summary.getTotalPointsRedeemed(),
                totalDiscountGiven,
                activeClients,
                summary.getExpiredPoints()
        );
    }
}
