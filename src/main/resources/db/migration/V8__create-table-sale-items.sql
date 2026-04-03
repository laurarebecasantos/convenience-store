CREATE TABLE sale_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT fk_sale_items_sale FOREIGN KEY (sale_id) REFERENCES sales(id)
);
