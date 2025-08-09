#!/bin/bash

# Simple script to run the Ticket Booking System application

# Set colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}   Ticket Booking System Launcher    ${NC}"
echo -e "${BLUE}======================================${NC}"

# Check if the build directory exists
if [ ! -d "build" ]; then
    echo -e "${RED}Error: Build directory not found${NC}"
    echo -e "${YELLOW}Please make sure the application is compiled${NC}"
    exit 1
fi

# Check if the Main class exists
if [ ! -f "build/com/ticketbooking/Main.class" ]; then
    echo -e "${RED}Error: Main class not found${NC}"
    echo -e "${YELLOW}Please make sure the application is compiled${NC}"
    exit 1
fi

# Check if lib directory exists
if [ ! -d "lib" ]; then
    echo -e "${YELLOW}Warning: Library directory not found, application may not run correctly${NC}"
fi

echo -e "${GREEN}Starting Ticket Booking System...${NC}"
echo -e "${BLUE}You can log in with the following credentials:${NC}"
echo -e "${BLUE}  Admin: username=admin, password=admin123${NC}"
echo -e "${BLUE}  Or continue as guest${NC}"
echo ""
echo -e "${YELLOW}Press Ctrl+C to exit the application${NC}"
echo ""

# Run the application
java -cp "build:lib/*" com.ticketbooking.Main

# Check exit code
if [ $? -eq 0 ]; then
    echo -e "${GREEN}Application exited successfully${NC}"
else
    echo -e "${RED}Application exited with an error${NC}"
fi

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}   Thank you for using the system!    ${NC}"
echo -e "${BLUE}======================================${NC}"
