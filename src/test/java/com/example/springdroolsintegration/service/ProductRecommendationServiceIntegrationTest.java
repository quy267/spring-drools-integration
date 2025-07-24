package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRecommendationService.
 * These tests verify that the product recommendation service can generate recommendations correctly.
 */
@SpringBootTest
// @ActiveProfiles("test") // Disabled due to test profile configuration issues - tests work with default profile
public class ProductRecommendationServiceIntegrationTest {

    @Autowired
    private ProductRecommendationService productRecommendationService;

    @Test
    @DisplayName("Test product recommendations for a customer with preferences")
    public void testRecommendationsWithPreferences() {
        // Create a request for a customer with preferences
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(12345L);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(35);
        request.setGender("M");
        request.setMaxRecommendations(5);
        request.setIncludeOutOfStock(false);
        
        // Add preferred categories and brands
        request.addPreferredCategory("ELECTRONICS");
        request.addPreferredBrand("TechBrand");
        
        // Set price range
        request.setPriceRangeMin(500.0);
        request.setPriceRangeMax(1000.0);

        // Get recommendations
        ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("John Doe", response.getCustomerName(), "Customer name should match");
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        assertTrue(response.getRecommendations().size() <= 5, "Should have at most 5 recommendations");
        
        // Verify each recommendation
        for (ProductRecommendationResponse.RecommendedProduct product : response.getRecommendations()) {
            assertNotNull(product.getProductId(), "Product ID should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
            assertTrue(product.getScore() >= 0, "Score should be non-negative");
            assertNotNull(product.getReason(), "Reason should not be null");
        }
    }

    @Test
    @DisplayName("Test product recommendations for a customer with purchase history")
    public void testRecommendationsWithPurchaseHistory() {
        // Create a request for a customer with purchase history
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(67890L);
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane.smith@example.com");
        request.setAge(28);
        request.setGender("F");
        request.setMaxRecommendations(3);
        request.setIncludeOutOfStock(false);
        
        // Add recently purchased products
        request.addRecentlyPurchasedProduct("PROD-123");
        request.addRecentlyPurchasedProduct("PROD-456");
        
        // Add recently viewed products
        request.addRecentlyViewedProduct("PROD-789");

        // Get recommendations
        ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("Jane Smith", response.getCustomerName(), "Customer name should match");
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        assertTrue(response.getRecommendations().size() <= 3, "Should have at most 3 recommendations");
        
        // Verify each recommendation
        for (ProductRecommendationResponse.RecommendedProduct product : response.getRecommendations()) {
            assertNotNull(product.getProductId(), "Product ID should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
            assertTrue(product.getScore() >= 0, "Score should be non-negative");
            assertNotNull(product.getReason(), "Reason should not be null");
        }
    }

    @Test
    @DisplayName("Test product recommendations for a new customer")
    public void testRecommendationsForNewCustomer() {
        // Create a request for a new customer with no preferences or history
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(999L);
        request.setFirstName("New");
        request.setLastName("Customer");
        request.setEmail("new.customer@example.com");
        request.setAge(22);
        request.setGender("O");
        request.setMaxRecommendations(4);
        request.setIncludeOutOfStock(true);

        // Get recommendations
        ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("New Customer", response.getCustomerName(), "Customer name should match");
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        assertTrue(response.getRecommendations().size() <= 4, "Should have at most 4 recommendations");
        
        // For a new customer, we expect popular or trending items
        for (ProductRecommendationResponse.RecommendedProduct product : response.getRecommendations()) {
            assertNotNull(product.getProductId(), "Product ID should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
            assertTrue(product.getScore() >= 0, "Score should be non-negative");
            assertNotNull(product.getReason(), "Reason should not be null");
        }
    }

    @Test
    @DisplayName("Test product-based recommendations")
    public void testProductBasedRecommendations() {
        // Get recommendations based on a specific product
        String productId = "1001"; // This should be a valid product ID in the mock repository
        int maxRecommendations = 3;
        
        ProductRecommendationResponse response = productRecommendationService.getProductBasedRecommendations(
                productId, maxRecommendations);

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getCurrentProductId(), "Current product ID should not be null");
        assertEquals(productId, response.getCurrentProductId(), "Current product ID should match");
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        assertTrue(response.getRecommendations().size() <= maxRecommendations, 
                "Should have at most " + maxRecommendations + " recommendations");
        
        // Verify each recommendation
        for (ProductRecommendationResponse.RecommendedProduct product : response.getRecommendations()) {
            assertNotNull(product.getProductId(), "Product ID should not be null");
            assertNotNull(product.getName(), "Product name should not be null");
            assertTrue(product.getScore() >= 0, "Score should be non-negative");
            assertNotNull(product.getReason(), "Reason should not be null");
            // Ensure we don't recommend the same product
            assertNotEquals(productId, product.getProductId(), "Should not recommend the same product");
        }
    }

    @Test
    @DisplayName("Test batch product recommendations")
    public void testGetRecommendationsBatch() {
        // Create a list of requests
        List<ProductRecommendationRequest> requests = new ArrayList<>();
        
        // Add different types of recommendation requests
        requests.add(createRequestWithPreferences("Customer1", 1001L, "ELECTRONICS", "TechBrand"));
        requests.add(createRequestWithPurchaseHistory("Customer2", 1002L, "PROD-123"));
        requests.add(createBasicRequest("Customer3", 1003L, 25));
        
        // Get recommendations in batch
        List<ProductRecommendationResponse> responses = productRecommendationService.getRecommendationsBatch(requests);

        // Verify the responses
        assertNotNull(responses, "Responses should not be null");
        assertEquals(3, responses.size(), "Should have 3 responses");
        
        // Verify each response
        for (int i = 0; i < 3; i++) {
            ProductRecommendationResponse response = responses.get(i);
            assertNotNull(response, "Response should not be null");
            String expectedName = requests.get(i).getFirstName() + " " + requests.get(i).getLastName();
            assertEquals(expectedName, response.getCustomerName(), "Customer name should match");
            assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        }
    }

    @Test
    @DisplayName("Test asynchronous product recommendations")
    public void testGetRecommendationsAsync() throws ExecutionException, InterruptedException {
        // Create a request
        ProductRecommendationRequest request = createBasicRequest("AsyncCustomer", 2001L, 30);

        // Get recommendations asynchronously
        CompletableFuture<ProductRecommendationResponse> future = 
                productRecommendationService.getRecommendationsAsync(request);

        // Wait for the result
        ProductRecommendationResponse response = future.get();

        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertEquals("AsyncCustomer Customer", response.getCustomerName(), "Customer name should match");
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
    }

    @Test
    @DisplayName("Test recommendation statistics")
    public void testRecommendationStatistics() {
        // Create and process a request to generate statistics
        ProductRecommendationRequest request = createBasicRequest("StatsCustomer", 3001L, 40);
        productRecommendationService.getRecommendations(request);

        // Get recommendation statistics
        Map<String, Object> statistics = productRecommendationService.getRecommendationStatistics();

        // Verify statistics
        assertNotNull(statistics, "Statistics should not be null");
        assertTrue(statistics.containsKey("totalRecommendations"), "Statistics should contain totalRecommendations");
        assertTrue(statistics.containsKey("totalBatchRecommendations"), "Statistics should contain totalBatchRecommendations");
        assertTrue(statistics.containsKey("totalProductBasedRecommendations"), "Statistics should contain totalProductBasedRecommendations");
    }

    /**
     * Helper method to create a basic recommendation request.
     */
    private ProductRecommendationRequest createBasicRequest(String firstName, Long customerId, int age) {
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(customerId);
        request.setFirstName(firstName);
        request.setLastName("Customer");
        request.setEmail(firstName.toLowerCase() + ".customer@example.com");
        request.setAge(age);
        request.setGender("M");
        request.setMaxRecommendations(5);
        request.setIncludeOutOfStock(false);
        return request;
    }

    /**
     * Helper method to create a request with preferences.
     */
    private ProductRecommendationRequest createRequestWithPreferences(String firstName, Long customerId, String category, String brand) {
        ProductRecommendationRequest request = createBasicRequest(firstName, customerId, 35);
        request.addPreferredCategory(category);
        request.addPreferredBrand(brand);
        request.setPriceRangeMin(500.0);
        request.setPriceRangeMax(1000.0);
        return request;
    }

    /**
     * Helper method to create a request with purchase history.
     */
    private ProductRecommendationRequest createRequestWithPurchaseHistory(String firstName, Long customerId, String productId) {
        ProductRecommendationRequest request = createBasicRequest(firstName, customerId, 28);
        request.addRecentlyPurchasedProduct(productId);
        return request;
    }
}