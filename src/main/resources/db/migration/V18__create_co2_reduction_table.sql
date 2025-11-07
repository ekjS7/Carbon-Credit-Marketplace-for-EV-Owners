CREATE TABLE co2_reduction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    baseline DECIMAL(18, 4) NOT NULL,
    actual DECIMAL(18, 4) NOT NULL,
    reduction DECIMAL(18, 4) NOT NULL,
    certified BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    version INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;