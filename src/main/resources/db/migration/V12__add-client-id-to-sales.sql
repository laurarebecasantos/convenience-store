ALTER TABLE sales ADD COLUMN client_id BIGINT;

UPDATE sales s
    JOIN clients c ON s.description LIKE CONCAT('%', c.cpf, '%')
    SET s.client_id = c.id;

ALTER TABLE sales ADD CONSTRAINT fk_sales_client FOREIGN KEY (client_id) REFERENCES clients(id);
