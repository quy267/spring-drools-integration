# Spring Boot Drools Integration

A production-ready Spring Boot application demonstrating integration with Drools rule engine using spreadsheet decision tables. This project showcases best practices for building rule-based applications with clean architecture, comprehensive testing, and enterprise-grade features.

## 🚀 Features

- **Drools Rule Engine Integration**: Seamless integration with Drools 8.44.0.Final
- **Excel Decision Tables**: Business rules defined in Excel spreadsheets for easy maintenance
- **RESTful APIs**: Comprehensive REST endpoints for rule execution and management
- **Three Use Cases**: Customer discounts, loan approval, and product recommendations
- **Hot Reloading**: Dynamic rule updates without application restart (development mode)
- **Security**: Spring Security integration with proper authentication and authorization
- **Monitoring**: Actuator endpoints with custom health checks and metrics
- **Testing**: 80%+ code coverage with unit, integration, and load tests
- **Documentation**: OpenAPI/Swagger documentation for all endpoints

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Decision Tables](#decision-tables)
- [Usage Examples](#usage-examples)
- [Testing](#testing)
- [Monitoring](#monitoring)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## 🏃 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/quy267/spring-drools-integration.git
   cd spring-drools-integration
   ```

2. **Build the application**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Actuator Health: http://localhost:8080/actuator/health

## ⚙️ Configuration

### Application Properties

Key configuration properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Drools Configuration
app.drools.decision-table-path=classpath:rules/decision-tables/
app.drools.hot-reload.enabled=true
app.drools.hot-reload.watch-interval=5000

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
```

### Environment-Specific Configuration

- **Development**: `application.properties` (default)
- **Production**: `application-prod.properties`

## 🔌 API Endpoints

### Rule Execution APIs

#### Customer Discount Calculation
```http
POST /api/v1/customer-discount/calculate
Content-Type: application/json

{
  "customerId": "CUST001",
  "age": 35,
  "loyaltyTier": "GOLD",
  "orderAmount": 1000.00,
  "orderItems": 5
}
```

#### Loan Approval Evaluation
```http
POST /api/v1/loan-approval/evaluate
Content-Type: application/json

{
  "applicantId": "APP001",
  "creditScore": 750,
  "annualIncome": 75000.00,
  "loanAmount": 250000.00,
  "employmentYears": 5
}
```

#### Product Recommendations
```http
POST /api/v1/product-recommendation/recommend
Content-Type: application/json

{
  "customerId": "CUST001",
  "age": 35,
  "preferences": ["ELECTRONICS", "BOOKS"],
  "purchaseHistory": ["LAPTOP", "SMARTPHONE"],
  "budget": 500.00
}
```

### Rule Management APIs

#### Upload Decision Table
```http
POST /api/v1/rules/upload
Content-Type: multipart/form-data

file: [Excel file]
rulePackage: customer-discount
```

#### Validate Rules
```http
PUT /api/v1/rules/validate
```

#### Reload Rules
```http
POST /api/v1/rules/reload
```

#### Get Rule Status
```http
GET /api/v1/rules/status
```

### Generic Rule Execution
```http
POST /api/v1/rules/execute
Content-Type: application/json

{
  "rulePackage": "customer-discount",
  "facts": [
    {
      "factType": "Customer",
      "data": { ... }
    }
  ]
}
```

## 📊 Decision Tables

The application uses Excel decision tables for defining business rules. Decision tables are located in `src/main/resources/rules/decision-tables/`.

### Available Decision Tables

1. **CustomerDiscountRules.xlsx** - Customer discount calculation rules
2. **LoanApprovalRules.xlsx** - Loan approval evaluation rules
3. **ProductRecommendationRules.xlsx** - Product recommendation rules

### Decision Table Structure

Each decision table follows a standardized format:

| RuleTable | CustomerDiscountRules |  |  |  |
|-----------|----------------------|--|--|--|
| CONDITION | CONDITION | CONDITION | ACTION | ACTION |
| Customer Age | Loyalty Tier | Order Amount | Discount Percentage | Max Discount |
| age >= $1 | loyaltyTier == "$2" | orderAmount >= $3 | discountPercentage = $4 | maxDiscount = $5 |

### Creating Custom Decision Tables

1. Use the provided Excel templates
2. Follow the column naming conventions
3. Include proper validation rules
4. Save in the decision-tables directory
5. Upload via the Rule Management API

For detailed decision table documentation, see:
- [Decision Table Guide](src/main/resources/rules/decision-tables/DecisionTableGuide.md)
- [Customer Discount Rules](src/main/resources/rules/decision-tables/CustomerDiscountRules.md)
- [Loan Approval Rules](src/main/resources/rules/decision-tables/LoanApprovalRules.md)
- [Product Recommendation Rules](src/main/resources/rules/decision-tables/ProductRecommendationRules.md)

## 💡 Usage Examples

### Java Client Example

```java
@RestController
public class ExampleController {
    
    private final RestTemplate restTemplate;
    
    public ExampleController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request) {
        String url = "http://localhost:8080/api/v1/customer-discount/calculate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin123");
        
        HttpEntity<CustomerDiscountRequest> entity = new HttpEntity<>(request, headers);
        
        return restTemplate.postForObject(url, entity, CustomerDiscountResponse.class);
    }
}
```

### cURL Examples

```bash
# Calculate customer discount
curl -X POST http://localhost:8080/api/v1/customer-discount/calculate \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "customerId": "CUST001",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'

# Evaluate loan application
curl -X POST http://localhost:8080/api/v1/loan-approval/evaluate \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "applicantId": "APP001",
    "creditScore": 750,
    "annualIncome": 75000.00,
    "loanAmount": 250000.00,
    "employmentYears": 5
  }'

# Get product recommendations
curl -X POST http://localhost:8080/api/v1/product-recommendation/recommend \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "customerId": "CUST001",
    "age": 35,
    "preferences": ["ELECTRONICS", "BOOKS"],
    "purchaseHistory": ["LAPTOP", "SMARTPHONE"],
    "budget": 500.00
  }'
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test categories
mvn test -Dtest="**/*UnitTest"
mvn test -Dtest="**/*IntegrationTest"

# Run with coverage report
mvn test jacoco:report
```

### Test Categories

- **Unit Tests**: Service layer and utility classes
- **Integration Tests**: Full application context with TestContainers
- **Controller Tests**: Web layer testing with @WebMvcTest
- **Security Tests**: Authentication and authorization
- **Performance Tests**: Load testing with JMeter

### Coverage Reports

After running tests with coverage:
- HTML Report: `target/site/jacoco/index.html`
- XML Report: `target/site/jacoco/jacoco.xml`

## 📈 Monitoring

### Health Checks

The application provides comprehensive health checks:

```bash
# Overall health
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

### Custom Health Indicators

- **Drools Health**: Validates rule engine status
- **Decision Table Health**: Checks decision table loading
- **Rule Validation Health**: Verifies rule compilation

### Metrics

Available metrics endpoints:

```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# Specific metrics
curl http://localhost:8080/actuator/metrics/rule.execution.time
curl http://localhost:8080/actuator/metrics/rule.hit.rate
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

### Prometheus Integration

Metrics are available in Prometheus format:

```bash
curl http://localhost:8080/actuator/prometheus
```

## 🔒 Security

### Authentication

The application uses HTTP Basic Authentication:
- **Username**: admin
- **Password**: admin123

### Authorization

API endpoints are secured with role-based access:
- **Public**: Health check endpoints
- **Authenticated**: Rule execution endpoints
- **Admin**: Rule management endpoints

### Security Headers

The application includes security headers:
- X-Content-Type-Options
- X-Frame-Options
- X-XSS-Protection
- Strict-Transport-Security (HTTPS)

### Input Validation

All API inputs are validated using Bean Validation:
- Required field validation
- Format validation
- Range validation
- Custom business rule validation

## 🏗️ Architecture

### Package Structure

```
com.example.springdroolsintegration/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── service/         # Business services
├── model/           # Domain models and DTOs
│   ├── dto/         # Data Transfer Objects
│   ├── entity/      # Domain entities
│   └── request/     # API request models
├── exception/       # Custom exceptions and handlers
├── util/           # Utility classes
└── health/         # Custom health indicators
```

### Key Components

- **DroolsConfig**: Drools rule engine configuration
- **RuleExecutionService**: Core rule execution logic
- **DecisionTableProcessor**: Excel file processing
- **GlobalExceptionHandler**: Centralized error handling
- **SecurityConfig**: Security configuration

### Design Patterns

- **Dependency Injection**: Constructor-based injection
- **Strategy Pattern**: Rule execution strategies
- **Template Method**: Decision table processing
- **Observer Pattern**: Rule change notifications

## 🚀 Deployment

### Local Development

```bash
# Start with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Enable hot reloading
mvn spring-boot:run -Dapp.drools.hot-reload.enabled=true
```

### Production Deployment

```bash
# Build production JAR
mvn clean package -Pprod

# Run production JAR
java -jar target/spring-drools-integration-1.0.0.jar \
  --spring.profiles.active=prod
```

### Docker Deployment

```bash
# Build Docker image (when Dockerfile is available)
docker build -t spring-drools-integration:1.0.0 .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  spring-drools-integration:1.0.0
```

## 🛠️ Development

### Prerequisites for Development

- Java 17 JDK
- Maven 3.6+
- IDE with Spring Boot support (IntelliJ IDEA, Eclipse, VS Code)
- Git

### Setting up Development Environment

1. **Import project** into your IDE
2. **Configure JDK 17** as project SDK
3. **Enable annotation processing** for MapStruct
4. **Install required plugins**: Lombok, MapStruct
5. **Run tests** to verify setup

### Code Style

The project follows Spring Boot best practices:
- Constructor injection over field injection
- Package-private visibility where appropriate
- Comprehensive JavaDoc documentation
- SLF4J logging with structured output

### Contributing Guidelines

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Update documentation
6. Submit a pull request

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Drools Documentation](https://docs.drools.org/)
- [Apache POI Documentation](https://poi.apache.org/)
- [OpenAPI Specification](https://swagger.io/specification/)

## 🐛 Troubleshooting

### Common Issues

1. **Rule Compilation Errors**
   - Check decision table format
   - Verify column headers match expected format
   - Review rule syntax in generated DRL

2. **Authentication Issues**
   - Verify credentials (admin/admin123)
   - Check security configuration
   - Ensure proper headers in requests

3. **Performance Issues**
   - Monitor rule execution metrics
   - Check memory usage
   - Review rule complexity

4. **File Upload Issues**
   - Verify file format (Excel .xlsx)
   - Check file size limits
   - Ensure proper multipart configuration

For detailed troubleshooting, see [Troubleshooting Guide](docs/TROUBLESHOOTING.md).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation in the `docs/` directory
- Review the example implementations

---

**Built with ❤️ using Spring Boot and Drools**