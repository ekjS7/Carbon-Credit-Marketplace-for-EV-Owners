-- V6__fix_enum_and_constraints.sql
-- Fix enum types and add constraints for wallet and wallet_transactions

-- Update wallet table
ALTER TABLE wallet
    MODIFY balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    ADD CONSTRAINT uq_wallet_user UNIQUE (user_id);

-- Update wallet_transactions table
ALTER TABLE wallet_transactions
    MODIFY type VARCHAR(20) NOT NULL,
    MODIFY amount DECIMAL(19,4) NOT NULL,
    ADD CONSTRAINT chk_amount_positive CHECK (amount > 0);

-- Update users table to use BigDecimal for carbon_balance
ALTER TABLE users
    MODIFY carbon_balance DECIMAL(19,4) NOT NULL DEFAULT 0;
