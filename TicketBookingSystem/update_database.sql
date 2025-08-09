-- Update database schema for new features

-- Add verification fields to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_expiry TIMESTAMP;

-- Update password field to accommodate longer hashed passwords
ALTER TABLE users ALTER COLUMN password TYPE VARCHAR(255);

-- Add last_login_date if it doesn't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_date TIMESTAMP;

-- Add full_name and phone if they don't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- Add registration_date if it doesn't exist
ALTER TABLE users ADD COLUMN IF NOT EXISTS registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Update existing admin user with hashed password (admin123)
-- This will be replaced with actual hashed password in the application
UPDATE users SET 
    password = '10000:SGVsbG9Xb3JsZCEhISEh:2jmj7l5rSw0yVb/vlWAYkK/YBwk=',
    verified = TRUE
WHERE username = 'admin';

-- Add reporting tables
CREATE TABLE IF NOT EXISTS user_activity (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    activity_type VARCHAR(50) NOT NULL,
    activity_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

CREATE TABLE IF NOT EXISTS payment_transactions (
    id SERIAL PRIMARY KEY,
    booking_id INTEGER REFERENCES bookings(id),
    amount NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(100),
    status VARCHAR(20),
    payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for new tables
CREATE INDEX IF NOT EXISTS idx_user_activity_user ON user_activity(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_booking ON payment_transactions(booking_id);
