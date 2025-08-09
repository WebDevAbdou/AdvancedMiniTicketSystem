-- Create database
CREATE DATABASE ticketbooking;

-- Connect to the database
\c ticketbooking

-- Create tables
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    email VARCHAR(100) UNIQUE,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP,
    CONSTRAINT check_role CHECK (role IN ('user', 'admin'))
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    time TIME NOT NULL,
    venue VARCHAR(100) NOT NULL,
    total_seats INTEGER NOT NULL,
    available_seats INTEGER NOT NULL,
    base_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_seats CHECK (available_seats <= total_seats)
);

CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100),
    customer_phone VARCHAR(20),
    event_id INTEGER NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    seat_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_quantity CHECK (quantity > 0)
);

-- Create indexes
CREATE INDEX idx_event_date ON events(date);
CREATE INDEX idx_bookings_event ON bookings(event_id);

-- Insert sample admin user
INSERT INTO users (username, password, role, email, full_name, registration_date)
VALUES ('admin', 'admin123', 'admin', 'admin@ticketbooking.com', 'System Administrator', CURRENT_TIMESTAMP);

-- Insert sample events
INSERT INTO events (name, description, date, time, venue, total_seats, available_seats, base_price)
VALUES
('Summer Music Festival', 'Annual music festival featuring top artists', '2023-07-15', '18:00:00', 'Central Park', 1000, 1000, 50.00),
('Comedy Night', 'Stand-up comedy show with famous comedians', '2023-06-30', '20:00:00', 'Laugh Factory', 200, 200, 25.00),
('Tech Conference', 'Conference for software developers and tech enthusiasts', '2023-08-10', '09:00:00', 'Convention Center', 500, 500, 75.00);
