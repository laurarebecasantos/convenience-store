-- Indexes to optimize report queries

-- Sales report: filtering by status + date_sale, used in all sales report groupings
CREATE INDEX idx_sales_status_date ON sales (status, date_sale);

-- Sales report: dashboard query filtering by date only
CREATE INDEX idx_sales_date_sale ON sales (date_sale);

-- Sales report: discount aggregation for loyalty report
CREATE INDEX idx_sales_status_discount ON sales (status, discount);

-- Loyalty transactions: aggregation by type
CREATE INDEX idx_loyalty_transactions_type ON loyalty_transactions (type);

-- Loyalty transactions: dashboard query filtering by type + date
CREATE INDEX idx_loyalty_transactions_type_created ON loyalty_transactions (type, created_at);

-- Loyalty points: active clients query
CREATE INDEX idx_loyalty_points_remaining_expiration ON loyalty_points (remaining_points, expiration_date);

-- Products: stock report filtering by status
CREATE INDEX idx_products_status_stock ON products (status, stock_quantity);

-- Products: expiration report queries
CREATE INDEX idx_products_status_expiration ON products (status, expiration_date);
