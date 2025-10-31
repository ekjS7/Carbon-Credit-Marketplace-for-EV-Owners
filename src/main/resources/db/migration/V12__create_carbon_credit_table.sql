-- V12__create_carbon_credit_table.sql
-- Tạo bảng carbon_credits

CREATE TABLE carbon_credits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,               -- userId từ bảng users
    amount DOUBLE NOT NULL,                 -- số lượng tín chỉ
    source VARCHAR(255),                    -- nguồn gốc (VD: Request#id)
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- ngày phát hành
    listed BOOLEAN NOT NULL DEFAULT FALSE,  -- đã niêm yết chưa
    CONSTRAINT fk_carbon_credits_owner FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
