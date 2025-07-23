# Spring Boot Drools Integration - API Documentation

This document provides comprehensive documentation for all REST APIs in the Spring Boot Drools Integration application. The APIs are designed following RESTful principles and include comprehensive OpenAPI/Swagger documentation.

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Base URL](#base-url)
- [Response Formats](#response-formats)
- [Error Handling](#error-handling)
- [Customer Discount APIs](#customer-discount-apis)
- [Loan Approval APIs](#loan-approval-apis)
- [Product Recommendation APIs](#product-recommendation-apis)
- [Rule Execution APIs](#rule-execution-apis)
- [Rule Management APIs](#rule-management-apis)
- [OpenAPI Specification](#openapi-specification)
- [Client Examples](#client-examples)

## 🔍 Overview

The Spring Boot Drools Integration application provides REST APIs for executing business rules using the Drools rule engine. The application supports three main use cases:

1. **Customer Discount Calculation** - Calculate discounts based on customer attributes and order details
2. **Loan Approval Evaluation** - Evaluate loan applications based on applicant information
3. **Product Recommendations** - Generate product recommendations based on customer preferences

All APIs are documented using OpenAPI 3.0 specification and are available through Swagger UI.

## 🔐 Authentication

The application uses HTTP Basic Authentication for all protected endpoints.

**Default Credentials:**
- Username: `admin`
- Password: `admin123`

**Authentication Header:**
```
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

## 🌐 Base URL

**Local Development:**
```
http://localhost:8080
```

**Production:**
```
https://your-domain.com
```

## 📄 Response Formats

All APIs return JSON responses with consistent structure:

### Success Response
```json
{
  "data": {
    "customerId": "CUST001",
    "discountPercentage": 15.0
  },
  "timestamp": "2025-07-23T10:09:00Z",
  "status": "success"
}
```

### Error Response (RFC 7807 Problem Details)
```json
{
  "type": "https://example.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "The request contains invalid data",
  "instance": "/api/v1/discounts/calculate",
  "timestamp": "2025-07-23T10:09:00Z",
  "errors": [
    {
      "field": "age",
      "message": "Age must be between 18 and 120"
    }
  ]
}
```

## ❌ Error Handling

The application uses standardized HTTP status codes:

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 202 | Accepted - Request accepted for processing |
| 400 | Bad Request - Invalid request data |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Access denied |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource conflict |
| 422 | Unprocessable Entity - Validation error |
| 500 | Internal Server Error - Server error |

## 💰 Customer Discount APIs

### Calculate Customer Discount

Calculates discount for a customer order based on Drools rules.

**Endpoint:** `POST /api/v1/discounts/calculate`

**Request Body:**
```json
{
  "customerId": "CUST001",
  "customerName": "John Doe",
  "age": 35,
  "loyaltyTier": "GOLD",
  "orderAmount": 1000.00,
  "orderItems": 5,
  "membershipYears": 3,
  "previousOrders": 25
}
```

**Response:**
```json
{
  "customerId": "CUST001",
  "customerName": "John Doe",
  "originalAmount": 1000.00,
  "discountPercentage": 15.0,
  "discountAmount": 150.00,
  "finalAmount": 850.00,
  "appliedRules": [
    "Age-based discount: 5%",
    "Loyalty tier discount: 10%"
  ],
  "calculationTimestamp": "2025-07-23T10:09:00Z"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5,
    "membershipYears": 3,
    "previousOrders": 25
  }'
```

### Batch Discount Calculation

Calculates discounts for multiple customer orders with pagination support.

**Endpoint:** `POST /api/v1/discounts/batch`

**Query Parameters:**
- `page` (optional): Page number (0-based), default: 0
- `size` (optional): Page size, default: 20

**Request Body:**
```json
[
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
  }
]
```

**Response:**
```json
{
  "content": [
    {
      "customerId": "CUST001",
      "customerName": "John Doe",
      "originalAmount": 1000.00,
      "discountPercentage": 15.0,
      "discountAmount": 150.00,
      "finalAmount": 850.00
    },
    {
      "customerId": "CUST002",
      "customerName": "Jane Smith",
      "originalAmount": 500.00,
      "discountPercentage": 8.0,
      "discountAmount": 40.00,
      "finalAmount": 460.00
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 2,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Asynchronous Discount Calculation

Initiates asynchronous discount calculation for a customer order.

**Endpoint:** `POST /api/v1/discounts/async`

**Request Body:** Same as calculate discount

**Response:** HTTP 202 Accepted (no body)

### Discount Statistics

Retrieves statistics about discount rule executions.

**Endpoint:** `GET /api/v1/discounts/statistics`

**Response:**
```json
{
  "totalCalculations": 1250,
  "averageDiscountPercentage": 12.5,
  "totalDiscountAmount": 125000.00,
  "ruleExecutionStats": {
    "age-based-discount": 800,
    "loyalty-tier-discount": 600,
    "volume-discount": 300
  },
  "performanceMetrics": {
    "averageExecutionTime": 45,
    "maxExecutionTime": 120,
    "minExecutionTime": 15
  }
}
```

## 🏦 Loan Approval APIs

### Evaluate Loan Application

Evaluates a loan application based on Drools rules.

**Endpoint:** `POST /api/v1/loan-approval/evaluate`

**Request Body:**
```json
{
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
}
```

**Response:**
```json
{
  "applicantId": "APP001",
  "applicantName": "John Doe",
  "loanAmount": 250000.00,
  "approvalStatus": "APPROVED",
  "approvedAmount": 250000.00,
  "interestRate": 3.5,
  "loanTerm": 30,
  "monthlyPayment": 1123.00,
  "conditions": [
    "Provide proof of income",
    "Property appraisal required"
  ],
  "appliedRules": [
    "High credit score: Approved",
    "Stable employment: Rate reduction",
    "Good debt-to-income ratio: Full amount approved"
  ],
  "evaluationTimestamp": "2025-07-23T10:09:00Z"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
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
  }'
```

### Batch Loan Evaluation

Evaluates multiple loan applications with pagination support.

**Endpoint:** `POST /api/v1/loan-approval/batch`

**Query Parameters:**
- `page` (optional): Page number (0-based), default: 0
- `size` (optional): Page size, default: 20

### Loan Approval Statistics

Retrieves statistics about loan approval evaluations.

**Endpoint:** `GET /api/v1/loan-approval/statistics`

**Response:**
```json
{
  "totalApplications": 500,
  "approvalRate": 75.0,
  "averageLoanAmount": 180000.00,
  "averageInterestRate": 4.2,
  "approvalsByPurpose": {
    "HOME_PURCHASE": 300,
    "REFINANCE": 150,
    "HOME_IMPROVEMENT": 50
  },
  "performanceMetrics": {
    "averageEvaluationTime": 85,
    "maxEvaluationTime": 200,
    "minEvaluationTime": 30
  }
}
```

## 🛍️ Product Recommendation APIs

### Generate Product Recommendations

Generates product recommendations based on customer preferences and history.

**Endpoint:** `POST /api/v1/product-recommendation/recommend`

**Request Body:**
```json
{
  "customerId": "CUST001",
  "customerName": "John Doe",
  "age": 35,
  "preferences": ["ELECTRONICS", "BOOKS", "SPORTS"],
  "purchaseHistory": ["LAPTOP", "SMARTPHONE", "TABLET"],
  "budget": 500.00,
  "location": "US",
  "seasonalPreferences": ["SUMMER"]
}
```

**Response:**
```json
{
  "customerId": "CUST001",
  "customerName": "John Doe",
  "recommendations": [
    {
      "productId": "PROD001",
      "productName": "Wireless Headphones",
      "category": "ELECTRONICS",
      "price": 199.99,
      "confidence": 0.95,
      "reason": "Based on electronics preference and recent smartphone purchase"
    },
    {
      "productId": "PROD002",
      "productName": "Programming Book",
      "category": "BOOKS",
      "price": 49.99,
      "confidence": 0.85,
      "reason": "Based on books preference and tech purchase history"
    },
    {
      "productId": "PROD003",
      "productName": "Fitness Tracker",
      "category": "SPORTS",
      "price": 149.99,
      "confidence": 0.75,
      "reason": "Based on sports preference and age group"
    }
  ],
  "totalRecommendations": 3,
  "appliedRules": [
    "Electronics preference match",
    "Purchase history analysis",
    "Budget constraint applied"
  ],
  "recommendationTimestamp": "2025-07-23T10:09:00Z"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "preferences": ["ELECTRONICS", "BOOKS", "SPORTS"],
    "purchaseHistory": ["LAPTOP", "SMARTPHONE", "TABLET"],
    "budget": 500.00,
    "location": "US",
    "seasonalPreferences": ["SUMMER"]
  }'
```

### Batch Product Recommendations

Generates product recommendations for multiple customers with pagination support.

**Endpoint:** `POST /api/v1/product-recommendation/batch`

### Product Recommendation Statistics

Retrieves statistics about product recommendation generations.

**Endpoint:** `GET /api/v1/product-recommendation/statistics`

## ⚙️ Rule Execution APIs

### Generic Rule Execution

Executes rules for any rule package with custom facts.

**Endpoint:** `POST /api/v1/rules/execute`

**Request Body:**
```json
{
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
}
```

**Response:**
```json
{
  "executionId": "EXEC001",
  "rulePackage": "customer-discount",
  "results": [
    {
      "factType": "DiscountResult",
      "data": {
        "discountPercentage": 15.0,
        "discountAmount": 150.00,
        "appliedRules": ["age-discount", "loyalty-discount"]
      }
    }
  ],
  "executionTime": 45,
  "rulesExecuted": 3,
  "executionTimestamp": "2025-07-23T10:09:00Z"
}
```

### Batch Rule Execution

Executes rules for multiple fact sets with pagination support.

**Endpoint:** `POST /api/v1/rules/batch`

### Rule Execution Metadata

Retrieves metadata about available rule packages and their capabilities.

**Endpoint:** `GET /api/v1/rules/metadata`

**Response:**
```json
{
  "rulePackages": [
    {
      "name": "customer-discount",
      "version": "1.0.0",
      "description": "Customer discount calculation rules",
      "supportedFactTypes": ["Customer", "Order"],
      "outputTypes": ["DiscountResult"],
      "lastUpdated": "2025-07-23T10:09:00Z"
    },
    {
      "name": "loan-approval",
      "version": "1.0.0",
      "description": "Loan approval evaluation rules",
      "supportedFactTypes": ["LoanApplication", "Applicant"],
      "outputTypes": ["ApprovalResult"],
      "lastUpdated": "2025-07-23T10:09:00Z"
    }
  ],
  "totalPackages": 3,
  "engineVersion": "8.44.0.Final"
}
```

## 🔧 Rule Management APIs

### Upload Decision Table

Uploads a new decision table Excel file.

**Endpoint:** `POST /api/v1/rules/upload`

**Content-Type:** `multipart/form-data`

**Form Parameters:**
- `file`: Excel file (.xlsx)
- `rulePackage`: Target rule package name
- `description` (optional): Description of the rules

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/rules/upload \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -F "file=@CustomerDiscountRules.xlsx" \
  -F "rulePackage=customer-discount" \
  -F "description=Updated customer discount rules"
```

**Response:**
```json
{
  "uploadId": "UPLOAD001",
  "fileName": "CustomerDiscountRules.xlsx",
  "rulePackage": "customer-discount",
  "status": "SUCCESS",
  "rulesCount": 15,
  "validationResults": {
    "valid": true,
    "warnings": [],
    "errors": []
  },
  "uploadTimestamp": "2025-07-23T10:09:00Z"
}
```

### Validate Rules

Validates all loaded rules for syntax and consistency.

**Endpoint:** `PUT /api/v1/rules/validate`

**Response:**
```json
{
  "validationId": "VAL001",
  "overallStatus": "VALID",
  "rulePackages": [
    {
      "name": "customer-discount",
      "status": "VALID",
      "rulesCount": 15,
      "warnings": [],
      "errors": []
    },
    {
      "name": "loan-approval",
      "status": "WARNING",
      "rulesCount": 12,
      "warnings": [
        "Rule 'high-risk-applicant' has low priority"
      ],
      "errors": []
    }
  ],
  "validationTimestamp": "2025-07-23T10:09:00Z"
}
```

### Reload Rules

Reloads all rules from the configured sources.

**Endpoint:** `POST /api/v1/rules/reload`

**Response:**
```json
{
  "reloadId": "RELOAD001",
  "status": "SUCCESS",
  "reloadedPackages": [
    "customer-discount",
    "loan-approval",
    "product-recommendation"
  ],
  "totalRules": 42,
  "reloadTime": 1250,
  "reloadTimestamp": "2025-07-23T10:09:00Z"
}
```

### Rule Engine Status

Retrieves the current status of the rule engine.

**Endpoint:** `GET /api/v1/rules/status`

**Response:**
```json
{
  "engineStatus": "HEALTHY",
  "version": "8.44.0.Final",
  "uptime": 3600000,
  "loadedPackages": 3,
  "totalRules": 42,
  "memoryUsage": {
    "used": "256MB",
    "max": "1GB",
    "percentage": 25.6
  },
  "performance": {
    "totalExecutions": 1500,
    "averageExecutionTime": 65,
    "executionsPerSecond": 15.5
  },
  "lastReload": "2025-07-23T10:09:00Z"
}
```

## 📖 OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:

**Swagger UI:** `http://localhost:8080/swagger-ui.html`
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
**OpenAPI YAML:** `http://localhost:8080/v3/api-docs.yaml`

### Key OpenAPI Features

- **Comprehensive Schemas**: All request/response models are fully documented
- **Example Values**: Realistic examples for all endpoints
- **Parameter Documentation**: Detailed parameter descriptions and constraints
- **Response Codes**: Complete HTTP status code documentation
- **Authentication**: Security scheme documentation
- **Tags**: Logical grouping of endpoints

## 💻 Client Examples

### Java Spring Boot Client

```java
@Service
public class DroolsApiClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl;
    
    public DroolsApiClient(RestTemplate restTemplate, 
                          @Value("${drools.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }
    
    public CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request) {
        String url = baseUrl + "/api/v1/discounts/calculate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin123");
        
        HttpEntity<CustomerDiscountRequest> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, CustomerDiscountResponse.class);
    }
    
    public LoanApprovalResponse evaluateLoan(LoanApprovalRequest request) {
        String url = baseUrl + "/api/v1/loan-approval/evaluate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin123");
        
        HttpEntity<LoanApprovalRequest> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, LoanApprovalResponse.class);
    }
}
```

### JavaScript/Node.js Client

```javascript
class DroolsApiClient {
    constructor(baseUrl, username, password) {
        this.baseUrl = baseUrl;
        this.auth = Buffer.from(`${username}:${password}`).toString('base64');
    }
    
    async calculateDiscount(request) {
        const response = await fetch(`${this.baseUrl}/api/v1/discounts/calculate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${this.auth}`
            },
            body: JSON.stringify(request)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    }
    
    async evaluateLoan(request) {
        const response = await fetch(`${this.baseUrl}/api/v1/loan-approval/evaluate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Basic ${this.auth}`
            },
            body: JSON.stringify(request)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    }
}

// Usage
const client = new DroolsApiClient('http://localhost:8080', 'admin', 'admin123');

const discountRequest = {
    customerId: 'CUST001',
    customerName: 'John Doe',
    age: 35,
    loyaltyTier: 'GOLD',
    orderAmount: 1000.00,
    orderItems: 5
};

client.calculateDiscount(discountRequest)
    .then(response => console.log('Discount calculated:', response))
    .catch(error => console.error('Error:', error));
```

### Python Client

```python
import requests
import json
from typing import Dict, Any

class DroolsApiClient:
    def __init__(self, base_url: str, username: str, password: str):
        self.base_url = base_url
        self.auth = (username, password)
        self.headers = {'Content-Type': 'application/json'}
    
    def calculate_discount(self, request: Dict[str, Any]) -> Dict[str, Any]:
        url = f"{self.base_url}/api/v1/discounts/calculate"
        response = requests.post(url, json=request, auth=self.auth, headers=self.headers)
        response.raise_for_status()
        return response.json()
    
    def evaluate_loan(self, request: Dict[str, Any]) -> Dict[str, Any]:
        url = f"{self.base_url}/api/v1/loan-approval/evaluate"
        response = requests.post(url, json=request, auth=self.auth, headers=self.headers)
        response.raise_for_status()
        return response.json()
    
    def get_recommendations(self, request: Dict[str, Any]) -> Dict[str, Any]:
        url = f"{self.base_url}/api/v1/product-recommendation/recommend"
        response = requests.post(url, json=request, auth=self.auth, headers=self.headers)
        response.raise_for_status()
        return response.json()

# Usage
client = DroolsApiClient('http://localhost:8080', 'admin', 'admin123')

discount_request = {
    'customerId': 'CUST001',
    'customerName': 'John Doe',
    'age': 35,
    'loyaltyTier': 'GOLD',
    'orderAmount': 1000.00,
    'orderItems': 5
}

try:
    result = client.calculate_discount(discount_request)
    print(f"Discount calculated: {result}")
except requests.exceptions.RequestException as e:
    print(f"Error: {e}")
```

## 📊 Rate Limiting and Performance

### Rate Limiting

The API implements rate limiting to ensure fair usage:

- **Authenticated users**: 1000 requests per hour
- **Rule execution endpoints**: 100 requests per minute
- **File upload endpoints**: 10 requests per minute

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1627123456
```

### Performance Guidelines

- **Response Times**: Most endpoints respond within 100ms
- **Batch Operations**: Use batch endpoints for multiple operations
- **Caching**: Results are cached for 5 minutes by default
- **Async Operations**: Use async endpoints for long-running operations

## 🔍 Monitoring and Observability

### Health Check Endpoints

- **Application Health**: `GET /actuator/health`
- **Detailed Health**: `GET /actuator/health` (with authentication)
- **Rule Engine Health**: Included in detailed health check

### Metrics Endpoints

- **Application Metrics**: `GET /actuator/metrics`
- **Prometheus Format**: `GET /actuator/prometheus`
- **Custom Metrics**: Rule execution times, hit rates, error rates

### Logging

All API requests and responses are logged with correlation IDs for traceability.

---

**For additional support, please refer to the main [README](../README.md) or create an issue in the repository.**