package com.api.rest.conveniencestore.reports.projection;

public interface LoyaltySummaryProjection {
    Long getTotalPointsGenerated();
    Long getTotalPointsRedeemed();
    Long getExpiredPoints();
}
