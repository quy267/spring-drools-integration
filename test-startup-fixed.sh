#!/bin/bash

echo "Testing Spring Boot application startup..."

# Start the application in background
./mvnw spring-boot:run > startup.log 2>&1 &
APP_PID=$!

echo "Started application with PID: $APP_PID"

# Wait for startup (max 60 seconds)
COUNTER=0
MAX_WAIT=60

while [ $COUNTER -lt $MAX_WAIT ]; do
    if grep -q "Started SpringDroolsIntegrationApplication" startup.log; then
        echo "Application started successfully!"
        
        # Check for errors in the log
        if grep -q "ERROR" startup.log; then
            echo "Found errors in startup log:"
            grep "ERROR" startup.log
        else
            echo "No errors found in startup log"
        fi
        
        # Kill the application
        kill $APP_PID
        wait $APP_PID 2>/dev/null
        exit 0
    fi
    
    if grep -q "APPLICATION FAILED TO START" startup.log; then
        echo "Application failed to start!"
        tail -50 startup.log
        kill $APP_PID 2>/dev/null
        wait $APP_PID 2>/dev/null
        exit 1
    fi
    
    echo "Waiting for startup... ($((COUNTER+1))/$MAX_WAIT)"
    sleep 1
    COUNTER=$((COUNTER+1))
done

echo "Startup timed out after $MAX_WAIT seconds"
echo "Last 50 lines of log:"
tail -50 startup.log

# Kill the application
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
exit 1
