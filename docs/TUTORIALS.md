# Tutorials - Spring Boot Drools Integration

This comprehensive tutorial guide provides step-by-step instructions for common use cases and scenarios with the Spring Boot Drools Integration application.

## 📋 Table of Contents

- [Getting Started Tutorial](#getting-started-tutorial)
- [Tutorial 1: Customer Discount System](#tutorial-1-customer-discount-system)
- [Tutorial 2: Loan Approval System](#tutorial-2-loan-approval-system)
- [Tutorial 3: Product Recommendation System](#tutorial-3-product-recommendation-system)
- [Tutorial 4: Creating Custom Decision Tables](#tutorial-4-creating-custom-decision-tables)
- [Tutorial 5: Building a Client Application](#tutorial-5-building-a-client-application)
- [Tutorial 6: Batch Processing](#tutorial-6-batch-processing)
- [Tutorial 7: Performance Optimization](#tutorial-7-performance-optimization)
- [Tutorial 8: Monitoring and Observability](#tutorial-8-monitoring-and-observability)
- [Tutorial 9: Deployment to Production](#tutorial-9-deployment-to-production)
- [Advanced Tutorials](#advanced-tutorials)

## 🚀 Getting Started Tutorial

### Prerequisites

Before starting these tutorials, ensure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- Git for cloning the repository
- A text editor or IDE (IntelliJ IDEA, Eclipse, VS Code)
- Basic understanding of Spring Boot and REST APIs

### Step 1: Clone and Setup

```bash
# Clone the repository
git clone https://github.com/quy267/spring-drools-integration.git
cd spring-drools-integration

# Build the application
mvn clean compile

# Run tests to verify setup
mvn test

# Start the application
mvn spring-boot:run
```

### Step 2: Verify Installation

```bash
# Test application health
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}

# Test API access
curl -u admin:admin123 http://localhost:8080/api/v1/rules/status
```

### Step 3: Explore the Application

1. **Swagger UI**: Visit http://localhost:8080/swagger-ui.html
2. **Actuator Endpoints**: Visit http://localhost:8080/actuator
3. **Application Info**: Visit http://localhost:8080/actuator/info

---

## Tutorial 1: Customer Discount System

### Overview

Learn how to use the customer discount system to calculate discounts based on customer attributes and order details.

### Step 1: Understanding the Business Rules

The customer discount system applies discounts based on:
- Customer age (seniors get discounts)
- Loyalty tier (Gold, Silver, Bronze)
- Order amount (volume discounts)
- Order quantity (bulk discounts)
- Membership duration

### Step 2: Basic Discount Calculation

```bash
# Calculate discount for a Gold tier customer
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

**Expected Response:**
```json
{
  "customerId": "CUST001",
  "customerName": "John Doe",
  "originalAmount": 1000.00,
  "discountPercentage": 15.0,
  "discountAmount": 150.00,
  "finalAmount": 850.00,
  "appliedRules": [
    "Gold tier discount: 10%",
    "Volume discount: 5%"
  ],
  "calculationTimestamp": "2025-07-23T10:09:00Z"
}
```

### Step 3: Testing Different Scenarios

#### Scenario A: Senior Customer
```bash
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST002",
    "customerName": "Mary Smith",
    "age": 68,
    "loyaltyTier": "SILVER",
    "orderAmount": 500.00,
    "orderItems": 3
  }'
```

#### Scenario B: Young Customer with Large Order
```bash
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST003",
    "customerName": "Alex Johnson",
    "age": 22,
    "loyaltyTier": "BRONZE",
    "orderAmount": 2000.00,
    "orderItems": 15
  }'
```

### Step 4: Understanding the Results

Analyze how different factors affect the discount:
- Age-based discounts for seniors (65+)
- Loyalty tier multipliers
- Volume discounts for large orders
- Bulk discounts for many items

### Step 5: Batch Processing

```bash
# Process multiple customers at once
curl -X POST "http://localhost:8080/api/v1/discounts/batch?page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '[
    {
      "customerId": "BATCH001",
      "customerName": "Customer 1",
      "age": 30,
      "loyaltyTier": "GOLD",
      "orderAmount": 800.00,
      "orderItems": 4
    },
    {
      "customerId": "BATCH002",
      "customerName": "Customer 2",
      "age": 65,
      "loyaltyTier": "SILVER",
      "orderAmount": 600.00,
      "orderItems": 3
    }
  ]'
```

---

## Tutorial 2: Loan Approval System

### Overview

Learn how to use the loan approval system to evaluate loan applications based on applicant information and financial criteria.

### Step 1: Understanding Loan Approval Criteria

The system evaluates loans based on:
- Credit score (primary factor)
- Annual income vs loan amount ratio
- Employment stability
- Existing debt obligations
- Down payment amount

### Step 2: Basic Loan Evaluation

```bash
# Evaluate a loan application
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

**Expected Response:**
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

### Step 3: Testing Different Credit Scenarios

#### Scenario A: Excellent Credit
```bash
curl -X POST http://localhost:8080/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "applicantId": "APP002",
    "applicantName": "Sarah Wilson",
    "age": 42,
    "creditScore": 820,
    "annualIncome": 120000.00,
    "loanAmount": 400000.00,
    "employmentYears": 10,
    "existingDebts": 5000.00,
    "loanPurpose": "HOME_PURCHASE",
    "downPayment": 80000.00
  }'
```

#### Scenario B: Marginal Credit
```bash
curl -X POST http://localhost:8080/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "applicantId": "APP003",
    "applicantName": "Mike Brown",
    "age": 28,
    "creditScore": 650,
    "annualIncome": 45000.00,
    "loanAmount": 180000.00,
    "employmentYears": 2,
    "existingDebts": 25000.00,
    "loanPurpose": "HOME_PURCHASE",
    "downPayment": 20000.00
  }'
```

### Step 4: Understanding Approval Decisions

The system returns different statuses:
- **APPROVED**: Full loan amount approved
- **CONDITIONAL**: Approved with conditions
- **PARTIAL**: Approved for reduced amount
- **REJECTED**: Application denied

---

## Tutorial 3: Product Recommendation System

### Overview

Learn how to generate personalized product recommendations based on customer preferences, purchase history, and behavior patterns.

### Step 1: Understanding Recommendation Logic

The system considers:
- Customer demographics (age, location)
- Stated preferences
- Purchase history
- Budget constraints
- Seasonal factors
- Inventory availability

### Step 2: Basic Recommendation Request

```bash
# Get product recommendations
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

**Expected Response:**
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
    }
  ],
  "totalRecommendations": 2,
  "appliedRules": [
    "Electronics preference match",
    "Purchase history analysis",
    "Budget constraint applied"
  ],
  "recommendationTimestamp": "2025-07-23T10:09:00Z"
}
```

### Step 3: Testing Different Customer Profiles

#### Scenario A: Fashion-Conscious Young Adult
```bash
curl -X POST http://localhost:8080/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST004",
    "customerName": "Emma Davis",
    "age": 24,
    "preferences": ["FASHION", "BEAUTY", "LIFESTYLE"],
    "purchaseHistory": ["DRESS", "MAKEUP", "ACCESSORIES"],
    "budget": 300.00,
    "location": "US",
    "seasonalPreferences": ["SPRING"]
  }'
```

#### Scenario B: Sports Enthusiast
```bash
curl -X POST http://localhost:8080/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST005",
    "customerName": "Tom Wilson",
    "age": 42,
    "preferences": ["SPORTS", "FITNESS", "OUTDOOR"],
    "purchaseHistory": ["RUNNING_SHOES", "GYM_MEMBERSHIP", "PROTEIN_POWDER"],
    "budget": 800.00,
    "location": "US",
    "seasonalPreferences": ["SUMMER"]
  }'
```

---

## Tutorial 4: Creating Custom Decision Tables

### Overview

Learn how to create and modify decision tables to implement custom business rules.

### Step 1: Understanding Decision Table Structure

A decision table consists of:
- **RuleSet**: Name of the rule collection
- **RuleId**: Unique identifier for each rule
- **Salience**: Priority (higher numbers execute first)
- **Condition Columns**: Input criteria
- **Action Columns**: Output results

### Step 2: Creating a Simple Decision Table

Create a new Excel file `SimpleDiscountRules.xlsx`:

| RuleSet | RuleId | Salience | Customer Age | Order Amount | Discount Percentage |
|---------|--------|----------|--------------|--------------|---------------------|
| SimpleDiscountRules | Senior | 100 | > 65 | | 10 |
| SimpleDiscountRules | Large Order | 90 | | >= 1000 | 5 |
| SimpleDiscountRules | Default | 10 | | | 0 |

### Step 3: Upload the Decision Table

```bash
# Upload the new decision table
curl -X POST http://localhost:8080/api/v1/rules/upload \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -F "file=@SimpleDiscountRules.xlsx" \
  -F "rulePackage=simple-discount" \
  -F "description=Simple discount rules for tutorial"
```

### Step 4: Test the Custom Rules

```bash
# Test with a senior customer
curl -X POST http://localhost:8080/api/v1/rules/execute \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "rulePackage": "simple-discount",
    "facts": [
      {
        "factType": "Customer",
        "data": {
          "age": 70,
          "orderAmount": 500
        }
      }
    ]
  }'
```

### Step 5: Modify and Reload Rules

1. Edit the Excel file to add new rules
2. Upload the updated file
3. Reload the rules:

```bash
curl -X POST http://localhost:8080/api/v1/rules/reload \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

---

## Tutorial 5: Building a Client Application

### Overview

Learn how to build a Java Spring Boot client application that integrates with the Drools API.

### Step 1: Create a New Spring Boot Project

```bash
# Create a new Spring Boot project
curl https://start.spring.io/starter.zip \
  -d dependencies=web,actuator \
  -d groupId=com.example \
  -d artifactId=drools-client \
  -d name=drools-client \
  -d packageName=com.example.droolsclient \
  -d javaVersion=17 \
  -o drools-client.zip

unzip drools-client.zip
cd drools-client
```

### Step 2: Add Dependencies

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### Step 3: Create the API Client

```java
@Service
public class DroolsApiClient {
    
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080";
    private final String username = "admin";
    private final String password = "admin123";
    
    public DroolsApiClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public Map<String, Object> calculateDiscount(Map<String, Object> request) {
        String url = baseUrl + "/api/v1/discounts/calculate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, password);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }
}
```

### Step 4: Create a Controller

```java
@RestController
@RequestMapping("/api/client")
public class ClientController {
    
    private final DroolsApiClient droolsClient;
    
    public ClientController(DroolsApiClient droolsClient) {
        this.droolsClient = droolsClient;
    }
    
    @PostMapping("/discount")
    public ResponseEntity<Map<String, Object>> calculateDiscount(
            @RequestBody Map<String, Object> request) {
        
        try {
            Map<String, Object> result = droolsClient.calculateDiscount(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
```

### Step 5: Test the Client

```bash
# Start your client application
mvn spring-boot:run

# Test the client endpoint
curl -X POST http://localhost:8081/api/client/discount \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CLIENT001",
    "customerName": "Test Customer",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

---

## Tutorial 6: Batch Processing

### Overview

Learn how to efficiently process large volumes of data using batch operations.

### Step 1: Prepare Batch Data

Create a file `customers.json`:

```json
[
  {
    "customerId": "BATCH001",
    "customerName": "Customer 1",
    "age": 25,
    "loyaltyTier": "BRONZE",
    "orderAmount": 150.00,
    "orderItems": 2
  },
  {
    "customerId": "BATCH002",
    "customerName": "Customer 2",
    "age": 45,
    "loyaltyTier": "SILVER",
    "orderAmount": 750.00,
    "orderItems": 8
  },
  {
    "customerId": "BATCH003",
    "customerName": "Customer 3",
    "age": 67,
    "loyaltyTier": "GOLD",
    "orderAmount": 1200.00,
    "orderItems": 12
  }
]
```

### Step 2: Process the Batch

```bash
# Process all customers in one batch
curl -X POST "http://localhost:8080/api/v1/discounts/batch?page=0&size=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d @customers.json
```

### Step 3: Handle Large Datasets with Pagination

```bash
# Process in smaller chunks
curl -X POST "http://localhost:8080/api/v1/discounts/batch?page=0&size=2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d @customers.json

# Process next page
curl -X POST "http://localhost:8080/api/v1/discounts/batch?page=1&size=2" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d @customers.json
```

### Step 4: Async Processing

```bash
# Initiate async processing
curl -X POST http://localhost:8080/api/v1/discounts/async \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "ASYNC001",
    "customerName": "Async Customer",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

---

## Tutorial 7: Performance Optimization

### Overview

Learn how to optimize the performance of your Drools integration for production use.

### Step 1: Monitor Performance

```bash
# Check current performance metrics
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/metrics/rule.execution.time

# Monitor JVM metrics
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Step 2: Optimize Rule Order

Ensure rules are ordered by priority (salience) in your decision tables:

| RuleSet | RuleId | Salience | Conditions | Actions |
|---------|--------|----------|------------|---------|
| Rules | MostSpecific | 100 | Multiple conditions | Action |
| Rules | Specific | 80 | Fewer conditions | Action |
| Rules | General | 50 | Basic conditions | Action |
| Rules | Default | 10 | No conditions | Default action |

### Step 3: Use Batch Processing

```bash
# Instead of individual calls
for customer in customers; do
  curl -X POST .../calculate -d "$customer"
done

# Use batch processing
curl -X POST .../batch -d "$all_customers"
```

### Step 4: Configure JVM for Performance

```bash
# Start with optimized JVM settings
java -Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 \
  -jar spring-drools-integration.jar
```

### Step 5: Load Testing

Create a simple load test script:

```bash
#!/bin/bash
# load_test.sh

CONCURRENT_USERS=10
TOTAL_REQUESTS=1000

echo "Starting load test with $CONCURRENT_USERS concurrent users"

for i in $(seq 1 $TOTAL_REQUESTS); do
  curl -s -X POST http://localhost:8080/api/v1/discounts/calculate \
    -H "Content-Type: application/json" \
    -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
    -d '{
      "customerId": "LOAD_TEST_'$i'",
      "customerName": "Load Test User",
      "age": 35,
      "loyaltyTier": "GOLD",
      "orderAmount": 1000.00,
      "orderItems": 5
    }' > /dev/null &
  
  if (( i % CONCURRENT_USERS == 0 )); then
    wait
  fi
done

wait
echo "Load test completed"
```

---

## Tutorial 8: Monitoring and Observability

### Overview

Learn how to monitor your Drools application in production.

### Step 1: Health Checks

```bash
# Basic health check
curl http://localhost:8080/actuator/health

# Detailed health with authentication
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/health
```

### Step 2: Custom Metrics

```bash
# View all available metrics
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/metrics

# Specific rule execution metrics
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/metrics/rule.execution.time

# JVM metrics
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Step 3: Prometheus Integration

```bash
# Get metrics in Prometheus format
curl -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  http://localhost:8080/actuator/prometheus
```

### Step 4: Log Analysis

Monitor application logs for:
- Rule execution times
- Error patterns
- Performance bottlenecks
- Security events

```bash
# Monitor logs in real-time
tail -f logs/application.log | grep "rule.execution"

# Search for errors
grep -i "error\|exception" logs/application.log | tail -20
```

---

## Tutorial 9: Deployment to Production

### Overview

Learn how to deploy the Drools application to production environments.

### Step 1: Build Production JAR

```bash
# Build with production profile
mvn clean package -Pprod

# Verify the build
java -jar target/spring-drools-integration-1.0.0.jar --version
```

### Step 2: Configure Production Properties

Create `application-prod.properties`:

```properties
# Server Configuration
server.port=8080
server.compression.enabled=true

# Security Configuration
spring.security.user.name=${ADMIN_USERNAME}
spring.security.user.password=${ADMIN_PASSWORD}

# Drools Configuration
app.drools.decision-table-path=file:/opt/drools/rules/decision-tables/
app.drools.hot-reload.enabled=false

# Logging Configuration
logging.level.com.example.springdroolsintegration=INFO
logging.file.name=/var/log/drools/application.log

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
```

### Step 3: Create Systemd Service

Create `/etc/systemd/system/drools-app.service`:

```ini
[Unit]
Description=Spring Boot Drools Integration
After=network.target

[Service]
Type=simple
User=drools
Group=drools
WorkingDirectory=/opt/drools
ExecStart=/usr/bin/java -Xmx2g -Xms1g -XX:+UseG1GC \
  -jar /opt/drools/spring-drools-integration-1.0.0.jar \
  --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

Environment=ADMIN_USERNAME=admin
Environment=ADMIN_PASSWORD=your-secure-password

[Install]
WantedBy=multi-user.target
```

### Step 4: Deploy and Start

```bash
# Create application user
sudo useradd -r -s /bin/false drools

# Create directories
sudo mkdir -p /opt/drools/rules/decision-tables
sudo mkdir -p /var/log/drools

# Copy files
sudo cp target/spring-drools-integration-1.0.0.jar /opt/drools/
sudo cp -r src/main/resources/rules/decision-tables/* /opt/drools/rules/decision-tables/

# Set permissions
sudo chown -R drools:drools /opt/drools
sudo chown -R drools:drools /var/log/drools

# Start service
sudo systemctl daemon-reload
sudo systemctl enable drools-app
sudo systemctl start drools-app

# Check status
sudo systemctl status drools-app
```

### Step 5: Verify Production Deployment

```bash
# Test health endpoint
curl http://your-server:8080/actuator/health

# Test API functionality
curl -u admin:your-password http://your-server:8080/api/v1/rules/status

# Monitor logs
sudo journalctl -u drools-app -f
```

---

## Advanced Tutorials

### Advanced Tutorial 1: Custom Rule Engine Configuration

Learn how to customize the Drools rule engine for specific requirements:

```java
@Configuration
public class CustomDroolsConfig {
    
    @Bean
    public KieContainer kieContainer() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // Custom configuration
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel("rules")
            .setDefault(true)
            .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
            .setEventProcessingMode(EventProcessingOption.STREAM);
            
        KieSessionModel kieSessionModel = kieBaseModel.newKieSessionModel("rulesSession")
            .setDefault(true)
            .setType(KieSessionModel.KieSessionType.STATEFUL)
            .setClockType(ClockTypeOption.get("realtime"));
        
        kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
        
        // Build and return container
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        return kieServices.newKieContainer(kieBuilder.getKieModule().getReleaseId());
    }
}
```

### Advanced Tutorial 2: Complex Decision Table Patterns

Learn advanced decision table patterns:

#### Pattern 1: Hierarchical Rules
```excel
| RuleSet | RuleId | Salience | Category | Subcategory | Amount | Discount |
|---------|--------|----------|----------|-------------|--------|----------|
| Rules | Electronics_Premium | 100 | ELECTRONICS | PREMIUM | >= 1000 | 20 |
| Rules | Electronics_Standard | 90 | ELECTRONICS | STANDARD | >= 500 | 15 |
| Rules | Electronics_Basic | 80 | ELECTRONICS | | >= 100 | 10 |
```

#### Pattern 2: Date-Based Rules
```excel
| RuleSet | RuleId | Salience | Start Date | End Date | Discount |
|---------|--------|----------|------------|----------|----------|
| Seasonal | Summer_Sale | 100 | 2025-06-01 | 2025-08-31 | 25 |
| Seasonal | Winter_Sale | 100 | 2025-12-01 | 2025-02-28 | 30 |
```

### Advanced Tutorial 3: Performance Tuning

Advanced performance optimization techniques:

#### 1. Rule Compilation Caching
```java
@Service
public class RuleCompilationCache {
    
    private final Map<String, KieContainer> containerCache = new ConcurrentHashMap<>();
    
    public KieContainer getOrCreateContainer(String ruleSetId, InputStream ruleStream) {
        return containerCache.computeIfAbsent(ruleSetId, id -> {
            // Compile and cache the container
            return compileRules(ruleStream);
        });
    }
}
```

#### 2. Session Pooling
```java
@Component
public class KieSessionPool {
    
    private final BlockingQueue<KieSession> sessionPool = new LinkedBlockingQueue<>();
    private final KieContainer kieContainer;
    
    public KieSession borrowSession() throws InterruptedException {
        KieSession session = sessionPool.poll(1, TimeUnit.SECONDS);
        return session != null ? session : kieContainer.newKieSession();
    }
    
    public void returnSession(KieSession session) {
        session.getFactHandles().forEach(session::delete);
        sessionPool.offer(session);
    }
}
```

### Advanced Tutorial 4: Integration Testing

Comprehensive integration testing strategies:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "app.drools.decision-table-path=classpath:test-rules/",
    "logging.level.com.example.springdroolsintegration=DEBUG"
})
class DroolsIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCalculateDiscountCorrectly() {
        // Given
        CustomerDiscountRequest request = createTestRequest();
        
        // When
        ResponseEntity<CustomerDiscountResponse> response = restTemplate
            .withBasicAuth("admin", "admin123")
            .postForEntity("/api/v1/discounts/calculate", request, CustomerDiscountResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDiscountPercentage()).isGreaterThan(0);
    }
    
    @Test
    void shouldHandleInvalidRequestGracefully() {
        // Test error handling scenarios
    }
    
    @Test
    void shouldPerformWithinAcceptableLimits() {
        // Performance testing
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // Execute multiple requests
        for (int i = 0; i < 100; i++) {
            restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity("/api/v1/discounts/calculate", createTestRequest(), 
                    CustomerDiscountResponse.class);
        }
        
        stopWatch.stop();
        assertThat(stopWatch.getTotalTimeMillis()).isLessThan(10000); // 10 seconds for 100 requests
    }
}
```

---

## 🎯 Best Practices Summary

### 1. Rule Design
- Keep rules simple and focused
- Use descriptive rule IDs
- Order rules by specificity (highest salience first)
- Document business logic clearly

### 2. Performance
- Use batch processing for multiple operations
- Implement proper caching strategies
- Monitor rule execution times
- Optimize JVM settings for production

### 3. Testing
- Test all rule scenarios
- Include edge cases and boundary conditions
- Use integration tests for end-to-end validation
- Implement performance testing

### 4. Deployment
- Use environment-specific configurations
- Implement proper monitoring and alerting
- Follow security best practices
- Plan for disaster recovery

### 5. Maintenance
- Version control decision tables
- Document all changes
- Regular performance reviews
- Keep dependencies updated

---

**These tutorials provide a comprehensive foundation for working with the Spring Boot Drools Integration application. Start with the basics and gradually work through the advanced topics as you become more comfortable with the system.**