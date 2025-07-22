# Spring Drools Integration API Usage Examples

This document provides examples of how to use the Spring Drools Integration API endpoints.

## Table of Contents

1. [Rule Execution API](#rule-execution-api)
2. [Customer Discount API](#customer-discount-api)
3. [Loan Approval API](#loan-approval-api)
4. [Product Recommendation API](#product-recommendation-api)

---

## Rule Execution API

The Rule Execution API provides generic endpoints for executing Drools rules on any fact object.

### Execute Rules on a Single Fact

**Endpoint:** `POST /api/v1/rules/execute`

**Description:** Executes Drools rules on a single fact object.

**Request Body Example:**
```json
{
  "name": "Test Customer",
  "age": 65,
  "loyaltyTier": "GOLD",
  "memberSince": "2020-01-01"
}
```

**Response Example:**
```json
{
  "name": "Test Customer",
  "age": 65,
  "loyaltyTier": "GOLD",
  "memberSince": "2020-01-01",
  "eligibleForSeniorDiscount": true,
  "discountPercentage": 15.0
}
```

**Possible Error Responses:**
- `400 Bad Request`: If the fact object is null or invalid
- `500 Internal Server Error`: If an error occurs during rule execution

### Execute Rules on a Batch of Facts

**Endpoint:** `POST /api/v1/rules/batch`

**Description:** Executes Drools rules on a batch of fact objects with pagination support.

**Request Parameters:**
- `page` (optional, default: 0): The page number (0-based)
- `size` (optional, default: 20): The page size

**Request Body Example:**
```json
[
  {
    "name": "Customer 1",
    "age": 65,
    "loyaltyTier": "GOLD"
  },
  {
    "name": "Customer 2",
    "age": 35,
    "loyaltyTier": "SILVER"
  },
  {
    "name": "Customer 3",
    "age": 25,
    "loyaltyTier": "BRONZE"
  }
]
```

**Response Example:**
```json
{
  "content": [
    {
      "name": "Customer 1",
      "age": 65,
      "loyaltyTier": "GOLD",
      "eligibleForSeniorDiscount": true,
      "discountPercentage": 15.0
    },
    {
      "name": "Customer 2",
      "age": 35,
      "loyaltyTier": "SILVER",
      "eligibleForSeniorDiscount": false,
      "discountPercentage": 5.0
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 3,
  "totalPages": 2,
  "first": true,
  "last": false
}
```

**Possible Error Responses:**
- `400 Bad Request`: If the facts list is null or empty
- `500 Internal Server Error`: If an error occurs during rule execution

### Get Rule Execution Metadata

**Endpoint:** `GET /api/v1/rules/metadata`

**Description:** Returns metadata and statistics about rule executions.

**Response Example:**
```json
{
  "totalExecutions": 125,
  "totalBatchExecutions": 15,
  "totalAsyncExecutions": 10,
  "sessionsCreated": 150,
  "sessionsDisposed": 150,
  "executionCounts": {
    "Customer": 75,
    "Order": 50
  },
  "executionTimes": {
    "Customer": 1250,
    "Order": 980
  }
}
```

**Possible Error Responses:**
- `500 Internal Server Error`: If an error occurs retrieving statistics

---

## Customer Discount API

The Customer Discount API provides endpoints for calculating discounts based on customer attributes and order details.

### Calculate Discount

**Endpoint:** `POST /api/v1/discounts`

**Description:** Calculates discount for a customer order based on rules.

**Request Body Example:**
```json
{
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "age": 65,
  "loyaltyTier": "GOLD",
  "orderAmount": 250.0,
  "orderQuantity": 3,
  "orderItems": [
    {
      "productId": "PROD-123",
      "productName": "Smartphone",
      "price": 599.99,
      "quantity": 1,
      "category": "ELECTRONICS"
    },
    {
      "productId": "PROD-456",
      "productName": "Headphones",
      "price": 99.99,
      "quantity": 2,
      "category": "ELECTRONICS"
    }
  ]
}
```

**Response Example:**
```json
{
  "customerId": 1001,
  "customerName": "John Doe",
  "loyaltyTier": "GOLD",
  "originalAmount": 250.0,
  "discountPercentage": 15.0,
  "discountAmount": 37.5,
  "finalAmount": 212.5,
  "appliedRules": "Senior Discount Rule, Gold Tier Discount Rule",
  "orderId": "ORD-12345",
  "timestamp": "2023-06-15T14:30:45.123",
  "notes": "Order volume: 3 items",
  "discounts": [
    {
      "ruleId": 1,
      "ruleName": "Senior Discount Rule",
      "discountPercentage": 10.0,
      "discountAmount": 25.0,
      "priority": 100
    },
    {
      "ruleId": 2,
      "ruleName": "Gold Tier Discount Rule",
      "discountPercentage": 5.0,
      "discountAmount": 12.5,
      "priority": 90
    }
  ]
}
```

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during discount calculation

### Calculate Discounts in Batch

**Endpoint:** `POST /api/v1/discounts/batch`

**Description:** Calculates discounts for multiple customer orders in batch with pagination support.

**Request Parameters:**
- `page` (optional, default: 0): The page number (0-based)
- `size` (optional, default: 20): The page size

**Request Body Example:**
```json
[
  {
    "customerName": "John Doe",
    "age": 65,
    "loyaltyTier": "GOLD",
    "orderAmount": 250.0,
    "orderQuantity": 3
  },
  {
    "customerName": "Jane Smith",
    "age": 35,
    "loyaltyTier": "SILVER",
    "orderAmount": 150.0,
    "orderQuantity": 2
  }
]
```

**Response Example:**
```json
{
  "content": [
    {
      "customerId": 1001,
      "customerName": "John Doe",
      "loyaltyTier": "GOLD",
      "originalAmount": 250.0,
      "discountPercentage": 15.0,
      "discountAmount": 37.5,
      "finalAmount": 212.5,
      "appliedRules": "Senior Discount Rule, Gold Tier Discount Rule"
    },
    {
      "customerId": 1002,
      "customerName": "Jane Smith",
      "loyaltyTier": "SILVER",
      "originalAmount": 150.0,
      "discountPercentage": 5.0,
      "discountAmount": 7.5,
      "finalAmount": 142.5,
      "appliedRules": "Silver Tier Discount Rule"
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

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during discount calculation

### Calculate Discount Asynchronously

**Endpoint:** `POST /api/v1/discounts/async`

**Description:** Asynchronously calculates discount for a customer order.

**Request Body Example:**
```json
{
  "customerName": "John Doe",
  "age": 65,
  "loyaltyTier": "GOLD",
  "orderAmount": 250.0,
  "orderQuantity": 3
}
```

**Response:**
- `202 Accepted`: The request has been accepted for processing

### Get Discount Statistics

**Endpoint:** `GET /api/v1/discounts/statistics`

**Description:** Returns statistics about discount rule executions.

**Response Example:**
```json
{
  "totalDiscountCalculations": 125,
  "totalBatchCalculations": 15,
  "discountRuleCounts": {
    "Senior Discount Rule": 45,
    "Gold Tier Discount Rule": 30,
    "Silver Tier Discount Rule": 50
  }
}
```

---

## Loan Approval API

The Loan Approval API provides endpoints for evaluating loan applications based on rules.

### Evaluate Loan Application

**Endpoint:** `POST /api/v1/loans/evaluate`

**Description:** Evaluates a loan application based on rules.

**Request Body Example:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1985-06-15",
  "ssn": "123-45-6789",
  "email": "john.doe@example.com",
  "employmentStatus": "EMPLOYED",
  "annualIncome": 120000.0,
  "monthlyDebtPayments": 5000.0,
  "creditScore": 780,
  "loanType": "MORTGAGE",
  "loanPurpose": "PURCHASE",
  "loanAmount": 250000.0,
  "loanTermMonths": 360,
  "interestRate": 4.5,
  "hasCoApplicant": false
}
```

**Response Example:**
```json
{
  "applicationNumber": "LOAN12345678",
  "applicantName": "John Doe",
  "loanType": "MORTGAGE",
  "loanPurpose": "PURCHASE",
  "loanAmount": 250000.0,
  "loanTermMonths": 360,
  "interestRate": 4.5,
  "monthlyPayment": 1266.71,
  "approved": true,
  "status": "APPROVED",
  "decisionReason": "Credit score meets requirements",
  "decisionFactors": [
    "Credit score is excellent",
    "Debt-to-income ratio is acceptable",
    "Loan-to-value ratio is acceptable"
  ],
  "riskScore": 25,
  "riskCategory": "LOW",
  "debtToIncomeRatio": 32.5,
  "loanToValueRatio": 80.0,
  "approvedAmount": 250000.0,
  "conditions": [
    "Proof of income required",
    "Property appraisal required"
  ],
  "requiredDocuments": [
    "Last 2 years tax returns",
    "Last 3 months bank statements",
    "Proof of employment"
  ],
  "decisionDate": "2023-06-15",
  "timestamp": "2023-06-15T14:30:45.123",
  "offerExpirationDate": "2023-07-15",
  "notes": "Excellent credit profile"
}
```

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during loan evaluation

### Evaluate Loan Applications in Batch

**Endpoint:** `POST /api/v1/loans/batch`

**Description:** Evaluates multiple loan applications in batch with pagination support.

**Request Parameters:**
- `page` (optional, default: 0): The page number (0-based)
- `size` (optional, default: 20): The page size

**Request Body Example:**
```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1985-06-15",
    "ssn": "123-45-6789",
    "creditScore": 780,
    "loanType": "MORTGAGE",
    "loanAmount": 250000.0,
    "loanTermMonths": 360
  },
  {
    "firstName": "Jane",
    "lastName": "Smith",
    "dateOfBirth": "1990-03-20",
    "ssn": "987-65-4321",
    "creditScore": 680,
    "loanType": "MORTGAGE",
    "loanAmount": 200000.0,
    "loanTermMonths": 360
  }
]
```

**Response Example:**
```json
{
  "content": [
    {
      "applicationNumber": "LOAN12345678",
      "applicantName": "John Doe",
      "loanType": "MORTGAGE",
      "loanAmount": 250000.0,
      "approved": true,
      "status": "APPROVED",
      "riskCategory": "LOW"
    },
    {
      "applicationNumber": "LOAN87654321",
      "applicantName": "Jane Smith",
      "loanType": "MORTGAGE",
      "loanAmount": 200000.0,
      "approved": true,
      "status": "APPROVED",
      "riskCategory": "MEDIUM"
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

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during loan evaluation

### Evaluate Loan Application Asynchronously

**Endpoint:** `POST /api/v1/loans/async`

**Description:** Asynchronously evaluates a loan application.

**Request Body Example:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1985-06-15",
  "ssn": "123-45-6789",
  "creditScore": 780,
  "loanType": "MORTGAGE",
  "loanAmount": 250000.0,
  "loanTermMonths": 360
}
```

**Response:**
- `202 Accepted`: The request has been accepted for processing

### Get Loan Approval Statistics

**Endpoint:** `GET /api/v1/loans/statistics`

**Description:** Returns statistics about loan approval rule executions.

**Response Example:**
```json
{
  "totalEvaluations": 125,
  "totalBatchEvaluations": 15,
  "totalApprovals": 95,
  "totalRejections": 30,
  "approvalRate": 76.0,
  "approvalReasons": {
    "Excellent credit score": 45,
    "Good income to debt ratio": 50
  },
  "rejectionReasons": {
    "Poor credit score": 15,
    "High debt to income ratio": 15
  }
}
```

---

## Product Recommendation API

The Product Recommendation API provides endpoints for generating product recommendations based on rules.

### Get Recommendations

**Endpoint:** `POST /api/v1/recommendations`

**Description:** Gets product recommendations for a customer based on rules.

**Request Body Example:**
```json
{
  "customerId": 12345,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "age": 35,
  "gender": "M",
  "preferredCategories": ["ELECTRONICS", "BOOKS"],
  "preferredBrands": ["TechBrand", "BookBrand"],
  "recentlyViewedProducts": ["PROD-123", "PROD-456"],
  "maxRecommendations": 5,
  "includeOutOfStock": false
}
```

**Response Example:**
```json
{
  "customerId": 12345,
  "customerName": "John Doe",
  "recommendationType": "PERSONALIZED",
  "recommendations": [
    {
      "productId": "PROD-789",
      "sku": "ELEC-001",
      "name": "Smartphone X",
      "description": "Latest smartphone with advanced features",
      "category": "ELECTRONICS",
      "subcategory": "PHONES",
      "brand": "TechBrand",
      "price": 799.99,
      "salePrice": 749.99,
      "inStock": true,
      "averageRating": 4.5,
      "ratingCount": 120,
      "score": 0.95,
      "reason": "Based on your preferred categories",
      "rule": "Category Preference Rule",
      "type": "SIMILAR"
    },
    {
      "productId": "PROD-012",
      "sku": "BOOK-001",
      "name": "Bestselling Novel",
      "category": "BOOKS",
      "brand": "BookBrand",
      "price": 24.99,
      "inStock": true,
      "score": 0.85,
      "reason": "Based on your preferred brands",
      "rule": "Brand Preference Rule",
      "type": "RECOMMENDED"
    }
  ],
  "categories": ["ELECTRONICS", "BOOKS"],
  "brands": ["TechBrand", "BookBrand"],
  "appliedRules": "Category Preference Rule, Brand Preference Rule",
  "timestamp": "2023-06-15T14:30:45.123"
}
```

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during recommendation generation

### Get Recommendations in Batch

**Endpoint:** `POST /api/v1/recommendations/batch`

**Description:** Gets product recommendations for multiple customers in batch with pagination support.

**Request Parameters:**
- `page` (optional, default: 0): The page number (0-based)
- `size` (optional, default: 20): The page size

**Request Body Example:**
```json
[
  {
    "customerId": 12345,
    "firstName": "John",
    "lastName": "Doe",
    "preferredCategories": ["ELECTRONICS"],
    "maxRecommendations": 5
  },
  {
    "customerId": 67890,
    "firstName": "Jane",
    "lastName": "Smith",
    "preferredCategories": ["CLOTHING"],
    "maxRecommendations": 3
  }
]
```

**Response Example:**
```json
{
  "content": [
    {
      "customerId": 12345,
      "customerName": "John Doe",
      "recommendationType": "PERSONALIZED",
      "recommendations": [
        {
          "productId": "PROD-789",
          "name": "Smartphone X",
          "category": "ELECTRONICS",
          "price": 799.99,
          "score": 0.95
        }
      ]
    },
    {
      "customerId": 67890,
      "customerName": "Jane Smith",
      "recommendationType": "PERSONALIZED",
      "recommendations": [
        {
          "productId": "PROD-345",
          "name": "Designer Jeans",
          "category": "CLOTHING",
          "price": 59.99,
          "score": 0.90
        }
      ]
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

**Possible Error Responses:**
- `400 Bad Request`: If the request is invalid or missing required fields
- `500 Internal Server Error`: If an error occurs during recommendation generation

### Get Recommendations Asynchronously

**Endpoint:** `POST /api/v1/recommendations/async`

**Description:** Asynchronously gets product recommendations for a customer.

**Request Body Example:**
```json
{
  "customerId": 12345,
  "firstName": "John",
  "lastName": "Doe",
  "preferredCategories": ["ELECTRONICS"],
  "maxRecommendations": 5
}
```

**Response:**
- `202 Accepted`: The request has been accepted for processing

### Get Product-Based Recommendations

**Endpoint:** `GET /api/v1/recommendations/product/{productId}`

**Description:** Gets recommendations for a specific product (similar or complementary products).

**Path Parameters:**
- `productId`: The ID of the product

**Request Parameters:**
- `maxRecommendations` (optional, default: 5): The maximum number of recommendations to return

**Response Example:**
```json
{
  "currentProductId": "PROD-123",
  "currentProductName": "Smartphone X",
  "recommendationType": "PRODUCT_BASED",
  "recommendations": [
    {
      "productId": "PROD-456",
      "name": "Smartphone Case",
      "category": "ACCESSORIES",
      "price": 29.99,
      "score": 0.95,
      "reason": "Frequently bought together",
      "type": "COMPLEMENTARY"
    },
    {
      "productId": "PROD-789",
      "name": "Smartphone Y",
      "category": "ELECTRONICS",
      "price": 699.99,
      "score": 0.85,
      "reason": "Similar product",
      "type": "SIMILAR"
    }
  ],
  "appliedRules": "Complementary Products Rule, Similar Products Rule",
  "timestamp": "2023-06-15T14:30:45.123"
}
```

**Possible Error Responses:**
- `404 Not Found`: If the product is not found
- `500 Internal Server Error`: If an error occurs during recommendation generation

### Get Recommendation Statistics

**Endpoint:** `GET /api/v1/recommendations/statistics`

**Description:** Returns statistics about product recommendation rule executions.

**Response Example:**
```json
{
  "totalRecommendations": 125,
  "totalBatchRecommendations": 15,
  "totalProductBasedRecommendations": 50,
  "recommendationTypeCounts": {
    "SIMILAR": 75,
    "COMPLEMENTARY": 50,
    "TRENDING": 25
  },
  "categoryRecommendationCounts": {
    "ELECTRONICS": 60,
    "CLOTHING": 40,
    "HOME_GOODS": 25
  }
}
```

---

## Using the API with cURL

Here are some examples of how to use the API with cURL:

### Calculate a Discount

```bash
curl -X POST http://localhost:8080/api/v1/discounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "age": 65,
    "loyaltyTier": "GOLD",
    "orderAmount": 250.0,
    "orderQuantity": 3
  }'
```

### Evaluate a Loan Application

```bash
curl -X POST http://localhost:8080/api/v1/loans/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1985-06-15",
    "ssn": "123-45-6789",
    "creditScore": 780,
    "loanType": "MORTGAGE",
    "loanAmount": 250000.0,
    "loanTermMonths": 360
  }'
```

### Get Product Recommendations

```bash
curl -X POST http://localhost:8080/api/v1/recommendations \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 12345,
    "firstName": "John",
    "lastName": "Doe",
    "preferredCategories": ["ELECTRONICS"],
    "maxRecommendations": 5
  }'
```

### Get Product-Based Recommendations

```bash
curl -X GET "http://localhost:8080/api/v1/recommendations/product/1001?maxRecommendations=3"
```

### Get Rule Execution Statistics

```bash
curl -X GET http://localhost:8080/api/v1/rules/metadata
```