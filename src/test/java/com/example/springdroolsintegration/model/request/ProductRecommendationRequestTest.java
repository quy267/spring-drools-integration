package com.example.springdroolsintegration.model.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for ProductRecommendationRequest DTO.
 * These tests verify that Bean Validation annotations work correctly.
 */
class ProductRecommendationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid ProductRecommendationRequest should pass validation")
    void validRequestShouldPassValidation() {
        // Create a valid request
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(35);
        request.setGender("M");

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert no violations
        assertTrue(violations.isEmpty(), "Valid request should not have validation violations");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too short first name should fail validation")
    void tooShortFirstNameShouldFailValidation() {
        // Create a request with too short first name
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("J"); // Too short
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too short first name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("firstName", violation.getPropertyPath().toString(), "Violation should be on firstName field");
        assertEquals("First name must be between 2 and 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too short last name should fail validation")
    void tooShortLastNameShouldFailValidation() {
        // Create a request with too short last name
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("D"); // Too short
        request.setEmail("john.doe@example.com");

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too short last name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("lastName", violation.getPropertyPath().toString(), "Violation should be on lastName field");
        assertEquals("Last name must be between 2 and 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with invalid email should fail validation")
    void invalidEmailShouldFailValidation() {
        // Create a request with invalid email
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email"); // Invalid email

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid email should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString(), "Violation should be on email field");
        assertEquals("Email should be valid", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with negative age should fail validation")
    void negativeAgeShouldFailValidation() {
        // Create a request with negative age
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(-5); // Negative age

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative age should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("age", violation.getPropertyPath().toString(), "Violation should be on age field");
        assertEquals("Age must be non-negative", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with invalid gender should fail validation")
    void invalidGenderShouldFailValidation() {
        // Create a request with invalid gender
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setGender("X"); // Invalid gender (not M, F, or O)

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid gender should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("gender", violation.getPropertyPath().toString(), "Violation should be on gender field");
        assertEquals("Gender must be M, F, or O", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with invalid account number should fail validation")
    void invalidAccountNumberShouldFailValidation() {
        // Create a request with invalid account number
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAccountNumber("CUST1"); // Too short (only 5 characters, needs 6-10)

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid account number should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("accountNumber", violation.getPropertyPath().toString(), "Violation should be on accountNumber field");
        assertEquals("Account number must be 6-10 alphanumeric characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too long city should fail validation")
    void tooLongCityShouldFailValidation() {
        // Create a request with too long city
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        
        // Create a city name that's too long (> 50 characters)
        StringBuilder cityBuilder = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            cityBuilder.append("a");
        }
        request.setCity(cityBuilder.toString());

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too long city should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("city", violation.getPropertyPath().toString(), "Violation should be on city field");
        assertEquals("City cannot exceed 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too long state should fail validation")
    void tooLongStateShouldFailValidation() {
        // Create a request with too long state
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        
        // Create a state name that's too long (> 50 characters)
        StringBuilder stateBuilder = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            stateBuilder.append("a");
        }
        request.setState(stateBuilder.toString());

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too long state should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("state", violation.getPropertyPath().toString(), "Violation should be on state field");
        assertEquals("State cannot exceed 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too long country should fail validation")
    void tooLongCountryShouldFailValidation() {
        // Create a request with too long country
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        
        // Create a country name that's too long (> 50 characters)
        StringBuilder countryBuilder = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            countryBuilder.append("a");
        }
        request.setCountry(countryBuilder.toString());

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too long country should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("country", violation.getPropertyPath().toString(), "Violation should be on country field");
        assertEquals("Country cannot exceed 50 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with negative max recommendations should fail validation")
    void negativeMaxRecommendationsShouldFailValidation() {
        // Create a request with negative max recommendations
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                -1 // Negative max recommendations
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative max recommendations should have validation violations");
        
        // Find the violation for maxRecommendations
        boolean foundMaxRecommendationsViolation = false;
        for (ConstraintViolation<ProductRecommendationRequest> violation : violations) {
            if (violation.getPropertyPath().toString().equals("maxRecommendations")) {
                foundMaxRecommendationsViolation = true;
                break;
            }
        }
        
        assertTrue(foundMaxRecommendationsViolation, "Should have a violation on maxRecommendations field");
    }

    @Test
    @DisplayName("ProductRecommendationRequest with too long zip code should fail validation")
    void tooLongZipCodeShouldFailValidation() {
        // Create a request with too long zip code
        ProductRecommendationRequest request = new ProductRecommendationRequest(
                1001L,
                "PROD-123",
                "PERSONALIZED",
                5
        );
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        
        // Create a zip code that's too long (> 10 characters)
        request.setZipCode("12345678901");

        // Validate the request
        Set<ConstraintViolation<ProductRecommendationRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too long zip code should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<ProductRecommendationRequest> violation = violations.iterator().next();
        assertEquals("zipCode", violation.getPropertyPath().toString(), "Violation should be on zipCode field");
        assertEquals("Zip code cannot exceed 10 characters", violation.getMessage(), "Violation message should match");
    }
}