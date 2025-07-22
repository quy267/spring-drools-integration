#!/bin/bash

echo "Testing Spring Boot application startup..."

# Start the application in the background
./mvnw spring-boot:run > startup.log 2>&1 &
APP_PID=$!

echo "Started application with PID: $APP_PID"

# Wait for startup (check for successful startup message)
for i in {1..30}; do
    if grep -q "Started SpringDroolsIntegrationApplication" startup.log; then
        echo "✅ Application started successfully!"
        echo "Log output:"
        tail -20 startup.log
        kill $APP_PID
        exit 0
    fi
    
    if grep -q "Error" startup.log; then
        echo "❌ Application failed to start!"
        echo "Error log:"
        cat startup.log
        kill $APP_PID
        exit 1
    fi
    
    echo "Waiting for startup... ($i/30)"
    sleep 2
done

echo "❌ Timeout waiting for application startup"
echo "Log output:"
cat startup.log
kill $APP_PID
exit 1
