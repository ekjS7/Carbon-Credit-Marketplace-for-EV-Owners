-- V13__create_order_transaction_table.sql
-- Tạo bảng order_transactions

CREATE TABLE order_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT,                                        -- người mua
    owner_id BIGINT,                                        -- người bán (chủ tín chỉ)
    credits_amount DECIMAL(19,6) NOT NULL,                  -- số tín chỉ
    status VARCHAR(40) NOT NULL DEFAULT 'CREATED',          -- trạng thái giao dịch
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,         -- thời điểm tạo
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP, -- cập nhật gần nhất
    CONSTRAINT fk_order_transactions_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),
    CONSTRAINT fk_order_transactions_owner FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
