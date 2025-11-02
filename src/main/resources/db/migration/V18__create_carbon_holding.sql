CREATE TABLE carbon_holding (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                carbon_wallet_id BIGINT NOT NULL,
                                credit_id BIGINT NOT NULL,

                                quantity INT NOT NULL,

                                acquired_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_carbon_holding_wallet
                                    FOREIGN KEY (carbon_wallet_id)
                                        REFERENCES carbon_wallet(id),

                                CONSTRAINT fk_carbon_holding_credit
                                    FOREIGN KEY (credit_id)
                                        REFERENCES carbon_credit(id)
);
