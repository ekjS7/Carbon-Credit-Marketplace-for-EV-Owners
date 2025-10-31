-- Tạo bảng lưu lịch sử xác minh (Verification Logs)
CREATE TABLE IF NOT EXISTS verification_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NOT NULL,
    verifier_id BIGINT NOT NULL,
    decision VARCHAR(20) NOT NULL,         -- APPROVED / REJECTED
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_verification_request FOREIGN KEY (request_id) REFERENCES credit_request(id),
    CONSTRAINT fk_verification_verifier FOREIGN KEY (verifier_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Kiểm tra và thêm cột verifier_id nếu chưa tồn tại
SET @col_exists := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'credit_request'
      AND COLUMN_NAME = 'verifier_id'
);

SET @sql := IF(@col_exists = 0,
               'ALTER TABLE credit_request ADD COLUMN verifier_id BIGINT NULL;',
               'SELECT "Column already exists"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Thêm khóa ngoại (nếu chưa có)
ALTER TABLE credit_request
  ADD CONSTRAINT fk_credit_request_verifier FOREIGN KEY (verifier_id) REFERENCES users(id);

-- Tạo index
CREATE INDEX idx_credit_request_status ON credit_request(status);
CREATE INDEX idx_verification_logs_request_id ON verification_logs(request_id);
CREATE INDEX idx_verification_logs_verifier_id ON verification_logs(verifier_id);
