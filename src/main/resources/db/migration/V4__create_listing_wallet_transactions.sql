-- 🧹 Xoá bảng cũ
DROP TABLE IF EXISTS wallet_transactions;
DROP TABLE IF EXISTS wallet;

-- Wallet table
CREATE TABLE IF NOT EXISTS wallet (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      balance DECIMAL(19,4) DEFAULT 0,
                                      user_id BIGINT NULL,
                                      UNIQUE (user_id),
                                      FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Wallet transactions
CREATE TABLE IF NOT EXISTS wallet_transactions (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   amount DECIMAL(19,4) NOT NULL,
                                                   type VARCHAR(20) NOT NULL,
                                                   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                   wallet_id BIGINT NULL,
                                                   FOREIGN KEY (wallet_id) REFERENCES wallet(id)
);
