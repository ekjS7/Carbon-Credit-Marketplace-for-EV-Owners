-- Add password column to users. Set default empty string so existing rows are valid.
ALTER TABLE users
  ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '';
