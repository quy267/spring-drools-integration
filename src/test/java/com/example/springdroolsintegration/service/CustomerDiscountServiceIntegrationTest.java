package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CustomerDiscountService.
 * These tests verify that the customer discount service can calculate discounts correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
public class CustomerDiscountServiceIntegrationTest {

    @Autowired
    private CustomerDiscountService customerDiscountService;

    @Test
    @DisplayName("Test discount calculation for senior customer with gold tier")
    public void testDiscountForSeniorGoldCustomer() {
        // Create a request for a senior customer with gold tier
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "John Doe",
                65,
                "GOLD",
                250.0,
                3
        );
        request.setCustomerEmail("john.doe@example.com");

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("John Doe", response.getCustomerName(), "Customer name should match");
        assertEquals("GOLD", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(250.0, response.getOriginalAmount(), "Original amount should match");
        assertTrue(response.getDiscountPercentage() > 0, "Discount percentage should be greater than 0");
        assertTrue(response.getDiscountAmount() > 0, "Discount amount should be greater than 0");
        assertTrue(response.getFinalAmount() < response.getOriginalAmount(), "Final amount should be less than original amount");
        assertNotNull(response.getAppliedRules(), "Applied rules should not be null");
    }

    @Test
    @DisplayName("Test discount calculation for young customer with silver tier")
    public void testDiscountForYoungSilverCustomer() {
        // Create a request for a young customer with silver tier
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "Jane Smith",
                25,
                "SILVER",
                150.0,
                2
        );
        request.setCustomerEmail("jane.smith@example.com");

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Jane Smith", response.getCustomerName(), "Customer name should match");
        assertEquals("SILVER", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(150.0, response.getOriginalAmount(), "Original amount should match");
        // We don't assert specific discount values as they depend on the rules
    }

    @Test
    @DisplayName("Test discount calculation for child customer")
    public void testDiscountForChildCustomer() {
        // Create a request for a child customer
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "Child Customer",
                12,
                "BRONZE",
                50.0,
                1
        );

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Child Customer", response.getCustomerName(), "Customer name should match");
        assertEquals("BRONZE", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(50.0, response.getOriginalAmount(), "Original amount should match");
        // We don't assert specific discount values as they depend on the rules
    }

    @Test
    @DisplayName("Test discount calculation for large order")
    public void testDiscountForLargeOrder() {
        // Create a request for a large order
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "Large Order Customer",
                40,
                "BRONZE",
                500.0,
                5
        );

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Large Order Customer", response.getCustomerName(), "Customer name should match");
        assertEquals("BRONZE", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(500.0, response.getOriginalAmount(), "Original amount should match");
        assertTrue(response.getDiscountPercentage() > 0, "Discount percentage should be greater than 0");
        assertTrue(response.getDiscountAmount() > 0, "Discount amount should be greater than 0");
        assertTrue(response.getFinalAmount() < response.getOriginalAmount(), "Final amount should be less than original amount");
    }

    @Test
    @DisplayName("Test discount calculation for bulk order")
    public void testDiscountForBulkOrder() {
        // Create a request for a bulk order
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "Bulk Order Customer",
                40,
                "BRONZE",
                300.0,
                25
        );

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Bulk Order Customer", response.getCustomerName(), "Customer name should match");
        assertEquals("BRONZE", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(300.0, response.getOriginalAmount(), "Original amount should match");
        assertTrue(response.getDiscountPercentage() > 0, "Discount percentage should be greater than 0");
        assertTrue(response.getDiscountAmount() > 0, "Discount amount should be greater than 0");
        assertTrue(response.getFinalAmount() < response.getOriginalAmount(), "Final amount should be less than original amount");
    }

    @Test
    @DisplayName("Test discount calculation with order items")
    public void testDiscountWithOrderItems() {
        // Create a request with order items
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                "Order Items Customer",
                35,
                "SILVER",
                200.0,
                3
        );
        
        // Add order items
        CustomerDiscountRequest.OrderItemRequest item1 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-123",
                "Smartphone",
                599.99,
                1,
                "ELECTRONICS"
        );
        CustomerDiscountRequest.OrderItemRequest item2 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-456",
                "Headphones",
                99.99,
                2,
                "ELECTRONICS"
        );
        request.addOrderItem(item1);
        request.addOrderItem(item2);

        // Calculate discount
        CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Order Items Customer", response.getCustomerName(), "Customer name should match");
        assertEquals("SILVER", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(200.0, response.getOriginalAmount(), "Original amount should match");
        // We don't assert specific discount values as they depend on the rules
    }

    @Test
    @DisplayName("Test batch discount calculation")
    public void testCalculateDiscountBatch() {
        // Create a list of requests
        List<CustomerDiscountRequest> requests = new ArrayList<>();
        
        // Add different types of discount requests
        requests.add(createDiscountRequest("Senior Gold", 65, "GOLD", 250.0, 3));
        requests.add(createDiscountRequest("Young Silver", 25, "SILVER", 150.0, 2));
        requests.add(createDiscountRequest("Child Bronze", 12, "BRONZE", 50.0, 1));
        requests.add(createDiscountRequest("Large Order", 40, "BRONZE", 500.0, 5));
        requests.add(createDiscountRequest("Bulk Order", 40, "BRONZE", 300.0, 25));

        // Calculate discounts in batch
        List<CustomerDiscountResponse> responses = customerDiscountService.calculateDiscountBatch(requests);

        // Verify the responses
        assertNotNull(responses, "Responses should not be null");
        assertEquals(5, responses.size(), "Should have 5 responses");
        
        // Verify each response
        for (int i = 0; i < 5; i++) {
            CustomerDiscountResponse response = responses.get(i);
            assertNotNull(response, "Response should not be null");
            assertTrue(response.getCustomerName().contains(requests.get(i).getCustomerName()), 
                    "Customer name should match");
            assertEquals(requests.get(i).getLoyaltyTier(), response.getLoyaltyTier(), "Loyalty tier should match");
            assertEquals(requests.get(i).getOrderAmount(), response.getOriginalAmount(), "Original amount should match");
        }
    }

    @Test
    @DisplayName("Test asynchronous discount calculation")
    public void testCalculateDiscountAsync() throws ExecutionException, InterruptedException {
        // Create a request
        CustomerDiscountRequest request = createDiscountRequest("Async Customer", 40, "SILVER", 200.0, 3);

        // Calculate discount asynchronously
        CompletableFuture<CustomerDiscountResponse> future = customerDiscountService.calculateDiscountAsync(request);

        // Wait for the result
        CustomerDiscountResponse response = future.get();

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Async Customer", response.getCustomerName(), "Customer name should match");
        assertEquals("SILVER", response.getLoyaltyTier(), "Loyalty tier should match");
        assertEquals(200.0, response.getOriginalAmount(), "Original amount should match");
    }

    @Test
    @DisplayName("Test discount statistics")
    public void testDiscountStatistics() {
        // Create and process a request to generate statistics
        CustomerDiscountRequest request = createDiscountRequest("Stats Customer", 40, "GOLD", 200.0, 3);
        customerDiscountService.calculateDiscount(request);

        // Get discount statistics
        Map<String, Object> statistics = customerDiscountService.getDiscountStatistics();

        // Verify statistics
        assertNotNull(statistics, "Statistics should not be null");
        assertTrue(statistics.containsKey("totalDiscountCalculations"), "Statistics should contain totalDiscountCalculations");
        assertTrue(statistics.containsKey("totalBatchCalculations"), "Statistics should contain totalBatchCalculations");
        assertTrue(statistics.containsKey("discountRuleCounts"), "Statistics should contain discountRuleCounts");
    }

    /**
     * Helper method to create a discount request with common fields.
     */
    private CustomerDiscountRequest createDiscountRequest(String customerName, int age, String loyaltyTier, 
                                                        double orderAmount, int orderQuantity) {
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                customerName,
                age,
                loyaltyTier,
                orderAmount,
                orderQuantity
        );
        request.setCustomerEmail(customerName.toLowerCase().replace(" ", ".") + "@example.com");
        
        return request;
    }
}