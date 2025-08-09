#!/bin/bash

# Make sure we're in the correct directory
cd "$(dirname "$0")"

# Set the classpath
CLASSPATH="./build:./lib/*"

# Run the application
java -cp "$CLASSPATH" com.ticketbooking.Main
