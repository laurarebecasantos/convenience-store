ALTER TABLE products ADD CONSTRAINT chk_stock_not_negative CHECK (stock_quantity >= 0);

CREATE INDEX idx_sales_payment_method ON sales(payment_method);
CREATE INDEX idx_loyalty_transactions_client ON loyalty_transactions(client_id);
CREATE INDEX idx_loyalty_points_client ON loyalty_points(client_id);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
