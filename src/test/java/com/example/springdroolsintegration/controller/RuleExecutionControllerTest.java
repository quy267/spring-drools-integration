package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.DiscountRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for RuleExecutionController.
 * These tests verify that the rule execution endpoints work correctly.
 */
public class RuleExecutionControllerTest extends BaseApiIntegrationTest {
    
    @Test
    @DisplayName("Test execute rules endpoint with a valid fact object")
    public void testExecuteRules() throws Exception {
        // Create a test customer
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setAge(65); // Senior customer
        customer.setLoyaltyTier("GOLD");
        
        // Execute the request
        ResultActions result = performPost("/api/v1/rules/execute", customer);
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.id", is(1)))
              .andExpect(jsonPath("$.name", is("Test Customer")))
              .andExpect(jsonPath("$.age", is(65)))
              .andExpect(jsonPath("$.loyaltyTier", is("GOLD")));
    }
    
    @Test
    @DisplayName("Test execute rules endpoint with a null fact object")
    public void testExecuteRulesWithNullFact() throws Exception {
        // Execute the request with null
        ResultActions result = performPost("/api/v1/rules/execute", null);
        
        // Verify the response
        result.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test batch execute rules endpoint with valid fact objects")
    public void testExecuteRulesBatch() throws Exception {
        // Create a list of test customers
        List<Customer> customers = Arrays.asList(
                createCustomer(1L, "Customer 1", 65, "GOLD"),
                createCustomer(2L, "Customer 2", 45, "SILVER"),
                createCustomer(3L, "Customer 3", 25, "BRONZE")
        );
        
        // Execute the request
        ResultActions result = performPost("/api/v1/rules/batch", customers, "page", "0", "size", "10");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(3)))
              .andExpect(jsonPath("$.content[0].id", is(1)))
              .andExpect(jsonPath("$.content[1].id", is(2)))
              .andExpect(jsonPath("$.content[2].id", is(3)))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(10)))
              .andExpect(jsonPath("$.totalElements", is(3)))
              .andExpect(jsonPath("$.totalPages", is(1)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(true)));
    }
    
    @Test
    @DisplayName("Test batch execute rules endpoint with pagination")
    public void testExecuteRulesBatchWithPagination() throws Exception {
        // Create a list of test customers
        List<Customer> customers = Arrays.asList(
                createCustomer(1L, "Customer 1", 65, "GOLD"),
                createCustomer(2L, "Customer 2", 45, "SILVER"),
                createCustomer(3L, "Customer 3", 25, "BRONZE"),
                createCustomer(4L, "Customer 4", 35, "SILVER"),
                createCustomer(5L, "Customer 5", 55, "GOLD")
        );
        
        // Execute the request with page=0, size=2
        ResultActions result = performPost("/api/v1/rules/batch", customers, "page", "0", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].id", is(1)))
              .andExpect(jsonPath("$.content[1].id", is(2)))
              .andExpect(jsonPath("$.page", is(0)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(true)))
              .andExpect(jsonPath("$.last", is(false)));
        
        // Execute the request with page=1, size=2
        result = performPost("/api/v1/rules/batch", customers, "page", "1", "size", "2");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.content", hasSize(2)))
              .andExpect(jsonPath("$.content[0].id", is(3)))
              .andExpect(jsonPath("$.content[1].id", is(4)))
              .andExpect(jsonPath("$.page", is(1)))
              .andExpect(jsonPath("$.size", is(2)))
              .andExpect(jsonPath("$.totalElements", is(5)))
              .andExpect(jsonPath("$.totalPages", is(3)))
              .andExpect(jsonPath("$.first", is(false)))
              .andExpect(jsonPath("$.last", is(false)));
    }
    
    @Test
    @DisplayName("Test batch execute rules endpoint with empty list")
    public void testExecuteRulesBatchWithEmptyList() throws Exception {
        // Execute the request with an empty list
        ResultActions result = performPost("/api/v1/rules/batch", List.of());
        
        // Verify the response
        result.andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Test get rule metadata endpoint")
    public void testGetRuleMetadata() throws Exception {
        // Execute the request
        ResultActions result = performGet("/api/v1/rules/metadata");
        
        // Verify the response
        result.andExpect(status().isOk())
              .andExpect(content().contentType("application/json"))
              .andExpect(jsonPath("$.totalExecutions", notNullValue()))
              .andExpect(jsonPath("$.totalBatchExecutions", notNullValue()))
              .andExpect(jsonPath("$.sessionsCreated", notNullValue()))
              .andExpect(jsonPath("$.sessionsDisposed", notNullValue()));
    }
    
    /**
     * Helper method to create a customer for testing.
     */
    private Customer createCustomer(Long id, String name, int age, String loyaltyTier) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setAge(age);
        customer.setLoyaltyTier(loyaltyTier);
        return customer;
    }
}