# Running the Ticket Booking System

This document provides instructions on how to run the Ticket Booking System application.

## Prerequisites

- Java Development Kit (JDK) 17 or higher
- PostgreSQL 12 or higher (installed and running)

## Quick Start

The easiest way to run the application is using the provided script:

1. Open a terminal
2. Navigate to the TicketBookingSystem directory
3. Run the script:
   ```
   ./start.sh
   ```

This script will:
- Check if the application is compiled
- Run the application

If you need to set up the database first, you can use the `run_app.sh` script:
   ```
   ./run_app.sh
   ```

This script will:
- Check if PostgreSQL is running
- Create the database if it doesn't exist
- Set up the database schema
- Compile the application
- Run the application

## Manual Setup and Running

If you prefer to set up and run the application manually, follow these steps:

### 1. Set up the database

```bash
# Create the database
/Library/PostgreSQL/17/bin/psql -U postgres -c "CREATE DATABASE ticketbooking"

# Set up the database schema
/Library/PostgreSQL/17/bin/psql -U postgres -d ticketbooking -f setup_database.sql

# Update the database schema (optional)
/Library/PostgreSQL/17/bin/psql -U postgres -d ticketbooking -f update_database.sql
```

### 2. Compile the application

```bash
# Create build directory
mkdir -p build

# Find all Java files
find src/main/java -name "*.java" > sources.txt

# Compile the application
javac -d build -cp "lib/*:src/main/java:src/main/resources" @sources.txt

# Copy resources
cp -r src/main/resources/* build/
```

### 3. Run the application

```bash
java -cp "build:lib/*" com.ticketbooking.Main
```

## Troubleshooting

### Database Connection Issues

If you encounter database connection issues:

1. Make sure PostgreSQL is running
2. Check the database connection properties in `src/main/resources/database.properties`
3. Ensure the database user has the necessary permissions

### Compilation Issues

If you encounter compilation issues:

1. Make sure JDK is installed and properly configured
2. Check that all required libraries are in the `lib` directory
3. Ensure the source code is complete and valid

### Runtime Issues

If you encounter runtime issues:

1. Check the console output for error messages
2. Verify that the database is properly set up
3. Ensure all required resources are available

## Login Information

- **Admin Login**: Username: `admin`, Password: `admin123`
- **Guest Access**: Click "Continue as Guest" to browse events without logging in
- **User Registration**: Click "Register" to create a new user account
