package com.api.rest.conveniencestore.reports.stock.repository;

import com.api.rest.conveniencestore.product.model.Product;
import com.api.rest.conveniencestore.reports.stock.projection.StockSummaryProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface StockReportRepository extends Repository<Product, Long> {

    @Query(nativeQuery = true, value = """
            SELECT
                COUNT(*) AS totalProducts,
                SUM(CASE WHEN p.stock_quantity > 0 AND p.stock_quantity < 10 THEN 1 ELSE 0 END) AS lowStockProducts,
                SUM(CASE WHEN p.stock_quantity = 0 THEN 1 ELSE 0 END) AS outOfStockProducts,
                SUM(CASE WHEN p.expiration_date IS NOT NULL
                          AND p.expiration_date >= CURDATE()
                          AND p.expiration_date <= DATE_ADD(CURDATE(), INTERVAL :daysToExpire DAY)
                     THEN 1 ELSE 0 END) AS expiringSoon,
                SUM(CASE WHEN p.expiration_date IS NOT NULL
                          AND p.expiration_date < CURDATE()
                     THEN 1 ELSE 0 END) AS expired
            FROM products p
            WHERE p.status != 'INACTIVE'
            """)
    StockSummaryProjection findStockSummary(@Param("daysToExpire") int daysToExpire);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) FROM products
            WHERE status != 'INACTIVE'
              AND stock_quantity > 0
              AND stock_quantity < 10
            """)
    long countLowStockProducts();
}
