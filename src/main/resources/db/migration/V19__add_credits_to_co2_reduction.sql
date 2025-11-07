-- V19__add_credits_to_co2_reduction.sql
-- Add credits column and increase decimal precision for CO2 reduction
ALTER TABLE co2_reduction
  MODIFY COLUMN baseline DECIMAL(18,6) NOT NULL,
  MODIFY COLUMN actual DECIMAL(18,6) NOT NULL,
  MODIFY COLUMN reduction DECIMAL(18,6) NOT NULL,
  ADD COLUMN credits DECIMAL(18,6) DEFAULT 0;

-- Add index to speed queries by user and time
CREATE INDEX idx_co2_user_created_at ON co2_reduction(user_id, created_at);
