package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for CustomerDiscountController.
 * These tests verify that the customer discount endpoints work correctly.
 */
public class CustomerDiscountControllerTest extends BaseApiIntegrationTest {
    
    @Test
    @DisplayName("Test calculate discount endpoint with a valid request")
    public void testCalculateDiscount() throws Exception {
        // Create a test request
        CustomerDiscountRequest request = createDiscountRequest("Test Customer", 65, "GOLD", 250.0, 3);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/discounts/calculate", request);
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.customerName", is("Test Customer")))
              .andExpect(jsonPath("$.loyaltyTier", is("GOLD")))
              .andExpect(jsonPath("$.originalAmount", is(250.0)))
              .andExpect(jsonPath("$.discountPercentage", notNullValue()))
              .andExpect(jsonPath("$.discountAmount", notNullValue()))
              .andExpect(jsonPath("$.finalAmount", notNullValue()))
              .andExpect(jsonPath("$.appliedRules", notNullValue()))
              .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
    
    @Test
    @DisplayName("Test calculate discount endpoint with an invalid request")
    public void testCalculateDiscountWithInvalidRequest() throws Exception {
        // Create an invalid request (missing required fields)
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        
        // Execute the request
        ResultActions result = performPost("/api/v1/discounts/calculate", request);
        
        // Verify the response (should be 400 Bad Request due to validation)
        result.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test batch discount calculation endpoint with valid requests")
    public void testCalculateDiscountBatch() throws Exception {
        // Create a list of test requests
        List<CustomerDiscountRequest> requests = new ArrayList<>();
        requests.add(createDiscountRequest("Customer 1", 65, "GOLD", 250.0, 3));
        requests.add(createDiscountRequest("Customer 2", 45, "SILVER", 150.0, 2));
        requests.add(createDiscountRequest("Customer 3", 25, "BRONZE", 100.0, 1));
        
        // Execute the request
        ResultActions result = performPost("/api/v1/discounts/batch", requests, "page", "0", "size", "10");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(3)))
              .andExpect(jsonPath("$.content[0].customerName", is("Customer 1")))
              .andExpect(jsonPath("$.content[1].customerName", is("Customer 2")))
              .andExpect(jsonPath("$.content[2].customerName", is("Customer 3")))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(10)))
              .andExpect(jsonPath("$.totalElements", is(3)))
              .andExpect(jsonPath("$.totalPages", is(1)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(true)));
    }
    
    @Test
    @DisplayName("Test batch discount calculation endpoint with pagination")
    public void testCalculateDiscountBatchWithPagination() throws Exception {
        // Create a list of test requests
        List<CustomerDiscountRequest> requests = new ArrayList<>();
        requests.add(createDiscountRequest("Customer 1", 65, "GOLD", 250.0, 3));
        requests.add(createDiscountRequest("Customer 2", 45, "SILVER", 150.0, 2));
        requests.add(createDiscountRequest("Customer 3", 25, "BRONZE", 100.0, 1));
        requests.add(createDiscountRequest("Customer 4", 35, "SILVER", 200.0, 2));
        requests.add(createDiscountRequest("Customer 5", 55, "GOLD", 300.0, 3));
        
        // Execute the request with page=0, size=2
        ResultActions result = performPost("/api/v1/discounts/batch", requests, "page", "0", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].customerName", is("Customer 1")))
              .andExpect(jsonPath("$.content[1].customerName", is("Customer 2")))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(false)));
        
        // Execute the request with page=1, size=2
        result = performPost("/api/v1/discounts/batch", requests, "page", "1", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].customerName", is("Customer 3")))
              .andExpect(jsonPath("$.content[1].customerName", is("Customer 4")))
              .andExpect(jsonPath("$.page", is(1)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(false)))
              .andExpect(jsonPath("$.last", is(false)));
    }
    
    @Test
    @DisplayName("Test async discount calculation endpoint")
    public void testCalculateDiscountAsync() throws Exception {
        // Create a test request
        CustomerDiscountRequest request = createDiscountRequest("Async Customer", 40, "SILVER", 200.0, 2);
        
        // Execute the request
        ResultActions result = performPost("/api/v1/discounts/async", request);
        
        // Verify the response (should be 202 Accepted)
        result.andExpect(status().isAccepted());
    }
    
    @Test
    @DisplayName("Test get discount statistics endpoint")
    public void testGetDiscountStatistics() throws Exception {
        // Execute the request
        ResultActions result = performGet("/api/v1/discounts/statistics");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.totalDiscountCalculations", notNullValue()))
              .andExpect(jsonPath("$.totalBatchCalculations", notNullValue()))
              .andExpect(jsonPath("$.discountRuleCounts", notNullValue()));
    }
    
    /**
     * Helper method to create a discount request for testing.
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
        
        // Add order items
        CustomerDiscountRequest.OrderItemRequest item1 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-123",
                "Product 123",
                99.99,
                1,
                "ELECTRONICS"
        );
        CustomerDiscountRequest.OrderItemRequest item2 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-456",
                "Product 456",
                49.99,
                2,
                "ACCESSORIES"
        );
        request.addOrderItem(item1);
        request.addOrderItem(item2);
        
        return request;
    }
}