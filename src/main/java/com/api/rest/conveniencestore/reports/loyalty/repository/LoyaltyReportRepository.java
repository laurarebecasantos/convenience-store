package com.api.rest.conveniencestore.reports.loyalty.repository;

import com.api.rest.conveniencestore.loyalty.model.LoyaltyTransaction;
import com.api.rest.conveniencestore.reports.loyalty.projection.LoyaltySummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface LoyaltyReportRepository extends Repository<LoyaltyTransaction, Long> {

    @Query(nativeQuery = true, value = """
            SELECT
                COALESCE(SUM(CASE WHEN lt.type = 'EARN' THEN lt.points ELSE 0 END), 0) AS totalPointsGenerated,
                COALESCE(SUM(CASE WHEN lt.type = 'REDEEM' THEN lt.points ELSE 0 END), 0) AS totalPointsRedeemed,
                COALESCE(SUM(CASE WHEN lt.type = 'EXPIRE' THEN lt.points ELSE 0 END), 0) AS expiredPoints
            FROM loyalty_transactions lt
            """)
    LoyaltySummaryProjection findLoyaltySummary();

    @Query(nativeQuery = true, value = """
            SELECT COALESCE(SUM(s.discount), 0)
            FROM sales s
            WHERE s.status = 'APPROVED'
              AND s.discount > 0
            """)
    double findTotalDiscountGiven();

    @Query(nativeQuery = true, value = """
            SELECT COUNT(DISTINCT lp.client_id)
            FROM loyalty_points lp
            WHERE lp.remaining_points > 0
              AND lp.expiration_date > NOW()
            """)
    long countActiveClients();

    @Query(nativeQuery = true, value = """
            SELECT COALESCE(SUM(lt.points), 0)
            FROM loyalty_transactions lt
            WHERE lt.type = 'EARN'
              AND DATE(lt.created_at) = CURDATE()
            """)
    long countPointsEarnedToday();
}
