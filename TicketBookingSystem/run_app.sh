#!/bin/bash

# Ticket Booking System Setup and Run Script
# This script will set up the database and run the application

# Set colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# PostgreSQL settings
PSQL="/Library/PostgreSQL/17/bin/psql"
DB_NAME="ticketbooking"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_HOST="localhost"
DB_PORT="5432"

# Function to print section header
print_header() {
    echo -e "\n${BLUE}=== $1 ===${NC}\n"
}

# Function to print success message
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error message
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Function to print warning message
print_warning() {
    echo -e "${YELLOW}! $1${NC}"
}

# Function to check if PostgreSQL is running
check_postgres() {
    print_header "Checking PostgreSQL"

    if ! command -v $PSQL &> /dev/null; then
        print_error "PostgreSQL client not found at $PSQL"
        print_warning "Please install PostgreSQL or update the PSQL path in this script"
        return 1
    fi

    # Try to connect to PostgreSQL
    if $PSQL -h $DB_HOST -p $DB_PORT -U $DB_USER -c "SELECT 1" postgres &> /dev/null; then
        print_success "PostgreSQL is running"
        return 0
    else
        print_error "Could not connect to PostgreSQL"
        print_warning "Please make sure PostgreSQL is running and the credentials are correct"
        return 1
    fi
}

# Function to check if the database exists
check_database() {
    print_header "Checking Database"

    if $PSQL -h $DB_HOST -p $DB_PORT -U $DB_USER -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
        print_success "Database '$DB_NAME' exists"
        return 0
    else
        print_warning "Database '$DB_NAME' does not exist, will create it"
        return 1
    fi
}

# Function to create the database
create_database() {
    print_header "Creating Database"

    if $PSQL -h $DB_HOST -p $DB_PORT -U $DB_USER -c "CREATE DATABASE $DB_NAME" postgres; then
        print_success "Database '$DB_NAME' created successfully"
        return 0
    else
        print_error "Failed to create database '$DB_NAME'"
        return 1
    fi
}

# Function to set up the database schema
setup_database_schema() {
    print_header "Setting up Database Schema"

    if [ -f "setup_database.sql" ]; then
        if $PSQL -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f setup_database.sql; then
            print_success "Database schema set up successfully"
            return 0
        else
            print_error "Failed to set up database schema"
            return 1
        fi
    else
        print_error "setup_database.sql not found"
        return 1
    fi
}

# Function to update database schema with new features
update_database_schema() {
    print_header "Updating Database Schema"

    if [ -f "update_database.sql" ]; then
        if $PSQL -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f update_database.sql; then
            print_success "Database schema updated successfully"
            return 0
        else
            print_warning "Some updates may not have been applied"
            return 0  # Continue anyway
        fi
    else
        print_warning "update_database.sql not found, skipping updates"
        return 0  # Continue anyway
    fi
}

# Function to compile the application
compile_application() {
    print_header "Compiling Application"

    # Check if the build directory already exists and has compiled classes
    if [ -d "build/com/ticketbooking" ]; then
        print_success "Application already compiled"
        return 0
    fi

    # Create build directory if it doesn't exist
    mkdir -p build

    # Check if source directory exists
    if [ ! -d "src/main/java" ]; then
        print_warning "Source directory not found, using pre-compiled classes"

        # Check if we have pre-compiled classes
        if [ -d "build/com/ticketbooking" ]; then
            print_success "Using existing compiled classes"
            return 0
        else
            print_error "No compiled classes found"
            return 1
        fi
    fi

    # Find all Java files
    find src/main/java -name "*.java" > sources.txt

    # Compile the application
    if javac -d build -cp "lib/*:src/main/java:src/main/resources" @sources.txt; then
        print_success "Application compiled successfully"

        # Copy resources
        if [ -d "src/main/resources" ]; then
            cp -r src/main/resources/* build/
            print_success "Resources copied to build directory"
        else
            print_warning "Resources directory not found"
        fi

        return 0
    else
        print_error "Failed to compile application"
        return 1
    fi
}

# Function to run the application
run_application() {
    print_header "Running Application"

    # Check if the build directory exists
    if [ ! -d "build" ]; then
        print_error "Build directory not found"
        return 1
    fi

    # Check if the Main class exists
    if [ ! -f "build/com/ticketbooking/Main.class" ]; then
        print_error "Main class not found"
        return 1
    fi

    # Check if lib directory exists
    if [ ! -d "lib" ]; then
        print_warning "Library directory not found, application may not run correctly"
    fi

    echo "Starting Ticket Booking System..."
    echo "You can log in with the following credentials:"
    echo "  Admin: username=admin, password=admin123"
    echo "  Or continue as guest"
    echo ""
    echo "Press Ctrl+C to exit the application"
    echo ""

    # Run the application
    java -cp "build:lib/*" com.ticketbooking.Main

    # Check exit code
    if [ $? -eq 0 ]; then
        print_success "Application exited successfully"
    else
        print_error "Application exited with an error"
    fi
}

# Main script execution
echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}   Ticket Booking System Launcher    ${NC}"
echo -e "${BLUE}======================================${NC}"

# Check if PostgreSQL is running
if ! check_postgres; then
    echo -e "\n${RED}PostgreSQL is required to run the application.${NC}"
    echo -e "${YELLOW}Please make sure PostgreSQL is running and try again.${NC}"
    exit 1
fi

# Check if database exists
if ! check_database; then
    # Create database
    if ! create_database; then
        echo -e "\n${RED}Failed to create database. Exiting.${NC}"
        exit 1
    fi

    # Set up database schema
    if ! setup_database_schema; then
        echo -e "\n${RED}Failed to set up database schema. Exiting.${NC}"
        exit 1
    fi
else
    # Update database schema
    update_database_schema
fi

# Compile application
if ! compile_application; then
    echo -e "\n${RED}Failed to compile application. Exiting.${NC}"
    exit 1
fi

# Run application
run_application

echo -e "\n${BLUE}======================================${NC}"
echo -e "${BLUE}   Thank you for using the system!    ${NC}"
echo -e "${BLUE}======================================${NC}"
