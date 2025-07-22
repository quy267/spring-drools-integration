package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional integration tests for CustomerDiscountService focusing on:
 * 1. Combination of multiple discount factors
 * 2. Edge cases at the boundaries
 * 3. Error scenarios
 */
@SpringBootTest
@ActiveProfiles("test")
public class CustomerDiscountServiceAdditionalIntegrationTest {

    @Autowired
    private CustomerDiscountService customerDiscountService;

    @Test
    @DisplayName("Test discount for customer with multiple discount factors - Student with medium order")
    void testDiscountForStudentWithMediumOrder() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("John Doe", 20, "BRONZE", 150.0, 5);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getDiscountPercentage() > 0);
        // Student discount (5%) + Medium order discount (3%)
        assertEquals(8.0, response.getDiscountPercentage());
        assertEquals(12.0, response.getDiscountAmount());
        assertEquals(138.0, response.getFinalAmount());
        assertEquals("Student Discount, Medium Order Discount", response.getAppliedRules());
    }

    @Test
    @DisplayName("Test discount for customer with multiple discount factors - Senior with bulk order")
    void testDiscountForSeniorWithBulkOrder() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Jane Smith", 65, "BRONZE", 300.0, 25);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getDiscountPercentage() > 0);
        // Senior discount (10%) + Bronze tier (5%) + Large order (5%) + Bulk order (8%)
        assertEquals(28.0, response.getDiscountPercentage());
        assertEquals(84.0, response.getDiscountAmount());
        assertEquals(216.0, response.getFinalAmount());
        assertEquals("Senior Discount, Bronze Tier Discount, Large Order Discount, Bulk Order Discount", response.getAppliedRules());
    }

    @ParameterizedTest
    @DisplayName("Test discount at age boundaries")
    @CsvSource({
        "17, 5.0, Child Discount",  // Child discount (< 18)
        "18, 5.0, Student Discount", // Student discount (18-25)
        "25, 5.0, Student Discount", // Student discount (18-25)
        "26, 0.0, No discount",     // No age-based discount
        "60, 10.0, Senior Discount", // Senior discount (> 60)
        "61, 10.0, Senior Discount"  // Senior discount (> 60)
    })
    void testDiscountAtAgeBoundaries(int age, double expectedDiscountPercentage, String expectedRule) {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", age, "BRONZE", 50.0, 1);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        assertEquals(expectedDiscountPercentage, response.getDiscountPercentage());
        if (expectedDiscountPercentage > 0) {
            assertTrue(response.getAppliedRules().contains(expectedRule));
        } else {
            assertEquals("No discount applied", response.getAppliedRules());
        }
    }

    @ParameterizedTest
    @DisplayName("Test discount at order amount boundaries")
    @CsvSource({
        "99.99, 0.0, No discount",      // Below medium order threshold
        "100.0, 3.0, Medium Order Discount", // Exactly at medium order threshold
        "199.99, 3.0, Medium Order Discount", // Just below large order threshold
        "200.0, 5.0, Large Order Discount"   // Exactly at large order threshold
    })
    void testDiscountAtOrderAmountBoundaries(double orderAmount, double expectedDiscountPercentage, String expectedRule) {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 30, "BRONZE", orderAmount, 1);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        if (expectedDiscountPercentage > 0) {
            assertEquals(expectedDiscountPercentage, response.getDiscountPercentage());
            assertTrue(response.getAppliedRules().contains(expectedRule));
        } else {
            assertEquals(5.0, response.getDiscountPercentage()); // Bronze tier discount still applies
            assertEquals("Bronze Tier Discount", response.getAppliedRules());
        }
    }

    @ParameterizedTest
    @DisplayName("Test discount at order quantity boundaries")
    @CsvSource({
        "9, 0.0, No discount",      // Below medium quantity threshold
        "10, 5.0, Medium Quantity Discount", // Exactly at medium quantity threshold
        "19, 5.0, Medium Quantity Discount", // Just below bulk order threshold
        "20, 8.0, Bulk Order Discount"   // Exactly at bulk order threshold
    })
    void testDiscountAtOrderQuantityBoundaries(int orderQuantity, double expectedDiscountPercentage, String expectedRule) {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 30, "BRONZE", 50.0, orderQuantity);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        if (expectedDiscountPercentage > 0) {
            assertTrue(response.getDiscountPercentage() >= expectedDiscountPercentage);
            assertTrue(response.getAppliedRules().contains(expectedRule));
        } else {
            assertEquals(5.0, response.getDiscountPercentage()); // Bronze tier discount still applies
            assertEquals("Bronze Tier Discount", response.getAppliedRules());
        }
    }

    @Test
    @DisplayName("Test discount with premium customer (Senior with Gold tier)")
    void testDiscountForPremiumCustomer() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Premium Customer", 65, "GOLD", 50.0, 1);

        // Act
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Assert
        assertNotNull(response);
        assertEquals(20.0, response.getDiscountPercentage()); // Premium customer discount
        assertEquals(10.0, response.getDiscountAmount());
        assertEquals(40.0, response.getFinalAmount());
        assertEquals("Premium Customer Discount", response.getAppliedRules());
    }

    @Test
    @DisplayName("Test error handling with null request")
    void testErrorHandlingWithNullRequest() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerDiscountService.calculateDiscount(null);
        });
        
        assertTrue(exception.getMessage().contains("Request cannot be null"));
    }

    @Test
    @DisplayName("Test error handling with negative age")
    void testErrorHandlingWithNegativeAge() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", -5, "BRONZE", 50.0, 1);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerDiscountService.calculateDiscount(request);
        });
        
        assertTrue(exception.getMessage().contains("Age must be positive"));
    }

    @Test
    @DisplayName("Test error handling with negative order amount")
    void testErrorHandlingWithNegativeOrderAmount() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 30, "BRONZE", -50.0, 1);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerDiscountService.calculateDiscount(request);
        });
        
        assertTrue(exception.getMessage().contains("Order amount must be positive"));
    }

    @Test
    @DisplayName("Test error handling with negative order quantity")
    void testErrorHandlingWithNegativeOrderQuantity() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 30, "BRONZE", 50.0, -1);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerDiscountService.calculateDiscount(request);
        });
        
        assertTrue(exception.getMessage().contains("Order quantity must be positive"));
    }

    @Test
    @DisplayName("Test error handling with invalid loyalty tier")
    void testErrorHandlingWithInvalidLoyaltyTier() {
        // Arrange
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 30, "INVALID_TIER", 50.0, 1);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customerDiscountService.calculateDiscount(request);
        });
        
        assertTrue(exception.getMessage().contains("Invalid loyalty tier"));
    }

    /**
     * Helper method to create a discount request
     */
    private CustomerDiscountRequest createDiscountRequest(String customerName, int age, String loyaltyTier, double orderAmount, int orderQuantity) {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerName(customerName);
        request.setCustomerAge(age);
        request.setLoyaltyTier(loyaltyTier);
        request.setOrderAmount(orderAmount);
        request.setOrderQuantity(orderQuantity);
        request.setOrderItems(new ArrayList<>());
        return request;
    }
}