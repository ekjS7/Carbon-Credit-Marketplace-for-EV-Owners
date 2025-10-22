-- V7__create_listing_table.sql
-- Create listings table for carbon credit marketplace

CREATE TABLE listings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    carbon_amount DECIMAL(19,4) NOT NULL,
    price DECIMAL(19,4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    seller_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id),
    INDEX idx_listings_status (status),
    INDEX idx_listings_seller (seller_id),
    INDEX idx_listings_created_at (created_at)
);
