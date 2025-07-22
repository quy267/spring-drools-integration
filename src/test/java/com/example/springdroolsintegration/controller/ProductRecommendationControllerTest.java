package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for ProductRecommendationController.
 * These tests verify that the product recommendation endpoints work correctly.
 */
public class ProductRecommendationControllerTest extends BaseApiIntegrationTest {
    
    @Test
    @DisplayName("Test get recommendations endpoint with a valid request")
    public void testGetRecommendations() throws Exception {
        // Create a test request
        ProductRecommendationRequest request = createRecommendationRequest(1001L, "John", "Doe", 5);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/recommendations", request);
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.customerId", is(1001)))
              .andExpect(jsonPath("$.customerName", is("John Doe")))
              .andExpect(jsonPath("$.recommendationType", notNullValue()))
              .andExpect(jsonPath("$.recommendations", notNullValue()))
              .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
    
    @Test
    @DisplayName("Test get recommendations endpoint with an invalid request")
    public void testGetRecommendationsWithInvalidRequest() throws Exception {
        // Create an invalid request (missing required fields)
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        
        // Execute the request
        ResultActions result = performPost("/api/v1/recommendations", request);
        
        // Verify the response (should be 400 Bad Request due to validation)
        result.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test batch recommendations endpoint with valid requests")
    public void testGetRecommendationsBatch() throws Exception {
        // Create a list of test requests
        List<ProductRecommendationRequest> requests = new ArrayList<>();
        requests.add(createRecommendationRequest(1001L, "John", "Doe", 5));
        requests.add(createRecommendationRequest(1002L, "Jane", "Smith", 3));
        requests.add(createRecommendationRequest(1003L, "Bob", "Johnson", 4));
        
        // Execute the request
        ResultActions result = performPost("/api/v1/recommendations/batch", requests, "page", "0", "size", "10");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(3)))
              .andExpect(jsonPath("$.content[0].customerId", is(1001)))
              .andExpect(jsonPath("$.content[1].customerId", is(1002)))
              .andExpect(jsonPath("$.content[2].customerId", is(1003)))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(10)))
              .andExpect(jsonPath("$.totalElements", is(3)))
              .andExpect(jsonPath("$.totalPages", is(1)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(true)));
    }
    
    @Test
    @DisplayName("Test batch recommendations endpoint with pagination")
    public void testGetRecommendationsBatchWithPagination() throws Exception {
        // Create a list of test requests
        List<ProductRecommendationRequest> requests = new ArrayList<>();
        requests.add(createRecommendationRequest(1001L, "John", "Doe", 5));
        requests.add(createRecommendationRequest(1002L, "Jane", "Smith", 3));
        requests.add(createRecommendationRequest(1003L, "Bob", "Johnson", 4));
        requests.add(createRecommendationRequest(1004L, "Alice", "Brown", 2));
        requests.add(createRecommendationRequest(1005L, "David", "Wilson", 6));
        
        // Execute the request with page=0, size=2
        ResultActions result = performPost("/api/v1/recommendations/batch", requests, "page", "0", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].customerId", is(1001)))
              .andExpect(jsonPath("$.content[1].customerId", is(1002)))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(false)));
        
        // Execute the request with page=1, size=2
        result = performPost("/api/v1/recommendations/batch", requests, "page", "1", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].customerId", is(1003)))
              .andExpect(jsonPath("$.content[1].customerId", is(1004)))
              .andExpect(jsonPath("$.page", is(1)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(false)))
              .andExpect(jsonPath("$.last", is(false)));
    }
    
    @Test
    @DisplayName("Test async recommendations endpoint")
    public void testGetRecommendationsAsync() throws Exception {
        // Create a test request
        ProductRecommendationRequest request = createRecommendationRequest(2001L, "Async", "Customer", 4);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/recommendations/async", request);
        
        // Verify the response (should be 202 Accepted)
        result.andExpect(status().isAccepted());
    }
    
    @Test
    @DisplayName("Test product-based recommendations endpoint")
    public void testGetProductBasedRecommendations() throws Exception {
        // Execute the request
        ResultActions result = performGet("/api/v1/recommendations/product/1001", "maxRecommendations", "3");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.currentProductId", is("1001")))
              .andExpect(jsonPath("$.recommendationType", is("PRODUCT_BASED")))
              .andExpect(jsonPath("$.recommendations", notNullValue()));
    }
    
    @Test
    @DisplayName("Test product-based recommendations endpoint with invalid product ID")
    public void testGetProductBasedRecommendationsWithInvalidProductId() throws Exception {
        // Execute the request with an invalid product ID
        ResultActions result = performGet("/api/v1/recommendations/product/9999", "maxRecommendations", "3");
        
        // Verify the response (should be 404 Not Found or 500 Internal Server Error depending on implementation)
        result.andExpect(status().is4xxClientError());
    }
    
    @Test
    @DisplayName("Test get recommendation statistics endpoint")
    public void testGetRecommendationStatistics() throws Exception {
        // Execute the request
        ResultActions result = performGet("/api/v1/recommendations/statistics");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.totalRecommendations", notNullValue()))
              .andExpect(jsonPath("$.totalBatchRecommendations", notNullValue()))
              .andExpect(jsonPath("$.totalProductBasedRecommendations", notNullValue()));
    }
    
    /**
     * Helper method to create a recommendation request for testing.
     */
    private ProductRecommendationRequest createRecommendationRequest(Long customerId, String firstName, 
                                                                   String lastName, int maxRecommendations) {
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(customerId);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");
        request.setAge(35);
        request.setGender("M");
        request.setMaxRecommendations(maxRecommendations);
        
        // Add preferred categories
        Set<String> categories = new HashSet<>();
        categories.add("ELECTRONICS");
        categories.add("BOOKS");
        request.setPreferredCategories(categories);
        
        // Add preferred brands
        Set<String> brands = new HashSet<>();
        brands.add("TechBrand");
        brands.add("BookBrand");
        request.setPreferredBrands(brands);
        
        // Add recently viewed products
        Set<String> recentlyViewed = new HashSet<>();
        recentlyViewed.add("PROD-123");
        recentlyViewed.add("PROD-456");
        request.setRecentlyViewedProducts(recentlyViewed);
        
        return request;
    }
}