#!/bin/bash

# Make sure we're in the correct directory
cd "$(dirname "$0")"

# Print current directory for debugging
echo "Current directory: $(pwd)"

# List build directory contents
echo "Build directory contents:"
ls -la build/com/ticketbooking/

# Set the classpath explicitly
export CLASSPATH="./build:./lib/*"

# Run the application
echo "Launching application..."
java -cp "$CLASSPATH" com.ticketbooking.Main

# Check exit code
if [ $? -ne 0 ]; then
    echo "Error launching application"
    exit 1
fi
