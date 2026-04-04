package com.api.rest.conveniencestore.reports.sales.repository;

import com.api.rest.conveniencestore.sale.model.Sale;
import com.api.rest.conveniencestore.reports.sales.projection.SalesPeriodProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SalesReportRepository extends Repository<Sale, Long> {

    @Query(nativeQuery = true, value = """
            SELECT
                DATE(s.date_sale) AS period,
                COALESCE(SUM(s.total_value), 0) AS totalRevenue,
                COUNT(s.id) AS totalSales,
                COALESCE(AVG(s.total_value), 0) AS averageTicket,
                COALESCE(SUM(s.quantity), 0) AS totalItemsSold
            FROM sales s
            WHERE s.status = 'APPROVED'
              AND s.date_sale BETWEEN :startDate AND :endDate
            GROUP BY DATE(s.date_sale)
            ORDER BY period
            """)
    List<SalesPeriodProjection> findSalesGroupedByDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(nativeQuery = true, value = """
            SELECT
                DATE(DATE_SUB(s.date_sale, INTERVAL WEEKDAY(s.date_sale) DAY)) AS period,
                COALESCE(SUM(s.total_value), 0) AS totalRevenue,
                COUNT(s.id) AS totalSales,
                COALESCE(AVG(s.total_value), 0) AS averageTicket,
                COALESCE(SUM(s.quantity), 0) AS totalItemsSold
            FROM sales s
            WHERE s.status = 'APPROVED'
              AND s.date_sale BETWEEN :startDate AND :endDate
            GROUP BY DATE(DATE_SUB(s.date_sale, INTERVAL WEEKDAY(s.date_sale) DAY))
            ORDER BY period
            """)
    List<SalesPeriodProjection> findSalesGroupedByWeek(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(nativeQuery = true, value = """
            SELECT
                DATE_FORMAT(s.date_sale, '%Y-%m') AS period,
                COALESCE(SUM(s.total_value), 0) AS totalRevenue,
                COUNT(s.id) AS totalSales,
                COALESCE(AVG(s.total_value), 0) AS averageTicket,
                COALESCE(SUM(s.quantity), 0) AS totalItemsSold
            FROM sales s
            WHERE s.status = 'APPROVED'
              AND s.date_sale BETWEEN :startDate AND :endDate
            GROUP BY DATE_FORMAT(s.date_sale, '%Y-%m')
            ORDER BY period
            """)
    List<SalesPeriodProjection> findSalesGroupedByMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(nativeQuery = true, value = """
            SELECT
                COALESCE(SUM(s.total_value), 0) AS totalRevenue,
                COUNT(s.id) AS totalSales
            FROM sales s
            WHERE s.status = 'APPROVED'
              AND DATE(s.date_sale) = CURDATE()
            """)
    Object[] findTodaySalesSummary();
}
