ALTER TABLE clients ADD COLUMN points_balance INT NOT NULL DEFAULT 0;

ALTER TABLE sales ADD COLUMN points_earned INT NOT NULL DEFAULT 0;
ALTER TABLE sales ADD COLUMN points_used INT NOT NULL DEFAULT 0;
ALTER TABLE sales ADD COLUMN discount DECIMAL(10,2) NOT NULL DEFAULT 0.00;

CREATE TABLE loyalty_points (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    points INT NOT NULL,
    remaining_points INT NOT NULL,
    expiration_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_loyalty_points_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE loyalty_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_id BIGINT NOT NULL,
    points INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    reference_id BIGINT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_loyalty_transactions_client FOREIGN KEY (client_id) REFERENCES clients(id)
);
