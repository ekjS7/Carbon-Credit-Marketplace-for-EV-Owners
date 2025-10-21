CREATE TABLE co2_reduction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255),
    baseline DOUBLE,
    actual DOUBLE,
    reduction DOUBLE,
    certified BOOLEAN,
    status VARCHAR(255)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


