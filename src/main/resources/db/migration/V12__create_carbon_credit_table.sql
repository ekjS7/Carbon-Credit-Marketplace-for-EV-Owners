CREATE TABLE carbon_credit (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               owner_id BIGINT NOT NULL,
                               quantity DECIMAL(19,4) NOT NULL,
                               issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               is_retired BOOLEAN NOT NULL DEFAULT FALSE
);
