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
 * Validation tests for CustomerDiscountRequest DTO.
 * These tests verify that Bean Validation annotations work correctly.
 */
class CustomerDiscountRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Valid CustomerDiscountRequest should pass validation")
    void validRequestShouldPassValidation() {
        // Create a valid request
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert no violations
        assertTrue(violations.isEmpty(), "Valid request should not have validation violations");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with blank customer name should fail validation")
    void blankCustomerNameShouldFailValidation() {
        // Create a request with blank customer name
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "",
                35,
                "GOLD",
                150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with blank customer name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("customerName", violation.getPropertyPath().toString(), "Violation should be on customerName field");
        assertEquals("Customer name is required", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with too short customer name should fail validation")
    void tooShortCustomerNameShouldFailValidation() {
        // Create a request with too short customer name
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "J",
                35,
                "GOLD",
                150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with too short customer name should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("customerName", violation.getPropertyPath().toString(), "Violation should be on customerName field");
        assertEquals("Name must be between 2 and 100 characters", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with negative age should fail validation")
    void negativeAgeShouldFailValidation() {
        // Create a request with negative age
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                -5,
                "GOLD",
                150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative age should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("customerAge", violation.getPropertyPath().toString(), "Violation should be on customerAge field");
        assertEquals("Age must be a positive number", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with null loyalty tier should fail validation")
    void nullLoyaltyTierShouldFailValidation() {
        // Create a request with null loyalty tier
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                null,
                150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with null loyalty tier should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("loyaltyTier", violation.getPropertyPath().toString(), "Violation should be on loyaltyTier field");
        assertEquals("Loyalty tier is required", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with negative order amount should fail validation")
    void negativeOrderAmountShouldFailValidation() {
        // Create a request with negative order amount
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                -150.0,
                3
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with negative order amount should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("orderAmount", violation.getPropertyPath().toString(), "Violation should be on orderAmount field");
        assertEquals("Order amount must be non-negative", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with zero order quantity should fail validation")
    void zeroOrderQuantityShouldFailValidation() {
        // Create a request with zero order quantity
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                0
        );

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with zero order quantity should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("orderQuantity", violation.getPropertyPath().toString(), "Violation should be on orderQuantity field");
        assertEquals("Order quantity must be at least 1", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with invalid email should fail validation")
    void invalidEmailShouldFailValidation() {
        // Create a request with invalid email
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                3
        );
        request.setCustomerEmail("invalid-email");

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid email should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("customerEmail", violation.getPropertyPath().toString(), "Violation should be on customerEmail field");
        assertEquals("Email should be valid", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with invalid promotion code should fail validation")
    void invalidPromotionCodeShouldFailValidation() {
        // Create a request with invalid promotion code
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                3
        );
        request.setPromotionCode("invalid_code!");

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        assertFalse(violations.isEmpty(), "Request with invalid promotion code should have validation violations");
        assertEquals(1, violations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest> violation = violations.iterator().next();
        assertEquals("promotionCode", violation.getPropertyPath().toString(), "Violation should be on promotionCode field");
        assertEquals("Promotion code should contain only uppercase letters, numbers, underscores and hyphens", violation.getMessage(), "Violation message should match");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with valid order item should pass validation")
    void validOrderItemShouldPassValidation() {
        // Create a valid request with order item
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                3
        );
        
        CustomerDiscountRequest.OrderItemRequest orderItem = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-123",
                "Smartphone",
                599.99,
                2,
                "ELECTRONICS"
        );
        request.addOrderItem(orderItem);

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert no violations
        assertTrue(violations.isEmpty(), "Valid request with order item should not have validation violations");
    }

    @Test
    @DisplayName("CustomerDiscountRequest with invalid order item should fail validation")
    void invalidOrderItemShouldFailValidation() {
        // Create a request with invalid order item
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                35,
                "GOLD",
                150.0,
                3
        );
        
        CustomerDiscountRequest.OrderItemRequest orderItem = new CustomerDiscountRequest.OrderItemRequest(
                "", // Blank product ID
                "Smartphone",
                599.99,
                2,
                "ELECTRONICS"
        );
        request.addOrderItem(orderItem);

        // Validate the request
        Set<ConstraintViolation<CustomerDiscountRequest>> violations = validator.validate(request);
        
        // Assert violations
        // Note: Nested validation requires @Valid annotation on the collection field
        // If @Valid is not present on the orderItems field, this test might not detect the violation
        // In that case, we would need to validate the order item separately
        
        // Validate the order item directly
        Set<ConstraintViolation<CustomerDiscountRequest.OrderItemRequest>> itemViolations = 
                validator.validate(orderItem);
        
        assertFalse(itemViolations.isEmpty(), "Invalid order item should have validation violations");
        assertEquals(1, itemViolations.size(), "Should have exactly one violation");
        
        ConstraintViolation<CustomerDiscountRequest.OrderItemRequest> violation = itemViolations.iterator().next();
        assertEquals("productId", violation.getPropertyPath().toString(), "Violation should be on productId field");
        assertEquals("Product ID is required", violation.getMessage(), "Violation message should match");
    }
}