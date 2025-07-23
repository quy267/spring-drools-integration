# cURL Examples

This directory contains comprehensive cURL examples for testing and interacting with the Spring Boot Drools Integration APIs from the command line.

## 📋 Overview

These cURL examples demonstrate:
- All major API endpoints
- Authentication methods
- Request/response formats
- Error handling scenarios
- Batch processing
- File uploads

## 🚀 Prerequisites

- cURL installed on your system
- Spring Boot Drools Integration application running on `http://localhost:8080`
- Default credentials: `admin` / `admin123`

## 🔧 Basic Setup

### Environment Variables

Set up environment variables for easier testing:

```bash
export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_USERNAME="admin"
export DROOLS_PASSWORD="admin123"
export DROOLS_AUTH="Basic $(echo -n $DROOLS_USERNAME:$DROOLS_PASSWORD | base64)"
```

### Test Connection

```bash
# Test if the application is running
curl -f $DROOLS_BASE_URL/actuator/health
```

## 📊 Customer Discount APIs

### Calculate Customer Discount

```bash
# Basic discount calculation
curl -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5,
    "membershipYears": 3,
    "previousOrders": 25
  }' | jq '.'
```

### Batch Discount Calculation

```bash
# Calculate discounts for multiple customers
curl -X POST "$DROOLS_BASE_URL/api/v1/discounts/batch?page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '[
    {
      "customerId": "CUST001",
      "customerName": "John Doe",
      "age": 35,
      "loyaltyTier": "GOLD",
      "orderAmount": 1000.00,
      "orderItems": 5
    },
    {
      "customerId": "CUST002",
      "customerName": "Jane Smith",
      "age": 28,
      "loyaltyTier": "SILVER",
      "orderAmount": 500.00,
      "orderItems": 3
    },
    {
      "customerId": "CUST003",
      "customerName": "Bob Johnson",
      "age": 65,
      "loyaltyTier": "BRONZE",
      "orderAmount": 200.00,
      "orderItems": 2
    }
  ]' | jq '.'
```

### Async Discount Calculation

```bash
# Initiate async discount calculation
curl -X POST $DROOLS_BASE_URL/api/v1/discounts/async \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

### Get Discount Statistics

```bash
# Retrieve discount statistics
curl -X GET $DROOLS_BASE_URL/api/v1/discounts/statistics \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

## 🏦 Loan Approval APIs

### Evaluate Loan Application

```bash
# Basic loan evaluation
curl -X POST $DROOLS_BASE_URL/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "applicantId": "APP001",
    "applicantName": "John Doe",
    "age": 35,
    "creditScore": 750,
    "annualIncome": 75000.00,
    "loanAmount": 250000.00,
    "employmentYears": 5,
    "existingDebts": 15000.00,
    "loanPurpose": "HOME_PURCHASE",
    "downPayment": 50000.00
  }' | jq '.'
```

### Batch Loan Evaluation

```bash
# Evaluate multiple loan applications
curl -X POST "$DROOLS_BASE_URL/api/v1/loan-approval/batch?page=0&size=5" \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '[
    {
      "applicantId": "APP001",
      "applicantName": "John Doe",
      "age": 35,
      "creditScore": 750,
      "annualIncome": 75000.00,
      "loanAmount": 250000.00,
      "employmentYears": 5
    },
    {
      "applicantId": "APP002",
      "applicantName": "Jane Smith",
      "age": 28,
      "creditScore": 680,
      "annualIncome": 55000.00,
      "loanAmount": 180000.00,
      "employmentYears": 3
    }
  ]' | jq '.'
```

### Get Loan Approval Statistics

```bash
# Retrieve loan approval statistics
curl -X GET $DROOLS_BASE_URL/api/v1/loan-approval/statistics \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

## 🛍️ Product Recommendation APIs

### Get Product Recommendations

```bash
# Basic product recommendations
curl -X POST $DROOLS_BASE_URL/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "preferences": ["ELECTRONICS", "BOOKS", "SPORTS"],
    "purchaseHistory": ["LAPTOP", "SMARTPHONE", "TABLET"],
    "budget": 500.00,
    "location": "US",
    "seasonalPreferences": ["SUMMER"]
  }' | jq '.'
```

### Batch Product Recommendations

```bash
# Get recommendations for multiple customers
curl -X POST "$DROOLS_BASE_URL/api/v1/product-recommendation/batch?page=0&size=5" \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '[
    {
      "customerId": "CUST001",
      "customerName": "John Doe",
      "age": 35,
      "preferences": ["ELECTRONICS"],
      "budget": 500.00
    },
    {
      "customerId": "CUST002",
      "customerName": "Jane Smith",
      "age": 28,
      "preferences": ["BOOKS", "FASHION"],
      "budget": 300.00
    }
  ]' | jq '.'
```

### Get Recommendation Statistics

```bash
# Retrieve recommendation statistics
curl -X GET $DROOLS_BASE_URL/api/v1/product-recommendation/statistics \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

## ⚙️ Generic Rule Execution APIs

### Execute Rules

```bash
# Generic rule execution
curl -X POST $DROOLS_BASE_URL/api/v1/rules/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "rulePackage": "customer-discount",
    "facts": [
      {
        "factType": "Customer",
        "data": {
          "customerId": "CUST001",
          "age": 35,
          "loyaltyTier": "GOLD"
        }
      },
      {
        "factType": "Order",
        "data": {
          "orderId": "ORD001",
          "amount": 1000.00,
          "items": 5
        }
      }
    ],
    "parameters": {
      "maxExecutionTime": 5000,
      "enableLogging": true
    }
  }' | jq '.'
```

### Batch Rule Execution

```bash
# Execute rules for multiple fact sets
curl -X POST "$DROOLS_BASE_URL/api/v1/rules/batch?page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '[
    {
      "rulePackage": "customer-discount",
      "facts": [
        {
          "factType": "Customer",
          "data": {"customerId": "CUST001", "age": 35, "loyaltyTier": "GOLD"}
        }
      ]
    },
    {
      "rulePackage": "loan-approval",
      "facts": [
        {
          "factType": "LoanApplication",
          "data": {"applicantId": "APP001", "creditScore": 750, "loanAmount": 250000}
        }
      ]
    }
  ]' | jq '.'
```

### Get Rule Metadata

```bash
# Get metadata about available rule packages
curl -X GET $DROOLS_BASE_URL/api/v1/rules/metadata \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

## 🔧 Rule Management APIs

### Upload Decision Table

```bash
# Upload a decision table file
curl -X POST $DROOLS_BASE_URL/api/v1/rules/upload \
  -H "Authorization: $DROOLS_AUTH" \
  -F "file=@/path/to/CustomerDiscountRules.xlsx" \
  -F "rulePackage=customer-discount" \
  -F "description=Updated customer discount rules" | jq '.'
```

### Validate Rules

```bash
# Validate all loaded rules
curl -X PUT $DROOLS_BASE_URL/api/v1/rules/validate \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

### Reload Rules

```bash
# Reload all rules from configured sources
curl -X POST $DROOLS_BASE_URL/api/v1/rules/reload \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

### Get Rule Engine Status

```bash
# Get current status of the rule engine
curl -X GET $DROOLS_BASE_URL/api/v1/rules/status \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

## 📊 Monitoring and Health APIs

### Application Health

```bash
# Basic health check
curl -X GET $DROOLS_BASE_URL/actuator/health | jq '.'

# Detailed health check (requires authentication)
curl -X GET $DROOLS_BASE_URL/actuator/health \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

### Application Info

```bash
# Get application information
curl -X GET $DROOLS_BASE_URL/actuator/info | jq '.'
```

### Metrics

```bash
# Get application metrics
curl -X GET $DROOLS_BASE_URL/actuator/metrics \
  -H "Authorization: $DROOLS_AUTH" | jq '.'

# Get specific metrics
curl -X GET $DROOLS_BASE_URL/actuator/metrics/rule.execution.time \
  -H "Authorization: $DROOLS_AUTH" | jq '.'

curl -X GET $DROOLS_BASE_URL/actuator/metrics/jvm.memory.used \
  -H "Authorization: $DROOLS_AUTH" | jq '.'
```

### Prometheus Metrics

```bash
# Get metrics in Prometheus format
curl -X GET $DROOLS_BASE_URL/actuator/prometheus \
  -H "Authorization: $DROOLS_AUTH"
```

## 🧪 Testing Scenarios

### Test Script: Complete API Workflow

```bash
#!/bin/bash
# complete_workflow_test.sh

set -e  # Exit on any error

echo "=== Spring Boot Drools Integration API Test ==="

# Set up environment
export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_USERNAME="admin"
export DROOLS_PASSWORD="admin123"
export DROOLS_AUTH="Basic $(echo -n $DROOLS_USERNAME:$DROOLS_PASSWORD | base64)"

echo "1. Testing application health..."
curl -f $DROOLS_BASE_URL/actuator/health > /dev/null
echo "✓ Application is healthy"

echo "2. Testing customer discount calculation..."
DISCOUNT_RESPONSE=$(curl -s -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }')

DISCOUNT_PERCENTAGE=$(echo $DISCOUNT_RESPONSE | jq -r '.discountPercentage')
echo "✓ Discount calculated: $DISCOUNT_PERCENTAGE%"

echo "3. Testing loan approval evaluation..."
LOAN_RESPONSE=$(curl -s -X POST $DROOLS_BASE_URL/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "applicantId": "APP001",
    "applicantName": "John Doe",
    "age": 35,
    "creditScore": 750,
    "annualIncome": 75000.00,
    "loanAmount": 250000.00,
    "employmentYears": 5
  }')

APPROVAL_STATUS=$(echo $LOAN_RESPONSE | jq -r '.approvalStatus')
echo "✓ Loan evaluation: $APPROVAL_STATUS"

echo "4. Testing product recommendations..."
RECOMMENDATION_RESPONSE=$(curl -s -X POST $DROOLS_BASE_URL/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "preferences": ["ELECTRONICS"],
    "budget": 500.00
  }')

RECOMMENDATION_COUNT=$(echo $RECOMMENDATION_RESPONSE | jq -r '.totalRecommendations')
echo "✓ Product recommendations: $RECOMMENDATION_COUNT items"

echo "5. Testing rule engine status..."
STATUS_RESPONSE=$(curl -s -X GET $DROOLS_BASE_URL/api/v1/rules/status \
  -H "Authorization: $DROOLS_AUTH")

ENGINE_STATUS=$(echo $STATUS_RESPONSE | jq -r '.engineStatus')
echo "✓ Rule engine status: $ENGINE_STATUS"

echo "6. Testing rule validation..."
curl -s -X PUT $DROOLS_BASE_URL/api/v1/rules/validate \
  -H "Authorization: $DROOLS_AUTH" > /dev/null
echo "✓ Rules validated successfully"

echo ""
echo "=== All tests completed successfully! ==="
```

### Performance Test Script

```bash
#!/bin/bash
# performance_test.sh

echo "=== Performance Test ==="

# Set up environment
export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_AUTH="Basic $(echo -n admin:admin123 | base64)"

# Test parameters
CONCURRENT_REQUESTS=10
TOTAL_REQUESTS=100

echo "Testing with $CONCURRENT_REQUESTS concurrent requests, $TOTAL_REQUESTS total requests"

# Create test data file
cat > test_request.json << EOF
{
  "customerId": "PERF_TEST",
  "customerName": "Performance Test",
  "age": 35,
  "loyaltyTier": "GOLD",
  "orderAmount": 1000.00,
  "orderItems": 5
}
EOF

# Run performance test
echo "Starting performance test..."
START_TIME=$(date +%s)

for i in $(seq 1 $TOTAL_REQUESTS); do
  curl -s -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
    -H "Content-Type: application/json" \
    -H "Authorization: $DROOLS_AUTH" \
    -d @test_request.json > /dev/null &
  
  # Limit concurrent requests
  if (( i % CONCURRENT_REQUESTS == 0 )); then
    wait
  fi
done

wait  # Wait for all remaining requests

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))
REQUESTS_PER_SECOND=$((TOTAL_REQUESTS / DURATION))

echo "Performance test completed:"
echo "- Total requests: $TOTAL_REQUESTS"
echo "- Duration: ${DURATION}s"
echo "- Requests per second: $REQUESTS_PER_SECOND"

# Cleanup
rm test_request.json
```

### Error Handling Test Script

```bash
#!/bin/bash
# error_handling_test.sh

echo "=== Error Handling Test ==="

export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_AUTH="Basic $(echo -n admin:admin123 | base64)"

echo "1. Testing authentication error (wrong credentials)..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET $DROOLS_BASE_URL/api/v1/rules/status \
  -H "Authorization: Basic $(echo -n wrong:credentials | base64)")
if [ "$HTTP_CODE" = "401" ]; then
  echo "✓ Authentication error handled correctly (401)"
else
  echo "✗ Expected 401, got $HTTP_CODE"
fi

echo "2. Testing validation error (invalid request data)..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{"customerId": "TEST"}')  # Missing required fields
if [ "$HTTP_CODE" = "400" ]; then
  echo "✓ Validation error handled correctly (400)"
else
  echo "✗ Expected 400, got $HTTP_CODE"
fi

echo "3. Testing not found error..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET $DROOLS_BASE_URL/api/v1/nonexistent \
  -H "Authorization: $DROOLS_AUTH")
if [ "$HTTP_CODE" = "404" ]; then
  echo "✓ Not found error handled correctly (404)"
else
  echo "✗ Expected 404, got $HTTP_CODE"
fi

echo "Error handling tests completed."
```

## 📝 Advanced Examples

### Batch Processing with Error Handling

```bash
#!/bin/bash
# batch_processing_with_error_handling.sh

process_customer_batch() {
  local batch_file=$1
  local batch_id=$2
  
  echo "Processing batch $batch_id..."
  
  response=$(curl -s -w "HTTPSTATUS:%{http_code}" -X POST "$DROOLS_BASE_URL/api/v1/discounts/batch" \
    -H "Content-Type: application/json" \
    -H "Authorization: $DROOLS_AUTH" \
    -d @$batch_file)
  
  http_code=$(echo $response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
  body=$(echo $response | sed -e 's/HTTPSTATUS:.*//g')
  
  if [ "$http_code" = "200" ]; then
    success_count=$(echo $body | jq '.content | length')
    echo "✓ Batch $batch_id processed successfully: $success_count customers"
    return 0
  else
    echo "✗ Batch $batch_id failed with HTTP $http_code"
    echo "Error: $body"
    return 1
  fi
}

# Create sample batch files
cat > batch1.json << 'EOF'
[
  {"customerId": "BATCH1_001", "customerName": "Customer 1", "age": 25, "loyaltyTier": "BRONZE", "orderAmount": 100, "orderItems": 1},
  {"customerId": "BATCH1_002", "customerName": "Customer 2", "age": 35, "loyaltyTier": "SILVER", "orderAmount": 500, "orderItems": 3},
  {"customerId": "BATCH1_003", "customerName": "Customer 3", "age": 45, "loyaltyTier": "GOLD", "orderAmount": 1000, "orderItems": 5}
]
EOF

cat > batch2.json << 'EOF'
[
  {"customerId": "BATCH2_001", "customerName": "Customer 4", "age": 30, "loyaltyTier": "SILVER", "orderAmount": 300, "orderItems": 2},
  {"customerId": "BATCH2_002", "customerName": "Customer 5", "age": 60, "loyaltyTier": "GOLD", "orderAmount": 800, "orderItems": 4}
]
EOF

# Process batches
export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_AUTH="Basic $(echo -n admin:admin123 | base64)"

successful_batches=0
total_batches=2

for batch in batch1.json batch2.json; do
  if process_customer_batch $batch $(basename $batch .json); then
    ((successful_batches++))
  fi
done

echo ""
echo "Batch processing summary:"
echo "- Successful batches: $successful_batches/$total_batches"
echo "- Success rate: $(( successful_batches * 100 / total_batches ))%"

# Cleanup
rm batch1.json batch2.json
```

## 🔍 Debugging and Troubleshooting

### Debug Request/Response

```bash
# Enable verbose output for debugging
curl -v -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "DEBUG_TEST",
    "customerName": "Debug Test",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

### Check Response Headers

```bash
# Include response headers in output
curl -i -X GET $DROOLS_BASE_URL/actuator/health \
  -H "Authorization: $DROOLS_AUTH"
```

### Measure Response Time

```bash
# Measure response time
curl -w "Response time: %{time_total}s\nHTTP code: %{http_code}\n" \
  -o /dev/null -s \
  -X POST $DROOLS_BASE_URL/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: $DROOLS_AUTH" \
  -d '{
    "customerId": "TIMING_TEST",
    "customerName": "Timing Test",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

## 📚 Tips and Best Practices

### 1. Use jq for JSON Processing

```bash
# Install jq for better JSON handling
# Ubuntu/Debian: sudo apt-get install jq
# macOS: brew install jq

# Pretty print JSON response
curl -s $DROOLS_BASE_URL/actuator/health | jq '.'

# Extract specific fields
curl -s $DROOLS_BASE_URL/api/v1/rules/status \
  -H "Authorization: $DROOLS_AUTH" | jq '.engineStatus'
```

### 2. Save Responses for Analysis

```bash
# Save response to file
curl -X GET $DROOLS_BASE_URL/api/v1/rules/status \
  -H "Authorization: $DROOLS_AUTH" \
  -o rule_status.json

# Save with timestamp
curl -X GET $DROOLS_BASE_URL/api/v1/discounts/statistics \
  -H "Authorization: $DROOLS_AUTH" \
  -o "discount_stats_$(date +%Y%m%d_%H%M%S).json"
```

### 3. Environment-Specific Configuration

```bash
# Development environment
export DROOLS_BASE_URL="http://localhost:8080"
export DROOLS_USERNAME="admin"
export DROOLS_PASSWORD="admin123"

# Staging environment
export DROOLS_BASE_URL="https://staging.example.com"
export DROOLS_USERNAME="staging_user"
export DROOLS_PASSWORD="staging_password"

# Production environment
export DROOLS_BASE_URL="https://api.example.com"
export DROOLS_USERNAME="prod_user"
export DROOLS_PASSWORD="$PROD_PASSWORD"  # From secure source
```

---

**These cURL examples provide a comprehensive foundation for testing and integrating with the Drools API from the command line.**