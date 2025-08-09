-- Update users table to add new fields for user registration and profile

-- Check if users table exists
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = 'users') THEN
        -- Create users table if it doesn't exist
        CREATE TABLE users (
            id SERIAL PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            password VARCHAR(100) NOT NULL,
            role VARCHAR(20) NOT NULL,
            email VARCHAR(100) NOT NULL UNIQUE,
            full_name VARCHAR(100),
            phone VARCHAR(20),
            registration_date TIMESTAMP,
            last_login_date TIMESTAMP
        );
        
        -- Insert admin user
        INSERT INTO users (username, password, role, email, full_name, registration_date)
        VALUES ('admin', 'admin123', 'admin', 'admin@example.com', 'System Administrator', NOW());
    ELSE
        -- Add new columns if they don't exist
        BEGIN
            ALTER TABLE users ADD COLUMN full_name VARCHAR(100);
        EXCEPTION
            WHEN duplicate_column THEN
                RAISE NOTICE 'Column full_name already exists in users table';
        END;
        
        BEGIN
            ALTER TABLE users ADD COLUMN phone VARCHAR(20);
        EXCEPTION
            WHEN duplicate_column THEN
                RAISE NOTICE 'Column phone already exists in users table';
        END;
        
        BEGIN
            ALTER TABLE users ADD COLUMN registration_date TIMESTAMP;
        EXCEPTION
            WHEN duplicate_column THEN
                RAISE NOTICE 'Column registration_date already exists in users table';
        END;
        
        BEGIN
            ALTER TABLE users ADD COLUMN last_login_date TIMESTAMP;
        EXCEPTION
            WHEN duplicate_column THEN
                RAISE NOTICE 'Column last_login_date already exists in users table';
        END;
        
        -- Update existing users with registration date if it's NULL
        UPDATE users SET registration_date = NOW() WHERE registration_date IS NULL;
    END IF;
END
$$;
