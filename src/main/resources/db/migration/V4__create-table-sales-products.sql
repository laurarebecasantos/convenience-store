CREATE TABLE sales_products (
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL, -- Quantidade da venda
    description VARCHAR(100) NOT NULL, -- Descrição da venda
    price DECIMAL(10, 2) NOT NULL, -- Preço do produto no momento da venda
    PRIMARY KEY (sale_id, product_id),
    FOREIGN KEY (sale_id) REFERENCES sales(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

