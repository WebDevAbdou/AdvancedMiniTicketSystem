#!/bin/bash

# Set colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================${NC}"
echo -e "${BLUE}   Ticket Booking System Launcher    ${NC}"
echo -e "${BLUE}======================================${NC}"

# Compile the application
echo -e "${BLUE}Compiling application...${NC}"
mkdir -p build
find src/main/java -name "*.java" > sources.txt
javac -d build -cp "lib/*:src/main/java:src/main/resources" @sources.txt
if [ $? -ne 0 ]; then
    echo -e "${RED}Compilation failed.${NC}"
    exit 1
fi

# Copy resources
echo -e "${BLUE}Copying resources...${NC}"
cp -r src/main/resources/* build/

# Run the application
echo -e "${GREEN}Starting Ticket Booking System...${NC}"
echo -e "${BLUE}You can log in with the following credentials:${NC}"
echo -e "${BLUE}  Admin: username=admin, password=admin123${NC}"
echo -e "${BLUE}  Or continue as guest${NC}"
echo ""

# Run the application with explicit classpath
cd build
java -cp ".:../lib/*" com.ticketbooking.Main
