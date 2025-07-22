#!/bin/bash

echo "Testing Spring Boot application startup (skipping tests)..."

# Start the application in background
./mvnw spring-boot:run -Dmaven.test.skip=true > startup-skip-tests.log 2>&1 &
APP_PID=$!

echo "Started application with PID: $APP_PID"

# Wait for startup (max 60 seconds)
COUNTER=0
MAX_WAIT=60

while [ $COUNTER -lt $MAX_WAIT ]; do
    if grep -q "Started SpringDroolsIntegrationApplication" startup-skip-tests.log; then
        echo "✅ Application started successfully!"
        
        # Check for errors in the log
        if grep -q "ERROR" startup-skip-tests.log; then
            echo "⚠️  Found errors in startup log:"
            grep "ERROR" startup-skip-tests.log
        else
            echo "✅ No errors found in startup log"
        fi
        
        # Check if application is responding
        sleep 2
        if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
            echo "✅ Application health check passed"
        else
            echo "⚠️  Application health check failed"
        fi
        
        # Kill the application
        kill $APP_PID
        wait $APP_PID 2>/dev/null
        echo "✅ Application stopped successfully"
        exit 0
    fi
    
    if grep -q "APPLICATION FAILED TO START" startup-skip-tests.log; then
        echo "❌ Application failed to start!"
        tail -50 startup-skip-tests.log
        kill $APP_PID 2>/dev/null
        wait $APP_PID 2>/dev/null
        exit 1
    fi
    
    echo "Waiting for startup... ($((COUNTER+1))/$MAX_WAIT)"
    sleep 1
    COUNTER=$((COUNTER+1))
done

echo "❌ Startup timed out after $MAX_WAIT seconds"
echo "Last 50 lines of log:"
tail -50 startup-skip-tests.log

# Kill the application
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
exit 1
