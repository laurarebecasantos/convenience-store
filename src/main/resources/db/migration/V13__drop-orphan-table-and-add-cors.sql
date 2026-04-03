DROP TABLE IF EXISTS sales_products;

ALTER TABLE sale_items ADD CONSTRAINT fk_sale_items_product FOREIGN KEY (product_id) REFERENCES products(id);
