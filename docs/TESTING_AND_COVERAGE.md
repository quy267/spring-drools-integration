# Testing and Code Coverage Configuration

## Overview

This document outlines the testing strategy and code coverage configuration for the Spring Boot Drools Integration project. It includes details about JaCoCo configuration, exclusions, and rationale for coverage measurement decisions.

## Code Coverage Configuration

### JaCoCo Setup

The project uses JaCoCo (Java Code Coverage) version 0.8.10 for measuring code coverage. The configuration is defined in the `pom.xml` file with the following goals:

- **prepare-agent**: Prepares the JaCoCo runtime agent for coverage data collection
- **report**: Generates coverage reports in HTML, XML, and CSV formats
- **check**: Validates coverage against defined thresholds (80% minimum)

### Coverage Target

- **Overall Coverage Target**: 80% minimum instruction coverage
- **Service Layer Target**: 90% coverage (as per requirements)

### JaCoCo Exclusions

The following classes and packages are excluded from coverage analysis:

#### 1. Drools Internal Classes

```xml
<exclude>org/drools/drl/parser/lang/*</exclude>
<exclude>org/drools/compiler/**/*</exclude>
<exclude>org/kie/**/*</exclude>
```

**Rationale**: 
- These are internal Drools framework classes that cause JaCoCo instrumentation failures
- Drools lexer and compiler classes contain generated code that cannot be properly instrumented
- These classes are part of the Drools framework, not application business logic
- Excluding them prevents build failures while maintaining meaningful coverage metrics for application code

#### 2. MapStruct Generated Classes

```xml
<exclude>**/*MapperImpl.class</exclude>
```

**Rationale**:
- MapStruct generates implementation classes at compile time
- Generated code should not be included in coverage metrics as it's not manually written
- These implementations are automatically generated and tested through the mapper interface usage
- Excluding them provides more accurate coverage metrics for hand-written code

## Coverage Reports

### Report Generation

Coverage reports are generated in multiple formats:

- **HTML Report**: `target/site/jacoco/index.html` - Interactive web-based report
- **XML Report**: `target/site/jacoco/jacoco.xml` - Machine-readable format for CI/CD integration
- **CSV Report**: `target/site/jacoco/jacoco.csv` - Spreadsheet-compatible format

### Manual Report Generation

To generate coverage reports manually:

```bash
# Run tests and generate reports
./mvnw clean test

# Generate reports from existing coverage data
./mvnw jacoco:report
```

### Coverage Data

Coverage execution data is stored in `target/jacoco.exec` and is used to generate all report formats.

## Testing Strategy

### Unit Testing

- **Framework**: JUnit 5 with AssertJ for assertions
- **Mocking**: Mockito for test doubles
- **Test Slices**: Spring Boot test slices (@WebMvcTest, @DataJpaTest, etc.)

### Integration Testing

- **TestContainers**: For database integration tests
- **Spring Boot Test**: Full application context testing
- **MockMvc**: For web layer integration testing

### Test Configuration

- **Test Profiles**: `application-test.properties` for test-specific configuration
- **Test Configuration**: `@TestConfiguration` classes for test-specific beans
- **Test Data**: Builders and factories for consistent test data creation

## Compatibility Issues Resolved

### JaCoCo and Drools Compatibility

**Problem**: JaCoCo 0.8.10 had instrumentation failures when analyzing Drools 8.44.0.Final classes, specifically:
- Drools DRL parser language classes
- Drools compiler internal classes  
- KIE framework classes

**Solution**: Added specific exclusions to prevent instrumentation of problematic classes while maintaining coverage for application code.

**Impact**: 
- Build process now completes successfully
- Coverage reports are generated without errors
- Application code coverage is accurately measured
- Framework code is appropriately excluded

## Best Practices

### Coverage Guidelines

1. **Focus on Business Logic**: Prioritize coverage for service layer and business logic
2. **Exclude Generated Code**: Don't measure coverage for auto-generated classes
3. **Exclude Framework Code**: Don't include third-party framework classes in coverage
4. **Test Meaningful Scenarios**: Write tests that cover real business scenarios, not just coverage targets

### Test Organization

1. **Package Structure**: Mirror main package structure in test packages
2. **Test Naming**: Use descriptive test method names that explain the scenario
3. **Test Data**: Use builders and factories for consistent test data
4. **Assertions**: Use AssertJ for readable and maintainable assertions

## Troubleshooting

### Common Issues

1. **Coverage Reports Not Generated**: Ensure tests run successfully before report generation
2. **Low Coverage**: Focus on service layer and business logic first
3. **Build Failures**: Check for new Drools classes that might need exclusion

### Verification Commands

```bash
# Verify JaCoCo configuration
./mvnw help:effective-pom | grep -A 20 jacoco

# Check coverage data file
ls -la target/jacoco.exec

# Verify report generation
ls -la target/site/jacoco/

# Run coverage check
./mvnw jacoco:check
```

## Configuration History

### Version 1.0 (2025-07-23)
- Initial JaCoCo configuration with Drools exclusions
- Added MapStruct generated class exclusions
- Set 80% coverage threshold
- Resolved compatibility issues with Drools 8.44.0.Final

## References

- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [Drools Documentation](https://docs.drools.org/)
- [MapStruct Documentation](https://mapstruct.org/)