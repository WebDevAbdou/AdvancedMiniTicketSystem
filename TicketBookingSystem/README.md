# Smart Ticket Booking System Pro

A modular, user-friendly, and database-backed desktop application to manage real-time ticket bookings with complete GUI, validation, persistence, and future extensibility.

## Features

- **User-friendly GUI**: Intuitive interface for browsing events and booking tickets
- **Real-time seat availability**: Track and update seat availability in real-time
- **Multiple seat types**: Support for Standard, VIP, and Premium seat types with different pricing
- **Admin panel**: Manage events, view bookings, and generate reports
- **Data persistence**: Store all data in PostgreSQL database
- **Validation**: Comprehensive input validation for all forms
- **Logging**: Detailed application logging for troubleshooting
- **Error handling**: Robust error handling throughout the application

## Technology Stack

- **Language**: Java SE 22
- **GUI**: Swing
- **Database**: PostgreSQL
- **Logging**: Log4j2
- **Testing**: JUnit 5

## System Architecture

The application follows the MVC (Model-View-Controller) + DAO (Data Access Object) pattern:

- **Model**: Represents the data and business logic
  - `Event`: Represents an event with details like name, date, venue, etc.
  - `Booking`: Represents a ticket booking for an event
  - `User`: Represents a user of the system (admin or regular user)

- **View**: Represents the user interface
  - `MainFrame`: Main application window
  - `LoginPanel`: Panel for user login
  - `HomePanel`: Panel for displaying available events
  - `BookingPanel`: Panel for booking tickets
  - `AdminPanel`: Panel for admin functions

- **Controller**: Handles user input and updates the model and view
  - `EventController`: Manages event-related operations
  - `BookingController`: Manages booking-related operations
  - `UserController`: Manages user-related operations

- **DAO**: Handles database operations
  - `EventDAO`: Handles database operations for events
  - `BookingDAO`: Handles database operations for bookings
  - `UserDAO`: Handles database operations for users

## Database Schema

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
```

## Setup Instructions

### Prerequisites

- Java SE Development Kit (JDK) 22
- PostgreSQL 12 or higher
- Maven (optional, for building from source)

### Database Setup

1. Install PostgreSQL if not already installed
2. Create a new database named `ticketbooking`
3. Execute the SQL script in `src/main/resources/database.sql` to create the necessary tables and sample data

### Configuration

1. Edit the database connection properties in `src/main/resources/database.properties` to match your PostgreSQL setup:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/ticketbooking
   db.username=your_username
   db.password=your_password
   ```

### Running the Application

#### From JAR file

1. Download the latest release JAR file
2. Run the application using the command:
   ```
   java -jar TicketBookingSystem.jar
   ```

#### From Source

1. Clone the repository
2. Build the project using Maven:
   ```
   mvn clean package
   ```
3. Run the application:
   ```
   java -jar target/TicketBookingSystem-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Usage

### User Guide

1. **Login**: Use the login screen to log in as an admin or continue as a guest
2. **Browse Events**: View available events on the home screen
3. **Book Tickets**: Select an event and click "Book Tickets" to make a booking
4. **Admin Functions**: If logged in as an admin, access the admin panel to manage events and view bookings

### Default Admin Account

- Username: admin
- Password: admin123

## Testing

Run the unit tests using Maven:
```
mvn test
```

## Future Enhancements

- Role-based login (admin/user)
- Email confirmation for ticket (SMTP setup)
- PDF generation of receipt (using iText)
- Seat map GUI with availability status
- REST API support (for mobile/web expansion)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Java Swing for the GUI components
- PostgreSQL for the database
- Log4j2 for logging
- JUnit for testing
