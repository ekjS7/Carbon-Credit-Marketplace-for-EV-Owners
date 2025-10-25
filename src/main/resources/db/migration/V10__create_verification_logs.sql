
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

-- Thêm cột verifier_id vào bảng credit_request
ALTER TABLE credit_request
ADD COLUMN IF NOT EXISTS verifier_id BIGINT NULL,
ADD CONSTRAINT fk_credit_request_verifier FOREIGN KEY (verifier_id) REFERENCES users(id);

-- Tạo index để tối ưu truy vấn cho CVA dashboard
CREATE INDEX IF NOT EXISTS idx_credit_request_status ON credit_request(status);
CREATE INDEX IF NOT EXISTS idx_verification_logs_request_id ON verification_logs(request_id);
CREATE INDEX IF NOT EXISTS idx_verification_logs_verifier_id ON verification_logs(verifier_id);
