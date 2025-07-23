# API Usage Examples

This document provides comprehensive examples of how to use the Spring Drools Integration API endpoints. All examples include request/response formats and common use cases.

## Table of Contents

1. [Customer Discount API](#customer-discount-api)
2. [Loan Approval API](#loan-approval-api)
3. [Product Recommendation API](#product-recommendation-api)
4. [Rule Management API](#rule-management-api)
5. [Health Check and Monitoring](#health-check-and-monitoring)
6. [Error Handling Examples](#error-handling-examples)

## Customer Discount API

### Calculate Single Customer Discount

**Endpoint:** `POST /api/v1/discounts/calculate`

**Request:**
```json
{
  "customerName": "John Doe",
  "customerAge": 35,
  "loyaltyTier": "GOLD",
  "orderAmount": 250.00,
  "orderQuantity": 5
}
```

**Response:**
```json
{
  "customerName": "John Doe",
  "discountPercentage": 15,
  "discountAmount": 37.50,
  "finalAmount": 212.50,
  "appliedRule": "Gold Tier Discount",
  "executionTime": 45
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "customerAge": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 250.00,
    "orderQuantity": 5
  }'
```

### Calculate Batch Customer Discounts

**Endpoint:** `POST /api/v1/discounts/batch`

**Request:**
```json
[
  {
    "customerName": "Alice Smith",
    "customerAge": 65,
    "loyaltyTier": "GOLD",
    "orderAmount": 150.00,
    "orderQuantity": 3
  },
  {
    "customerName": "Bob Johnson",
    "customerAge": 22,
    "loyaltyTier": "SILVER",
    "orderAmount": 75.00,
    "orderQuantity": 2
  },
  {
    "customerName": "Carol Brown",
    "customerAge": 45,
    "loyaltyTier": "BRONZE",
    "orderAmount": 300.00,
    "orderQuantity": 25
  }
]
```

**Response:**
```json
{
  "content": [
    {
      "customerName": "Alice Smith",
      "discountPercentage": 20,
      "discountAmount": 30.00,
      "finalAmount": 120.00,
      "appliedRule": "Premium Customer Discount",
      "executionTime": 42
    },
    {
      "customerName": "Bob Johnson",
      "discountPercentage": 15,
      "discountAmount": 11.25,
      "finalAmount": 63.75,
      "appliedRule": "Student Discount + Silver Tier",
      "executionTime": 38
    },
    {
      "customerName": "Carol Brown",
      "discountPercentage": 13,
      "discountAmount": 39.00,
      "finalAmount": 261.00,
      "appliedRule": "Bulk Order + Large Order Discount",
      "executionTime": 51
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 3,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Asynchronous Discount Calculation

**Endpoint:** `POST /api/v1/discounts/async`

**Request:**
```json
{
  "customerName": "David Wilson",
  "customerAge": 28,
  "loyaltyTier": "SILVER",
  "orderAmount": 180.00,
  "orderQuantity": 8
}
```

**Response:**
```
HTTP/1.1 202 Accepted
```

### Get Discount Statistics

**Endpoint:** `GET /api/v1/discounts/statistics`

**Response:**
```json
{
  "totalDiscountsCalculated": 1247,
  "averageDiscountPercentage": 12.5,
  "totalSavingsGenerated": 15623.75,
  "ruleExecutionStats": {
    "Gold Tier Discount": 342,
    "Senior Discount": 156,
    "Bulk Order Discount": 89,
    "Student Discount": 234,
    "Large Order Discount": 178
  },
  "averageExecutionTime": 47.3,
  "lastUpdated": "2025-07-23T10:33:00Z"
}
```

## Loan Approval API

### Process Loan Application

**Endpoint:** `POST /api/v1/loans/apply`

**Request:**
```json
{
  "applicantName": "Sarah Johnson",
  "applicantAge": 32,
  "annualIncome": 75000.00,
  "creditScore": 720,
  "employmentStatus": "FULL_TIME",
  "loanAmount": 250000.00,
  "loanPurpose": "HOME_PURCHASE",
  "existingDebts": 15000.00
}
```

**Response:**
```json
{
  "applicantName": "Sarah Johnson",
  "applicationId": "LA-2025-001234",
  "approvalStatus": "APPROVED",
  "approvedAmount": 250000.00,
  "interestRate": 3.75,
  "loanTerm": 30,
  "monthlyPayment": 1157.79,
  "appliedRules": [
    "High Credit Score Approval",
    "Stable Employment Bonus"
  ],
  "conditions": [
    "Property appraisal required",
    "Income verification needed"
  ],
  "executionTime": 89
}
```

### Batch Loan Processing

**Endpoint:** `POST /api/v1/loans/batch`

**Request:**
```json
[
  {
    "applicantName": "Michael Davis",
    "applicantAge": 45,
    "annualIncome": 95000.00,
    "creditScore": 680,
    "employmentStatus": "FULL_TIME",
    "loanAmount": 180000.00,
    "loanPurpose": "REFINANCE",
    "existingDebts": 25000.00
  },
  {
    "applicantName": "Lisa Chen",
    "applicantAge": 29,
    "annualIncome": 55000.00,
    "creditScore": 620,
    "employmentStatus": "PART_TIME",
    "loanAmount": 150000.00,
    "loanPurpose": "HOME_PURCHASE",
    "existingDebts": 8000.00
  }
]
```

**Response:**
```json
{
  "content": [
    {
      "applicantName": "Michael Davis",
      "applicationId": "LA-2025-001235",
      "approvalStatus": "APPROVED",
      "approvedAmount": 180000.00,
      "interestRate": 4.25,
      "appliedRules": ["Standard Approval"],
      "executionTime": 76
    },
    {
      "applicantName": "Lisa Chen",
      "applicationId": "LA-2025-001236",
      "approvalStatus": "CONDITIONAL",
      "approvedAmount": 120000.00,
      "interestRate": 5.50,
      "appliedRules": ["Low Credit Score Conditional"],
      "conditions": ["Co-signer required", "Higher down payment needed"],
      "executionTime": 82
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1
}
```

## Product Recommendation API

### Get Product Recommendations

**Endpoint:** `POST /api/v1/recommendations/generate`

**Request:**
```json
{
  "customerId": "CUST-12345",
  "customerAge": 35,
  "customerGender": "FEMALE",
  "purchaseHistory": [
    {
      "productCategory": "ELECTRONICS",
      "purchaseAmount": 599.99,
      "purchaseDate": "2025-06-15"
    },
    {
      "productCategory": "BOOKS",
      "purchaseAmount": 29.99,
      "purchaseDate": "2025-07-01"
    }
  ],
  "preferences": ["TECHNOLOGY", "EDUCATION"],
  "budgetRange": {
    "min": 50.00,
    "max": 500.00
  }
}
```

**Response:**
```json
{
  "customerId": "CUST-12345",
  "recommendations": [
    {
      "productId": "PROD-8901",
      "productName": "Wireless Headphones Pro",
      "category": "ELECTRONICS",
      "price": 299.99,
      "confidence": 0.92,
      "reason": "Based on recent electronics purchase and technology preference"
    },
    {
      "productId": "PROD-2345",
      "productName": "Programming Fundamentals Course",
      "category": "EDUCATION",
      "price": 149.99,
      "confidence": 0.87,
      "reason": "Matches education preference and learning pattern"
    },
    {
      "productId": "PROD-5678",
      "productName": "Smart Home Starter Kit",
      "category": "ELECTRONICS",
      "price": 199.99,
      "confidence": 0.78,
      "reason": "Technology enthusiast profile match"
    }
  ],
  "appliedRules": [
    "Electronics Enthusiast Rule",
    "Education Seeker Rule",
    "Budget-Conscious Buyer Rule"
  ],
  "executionTime": 134
}
```

### Batch Product Recommendations

**Endpoint:** `POST /api/v1/recommendations/batch`

**Request:**
```json
[
  {
    "customerId": "CUST-11111",
    "customerAge": 28,
    "customerGender": "MALE",
    "preferences": ["SPORTS", "FITNESS"],
    "budgetRange": {"min": 100.00, "max": 300.00}
  },
  {
    "customerId": "CUST-22222",
    "customerAge": 42,
    "customerGender": "FEMALE",
    "preferences": ["HOME_DECOR", "GARDENING"],
    "budgetRange": {"min": 25.00, "max": 150.00}
  }
]
```

## Rule Management API

### Upload Decision Table

**Endpoint:** `POST /api/v1/rules/upload`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/rules/upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@CustomerDiscountRules.xlsx" \
  -F "ruleType=CUSTOMER_DISCOUNT"
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Decision table uploaded and compiled successfully",
  "fileName": "CustomerDiscountRules.xlsx",
  "ruleType": "CUSTOMER_DISCOUNT",
  "rulesCount": 11,
  "compilationTime": 1247,
  "uploadedAt": "2025-07-23T10:33:00Z"
}
```

### Validate Rules

**Endpoint:** `PUT /api/v1/rules/validate`

**Request:**
```json
{
  "ruleType": "CUSTOMER_DISCOUNT",
  "validateSyntax": true,
  "validateLogic": true
}
```

**Response:**
```json
{
  "status": "VALID",
  "ruleType": "CUSTOMER_DISCOUNT",
  "validationResults": {
    "syntaxValid": true,
    "logicValid": true,
    "rulesCount": 11,
    "warnings": [],
    "errors": []
  },
  "validatedAt": "2025-07-23T10:33:00Z"
}
```

### Get Rule Engine Status

**Endpoint:** `GET /api/v1/rules/status`

**Response:**
```json
{
  "status": "HEALTHY",
  "loadedRuleSets": [
    {
      "name": "CustomerDiscountRules",
      "rulesCount": 11,
      "lastModified": "2025-07-23T09:15:00Z",
      "status": "ACTIVE"
    },
    {
      "name": "LoanApprovalRules",
      "rulesCount": 8,
      "lastModified": "2025-07-22T14:30:00Z",
      "status": "ACTIVE"
    },
    {
      "name": "ProductRecommendationRules",
      "rulesCount": 15,
      "lastModified": "2025-07-21T11:45:00Z",
      "status": "ACTIVE"
    }
  ],
  "totalExecutions": 5432,
  "averageExecutionTime": 67.8,
  "lastReload": "2025-07-23T08:00:00Z"
}
```

### Reload Rules

**Endpoint:** `POST /api/v1/rules/reload`

**Request:**
```json
{
  "ruleType": "ALL",
  "forceReload": true
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "All rule sets reloaded successfully",
  "reloadedRuleSets": [
    "CustomerDiscountRules",
    "LoanApprovalRules", 
    "ProductRecommendationRules"
  ],
  "totalRulesLoaded": 34,
  "reloadTime": 2156,
  "reloadedAt": "2025-07-23T10:33:00Z"
}
```

## Health Check and Monitoring

### Application Health

**Endpoint:** `GET /actuator/health`

**Response:**
```json
{
  "status": "UP",
  "components": {
    "drools": {
      "status": "UP",
      "details": {
        "ruleEngine": "ACTIVE",
        "loadedRuleSets": 3,
        "totalRules": 34
      }
    },
    "decisionTables": {
      "status": "UP",
      "details": {
        "validTables": 3,
        "lastValidation": "2025-07-23T10:30:00Z"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 91943821312,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

### Application Metrics

**Endpoint:** `GET /actuator/metrics/rule.execution.time`

**Response:**
```json
{
  "name": "rule.execution.time",
  "description": "Time taken to execute rules",
  "baseUnit": "milliseconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1247.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 84329.0
    },
    {
      "statistic": "MEAN",
      "value": 67.6
    },
    {
      "statistic": "MAX",
      "value": 234.0
    }
  ]
}
```

## Error Handling Examples

### Validation Error

**Request:**
```json
{
  "customerName": "",
  "customerAge": -5,
  "loyaltyTier": "INVALID_TIER",
  "orderAmount": -100.00,
  "orderQuantity": 0
}
```

**Response:**
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/v1/discounts/calculate",
  "timestamp": "2025-07-23T10:33:00Z",
  "errors": [
    {
      "field": "customerName",
      "message": "Customer name cannot be blank"
    },
    {
      "field": "customerAge",
      "message": "Customer age must be between 0 and 150"
    },
    {
      "field": "loyaltyTier",
      "message": "Invalid loyalty tier. Must be one of: BRONZE, SILVER, GOLD"
    },
    {
      "field": "orderAmount",
      "message": "Order amount must be positive"
    },
    {
      "field": "orderQuantity",
      "message": "Order quantity must be greater than 0"
    }
  ]
}
```

### Rule Execution Error

**Response:**
```json
{
  "type": "https://example.com/problems/rule-execution-error",
  "title": "Rule Execution Failed",
  "status": 500,
  "detail": "Failed to execute business rules",
  "instance": "/api/v1/discounts/calculate",
  "timestamp": "2025-07-23T10:33:00Z",
  "errorCode": "RULE_EXECUTION_FAILED",
  "correlationId": "abc123-def456-ghi789"
}
```

### File Upload Error

**Response:**
```json
{
  "type": "https://example.com/problems/file-upload-error",
  "title": "File Upload Failed",
  "status": 400,
  "detail": "Invalid decision table format",
  "instance": "/api/v1/rules/upload",
  "timestamp": "2025-07-23T10:33:00Z",
  "errorCode": "INVALID_DECISION_TABLE",
  "validationErrors": [
    "Missing required column: RuleSet",
    "Invalid data type in column: Salience",
    "Duplicate rule ID: DISCOUNT_001"
  ]
}
```

## Rate Limiting

All API endpoints are subject to rate limiting:

- **Standard endpoints**: 100 requests per minute per IP
- **Batch endpoints**: 10 requests per minute per IP
- **File upload endpoints**: 5 requests per minute per IP

**Rate Limit Headers:**
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1642781400
```

**Rate Limit Exceeded Response:**
```json
{
  "type": "https://example.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Please try again later.",
  "instance": "/api/v1/discounts/calculate",
  "timestamp": "2025-07-23T10:33:00Z",
  "retryAfter": 60
}
```

## Authentication Examples

For secured endpoints (if authentication is enabled):

**Request with Bearer Token:**
```bash
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"customerName": "John Doe", ...}'
```

**Authentication Error:**
```json
{
  "type": "https://example.com/problems/authentication-error",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Valid authentication credentials are required",
  "instance": "/api/v1/discounts/calculate",
  "timestamp": "2025-07-23T10:33:00Z"
}
```

## Best Practices

1. **Always validate input data** before sending requests
2. **Handle errors gracefully** using the structured error responses
3. **Use batch endpoints** for processing multiple items efficiently
4. **Monitor rate limits** to avoid throttling
5. **Include correlation IDs** in logs for better debugging
6. **Use appropriate HTTP methods** (GET for retrieval, POST for creation, PUT for updates)
7. **Set proper timeouts** for long-running operations
8. **Cache responses** where appropriate to improve performance

## SDK and Client Libraries

For easier integration, consider using the provided client libraries:

- **Java Spring Boot**: See `examples/java-spring-boot/`
- **JavaScript/Node.js**: See `examples/javascript-nodejs/`
- **Python**: See `examples/python/`
- **cURL scripts**: See `examples/curl/`
- **Postman collection**: See `examples/postman/`

Each client library includes error handling, retry logic, and proper authentication support.