# Java Spring Boot Client Example

This example demonstrates how to create a Spring Boot client application that interacts with the Spring Boot Drools Integration APIs.

## 📋 Overview

This client application provides:
- RESTful service integration with the Drools API
- Comprehensive error handling
- Configuration management
- Unit and integration tests
- Example usage of all major API endpoints

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Spring Boot Drools Integration application running on `http://localhost:8080`

### Build and Run

```bash
# Navigate to the example directory
cd examples/java-spring-boot

# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run the JAR
mvn clean package
java -jar target/drools-client-example-1.0.0.jar
```

### Test the Client

```bash
# Test customer discount calculation
curl http://localhost:8081/api/client/discount/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'

# Test loan approval
curl http://localhost:8081/api/client/loan/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "APP001",
    "applicantName": "John Doe",
    "age": 35,
    "creditScore": 750,
    "annualIncome": 75000.00,
    "loanAmount": 250000.00,
    "employmentYears": 5
  }'
```

## 📁 Project Structure

```
java-spring-boot/
├── pom.xml                           # Maven configuration
├── src/main/java/
│   └── com/example/droolsclient/
│       ├── DroolsClientApplication.java    # Main application class
│       ├── config/
│       │   ├── DroolsApiConfig.java        # API configuration
│       │   └── RestTemplateConfig.java     # HTTP client configuration
│       ├── service/
│       │   ├── DroolsApiClient.java        # Main API client service
│       │   ├── CustomerDiscountClient.java # Customer discount operations
│       │   ├── LoanApprovalClient.java     # Loan approval operations
│       │   └── ProductRecommendationClient.java # Product recommendations
│       ├── controller/
│       │   └── ClientController.java       # Example REST endpoints
│       ├── model/
│       │   ├── request/                    # Request DTOs
│       │   └── response/                   # Response DTOs
│       └── exception/
│           └── DroolsApiException.java     # Custom exceptions
├── src/main/resources/
│   ├── application.yml                     # Application configuration
│   └── application-test.yml                # Test configuration
└── src/test/java/
    └── com/example/droolsclient/
        ├── service/                        # Service tests
        └── integration/                    # Integration tests
```

## ⚙️ Configuration

### Application Configuration

```yaml
# application.yml
drools:
  api:
    base-url: http://localhost:8080
    username: admin
    password: admin123
    timeout: 30s
    retry:
      max-attempts: 3
      delay: 1s

server:
  port: 8081

logging:
  level:
    com.example.droolsclient: DEBUG
    org.springframework.web.client: DEBUG
```

### Environment Variables

```bash
export DROOLS_API_BASE_URL=https://your-domain.com
export DROOLS_API_USERNAME=your-username
export DROOLS_API_PASSWORD=your-password
```

## 🔧 Key Components

### 1. DroolsApiClient Service

Main service class that handles all API interactions:

```java
@Service
public class DroolsApiClient {
    
    private final RestTemplate restTemplate;
    private final DroolsApiConfig config;
    
    public DroolsApiClient(RestTemplate restTemplate, DroolsApiConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }
    
    public <T> T post(String endpoint, Object request, Class<T> responseType) {
        String url = config.getBaseUrl() + endpoint;
        HttpEntity<?> entity = createHttpEntity(request);
        
        try {
            return restTemplate.postForObject(url, entity, responseType);
        } catch (RestClientException e) {
            throw new DroolsApiException("API call failed: " + endpoint, e);
        }
    }
    
    private HttpEntity<?> createHttpEntity(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(config.getUsername(), config.getPassword());
        return new HttpEntity<>(request, headers);
    }
}
```

### 2. Customer Discount Client

Specialized client for customer discount operations:

```java
@Service
public class CustomerDiscountClient {
    
    private final DroolsApiClient apiClient;
    
    public CustomerDiscountClient(DroolsApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request) {
        return apiClient.post("/api/v1/discounts/calculate", request, CustomerDiscountResponse.class);
    }
    
    public PagedResponse<CustomerDiscountResponse> calculateDiscountBatch(
            List<CustomerDiscountRequest> requests, int page, int size) {
        
        String endpoint = String.format("/api/v1/discounts/batch?page=%d&size=%d", page, size);
        return apiClient.post(endpoint, requests, 
            new ParameterizedTypeReference<PagedResponse<CustomerDiscountResponse>>() {});
    }
    
    public Map<String, Object> getDiscountStatistics() {
        return apiClient.get("/api/v1/discounts/statistics", Map.class);
    }
}
```

### 3. Error Handling

Comprehensive error handling with custom exceptions:

```java
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class DroolsApiException extends RuntimeException {
    
    private final String endpoint;
    private final int statusCode;
    
    public DroolsApiException(String message, Throwable cause) {
        super(message, cause);
        this.endpoint = extractEndpoint(cause);
        this.statusCode = extractStatusCode(cause);
    }
    
    // Additional error handling methods...
}

@RestControllerAdvice
public class ClientExceptionHandler {
    
    @ExceptionHandler(DroolsApiException.class)
    public ResponseEntity<ErrorResponse> handleDroolsApiException(DroolsApiException e) {
        ErrorResponse error = ErrorResponse.builder()
            .message("Drools API error: " + e.getMessage())
            .endpoint(e.getEndpoint())
            .statusCode(e.getStatusCode())
            .timestamp(Instant.now())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }
}
```

## 🧪 Testing

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class CustomerDiscountClientTest {
    
    @Mock
    private DroolsApiClient apiClient;
    
    @InjectMocks
    private CustomerDiscountClient discountClient;
    
    @Test
    void shouldCalculateDiscount() {
        // Given
        CustomerDiscountRequest request = createDiscountRequest();
        CustomerDiscountResponse expectedResponse = createDiscountResponse();
        
        when(apiClient.post(eq("/api/v1/discounts/calculate"), eq(request), 
            eq(CustomerDiscountResponse.class))).thenReturn(expectedResponse);
        
        // When
        CustomerDiscountResponse response = discountClient.calculateDiscount(request);
        
        // Then
        assertThat(response).isEqualTo(expectedResponse);
        verify(apiClient).post("/api/v1/discounts/calculate", request, CustomerDiscountResponse.class);
    }
}
```

### Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "drools.api.base-url=http://localhost:8080",
    "drools.api.username=admin",
    "drools.api.password=admin123"
})
class DroolsApiIntegrationTest {
    
    @Autowired
    private CustomerDiscountClient discountClient;
    
    @Test
    void shouldCalculateDiscountIntegration() {
        // Given
        CustomerDiscountRequest request = CustomerDiscountRequest.builder()
            .customerId("CUST001")
            .customerName("John Doe")
            .age(35)
            .loyaltyTier("GOLD")
            .orderAmount(BigDecimal.valueOf(1000))
            .orderItems(5)
            .build();
        
        // When
        CustomerDiscountResponse response = discountClient.calculateDiscount(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo("CUST001");
        assertThat(response.getDiscountPercentage()).isGreaterThan(0);
    }
}
```

## 📊 Usage Examples

### Example 1: Simple Discount Calculation

```java
@RestController
@RequestMapping("/api/client")
public class ClientController {
    
    private final CustomerDiscountClient discountClient;
    
    @PostMapping("/discount/calculate")
    public ResponseEntity<CustomerDiscountResponse> calculateDiscount(
            @RequestBody CustomerDiscountRequest request) {
        
        try {
            CustomerDiscountResponse response = discountClient.calculateDiscount(request);
            return ResponseEntity.ok(response);
        } catch (DroolsApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
```

### Example 2: Batch Processing with Pagination

```java
@PostMapping("/discount/batch")
public ResponseEntity<PagedResponse<CustomerDiscountResponse>> calculateDiscountBatch(
        @RequestBody List<CustomerDiscountRequest> requests,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    
    try {
        PagedResponse<CustomerDiscountResponse> response = 
            discountClient.calculateDiscountBatch(requests, page, size);
        return ResponseEntity.ok(response);
    } catch (DroolsApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }
}
```

### Example 3: Async Processing

```java
@Service
public class AsyncDiscountService {
    
    private final CustomerDiscountClient discountClient;
    
    @Async
    public CompletableFuture<CustomerDiscountResponse> calculateDiscountAsync(
            CustomerDiscountRequest request) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return discountClient.calculateDiscount(request);
            } catch (DroolsApiException e) {
                throw new CompletionException(e);
            }
        });
    }
}
```

## 🔍 Monitoring and Observability

### Health Checks

```java
@Component
public class DroolsApiHealthIndicator implements HealthIndicator {
    
    private final DroolsApiClient apiClient;
    
    @Override
    public Health health() {
        try {
            apiClient.get("/actuator/health", Map.class);
            return Health.up()
                .withDetail("drools-api", "Available")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("drools-api", "Unavailable")
                .withException(e)
                .build();
        }
    }
}
```

### Metrics

```java
@Component
public class DroolsApiMetrics {
    
    private final Counter apiCallCounter;
    private final Timer apiCallTimer;
    
    public DroolsApiMetrics(MeterRegistry meterRegistry) {
        this.apiCallCounter = Counter.builder("drools.api.calls")
            .description("Number of API calls to Drools service")
            .register(meterRegistry);
            
        this.apiCallTimer = Timer.builder("drools.api.call.duration")
            .description("Duration of API calls to Drools service")
            .register(meterRegistry);
    }
    
    public <T> T recordApiCall(String operation, Supplier<T> apiCall) {
        return Timer.Sample.start(apiCallTimer)
            .stop(apiCallTimer.tag("operation", operation))
            .recordCallable(() -> {
                apiCallCounter.increment(Tags.of("operation", operation));
                return apiCall.get();
            });
    }
}
```

## 🚀 Advanced Features

### Retry Logic

```java
@Configuration
@EnableRetry
public class RetryConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .exponentialBackoff(1000, 2, 10000)
            .retryOn(RestClientException.class)
            .build();
    }
}

@Service
public class ResilientDroolsApiClient {
    
    private final RetryTemplate retryTemplate;
    private final DroolsApiClient apiClient;
    
    @Retryable(value = {RestClientException.class}, maxAttempts = 3)
    public <T> T postWithRetry(String endpoint, Object request, Class<T> responseType) {
        return retryTemplate.execute(context -> 
            apiClient.post(endpoint, request, responseType));
    }
}
```

### Circuit Breaker

```java
@Component
public class CircuitBreakerDroolsClient {
    
    private final CircuitBreaker circuitBreaker;
    private final DroolsApiClient apiClient;
    
    public CircuitBreakerDroolsClient(DroolsApiClient apiClient) {
        this.apiClient = apiClient;
        this.circuitBreaker = CircuitBreaker.ofDefaults("drools-api");
    }
    
    public <T> T callWithCircuitBreaker(String endpoint, Object request, Class<T> responseType) {
        Supplier<T> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> apiClient.post(endpoint, request, responseType));
        
        return decoratedSupplier.get();
    }
}
```

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [RestTemplate Guide](https://spring.io/guides/gs/consuming-rest/)
- [Spring Retry](https://github.com/spring-projects/spring-retry)
- [Resilience4j](https://resilience4j.readme.io/)

## 🤝 Contributing

To extend this example:

1. Add new service clients for additional endpoints
2. Implement additional error handling scenarios
3. Add more comprehensive tests
4. Include performance testing examples

---

**This example provides a solid foundation for building Spring Boot applications that integrate with the Drools API.**