# Client Examples - Spring Boot Drools Integration

This directory contains example client implementations demonstrating how to interact with the Spring Boot Drools Integration APIs in various programming languages.

## 📋 Available Examples

- [Java Spring Boot Client](java-spring-boot/) - Complete Spring Boot application client
- [JavaScript/Node.js Client](javascript-nodejs/) - Node.js client with Express.js
- [Python Client](python/) - Python client using requests library
- [cURL Examples](curl/) - Command-line examples using cURL
- [Postman Collection](postman/) - Postman collection for API testing

## 🚀 Quick Start

### Prerequisites

- Spring Boot Drools Integration application running on `http://localhost:8080`
- Default credentials: `admin` / `admin123`

### Basic API Test

```bash
# Test application health
curl -f http://localhost:8080/actuator/health

# Test customer discount calculation
curl -X POST http://localhost:8080/api/v1/discounts/calculate \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "customerId": "CUST001",
    "customerName": "John Doe",
    "age": 35,
    "loyaltyTier": "GOLD",
    "orderAmount": 1000.00,
    "orderItems": 5
  }'
```

## 📚 Example Categories

### 1. Rule Execution Examples
- Customer discount calculation
- Loan approval evaluation
- Product recommendations
- Generic rule execution

### 2. Rule Management Examples
- Upload decision tables
- Validate rules
- Reload rules
- Check rule engine status

### 3. Batch Processing Examples
- Batch discount calculations
- Batch loan evaluations
- Paginated results handling

### 4. Error Handling Examples
- Authentication errors
- Validation errors
- Server errors
- Network timeouts

## 🔧 Configuration

All examples use the following default configuration:

```
Base URL: http://localhost:8080
Username: admin
Password: admin123
Timeout: 30 seconds
```

To use different settings, modify the configuration in each example or set environment variables:

```bash
export DROOLS_API_BASE_URL=https://your-domain.com
export DROOLS_API_USERNAME=your-username
export DROOLS_API_PASSWORD=your-password
```

## 📖 Usage Instructions

Each example directory contains:
- **README.md** - Specific setup and usage instructions
- **Source code** - Complete working examples
- **Configuration files** - Environment-specific settings
- **Test data** - Sample request/response data

## 🧪 Testing

Most examples include test cases demonstrating:
- Successful API calls
- Error handling
- Authentication
- Data validation
- Performance testing

## 🤝 Contributing

To add a new client example:

1. Create a new directory for your language/framework
2. Include a comprehensive README.md
3. Provide working code examples
4. Add test cases
5. Update this main README.md

## 📞 Support

For issues with the examples:
- Check the main [Troubleshooting Guide](../docs/TROUBLESHOOTING.md)
- Review the [API Documentation](../docs/API_DOCUMENTATION.md)
- Create an issue in the repository

---

**Choose your preferred language/framework from the directories above to get started!**