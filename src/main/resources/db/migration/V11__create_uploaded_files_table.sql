-- Tạo bảng lưu file / video minh chứng
CREATE TABLE IF NOT EXISTS uploaded_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id BIGINT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    size BIGINT,
    file_path VARCHAR(500) NOT NULL,
    uploaded_by VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_uploaded_files_request FOREIGN KEY (request_id) REFERENCES credit_request(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tạo index hỗ trợ truy vấn nhanh theo request_id
CREATE INDEX idx_uploaded_files_request_id ON uploaded_files(request_id);
