package com.api.rest.conveniencestore.reports.loyalty.projection;

public interface LoyaltySummaryProjection {
    Long getTotalPointsGenerated();
    Long getTotalPointsRedeemed();
    Long getExpiredPoints();
}
