-- V8__create_transaction_table.sql
-- Create transactions table for carbon credit marketplace

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    listing_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id),
    FOREIGN KEY (listing_id) REFERENCES listings(id),
    CHECK (buyer_id <> seller_id),
    INDEX idx_transactions_buyer (buyer_id),
    INDEX idx_transactions_seller (seller_id),
    INDEX idx_transactions_listing (listing_id),
    INDEX idx_transactions_status (status),
    INDEX idx_transactions_created_at (created_at)
);
