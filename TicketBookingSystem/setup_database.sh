#!/bin/bash

# Use the full path to psql
PSQL="/Library/PostgreSQL/17/bin/psql"

# Check if psql is available
if [ ! -f "$PSQL" ]; then
    echo "PostgreSQL client (psql) not found at $PSQL. Please make sure PostgreSQL is installed."
    exit 1
fi

# Run the SQL script
echo "Setting up the ticketbooking database..."
"$PSQL" -U postgres -f setup_database.sql

# Check if the database was created successfully
if [ $? -eq 0 ]; then
    echo "Database setup completed successfully!"
else
    echo "Failed to set up the database. Please check the error messages above."
    exit 1
fi

echo "You can now run the Ticket Booking System application."
