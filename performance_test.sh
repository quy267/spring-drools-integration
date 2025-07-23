#!/bin/bash

# Simple Performance Test Script
# Tests API response times for the three main rule scenarios

echo "=== Performance Test for Spring Drools Integration ==="
echo "Testing API response times (target: < 500ms)"
echo "Date: $(date)"
echo ""

# Test endpoints
CUSTOMER_DISCOUNT_URL="http://localhost:8080/api/v1/discounts/calculate"
LOAN_APPROVAL_URL="http://localhost:8080/api/v1/loans/evaluate"
PRODUCT_RECOMMENDATION_URL="http://localhost:8080/api/v1/recommendations"

# Test data
CUSTOMER_DISCOUNT_DATA='{
  "customer": {
    "id": 1,
    "name": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD"
  },
  "order": {
    "id": 1,
    "totalAmount": 1500.00,
    "itemCount": 5
  }
}'

LOAN_APPROVAL_DATA='{
  "applicant": {
    "id": 1,
    "name": "Jane Smith",
    "age": 30,
    "income": 75000,
    "employmentStatus": "EMPLOYED"
  },
  "loanApplication": {
    "id": 1,
    "amount": 250000,
    "term": 30,
    "purpose": "HOME_PURCHASE"
  },
  "creditScore": {
    "score": 750,
    "provider": "EXPERIAN"
  }
}'

PRODUCT_RECOMMENDATION_DATA='{
  "customer": {
    "id": 1,
    "name": "Bob Johnson",
    "age": 28,
    "preferences": ["ELECTRONICS", "BOOKS"]
  },
  "purchaseHistory": [
    {
      "productId": 1,
      "category": "ELECTRONICS",
      "purchaseDate": "2024-01-15",
      "amount": 299.99
    }
  ]
}'

# Function to test endpoint performance
test_endpoint() {
    local name="$1"
    local url="$2"
    local data="$3"
    local total_time=0
    local success_count=0
    local iterations=10
    
    echo "Testing $name..."
    echo "URL: $url"
    
    for i in $(seq 1 $iterations); do
        # Measure response time using curl
        response_time=$(curl -s -w "%{time_total}" -o /dev/null \
            -H "Content-Type: application/json" \
            -X POST \
            -d "$data" \
            "$url")
        
        # Check if request was successful
        if [ $? -eq 0 ]; then
            # Convert to milliseconds (multiply by 1000)
            response_time_ms=$(awk "BEGIN {printf \"%.0f\", $response_time * 1000}")
            total_time=$(awk "BEGIN {printf \"%.0f\", $total_time + $response_time_ms}")
            success_count=$((success_count + 1))
            printf "  Request %2d: %s ms\n" $i $response_time_ms
        else
            echo "  Request $i: FAILED"
        fi
    done
    
    if [ $success_count -gt 0 ]; then
        avg_time=$(awk "BEGIN {printf \"%.0f\", $total_time / $success_count}")
        echo "  Average response time: ${avg_time} ms"
        echo "  Success rate: $success_count/$iterations"
        
        # Check if meets 500ms requirement
        if [ $avg_time -lt 500 ]; then
            echo "  ✅ PASS - Under 500ms requirement"
        else
            echo "  ❌ FAIL - Exceeds 500ms requirement"
        fi
    else
        echo "  ❌ All requests failed"
    fi
    
    echo ""
}

# Run tests
test_endpoint "Customer Discount Rules" "$CUSTOMER_DISCOUNT_URL" "$CUSTOMER_DISCOUNT_DATA"
test_endpoint "Loan Approval Rules" "$LOAN_APPROVAL_URL" "$LOAN_APPROVAL_DATA"
test_endpoint "Product Recommendation Rules" "$PRODUCT_RECOMMENDATION_URL" "$PRODUCT_RECOMMENDATION_DATA"

echo "=== Performance Test Complete ==="