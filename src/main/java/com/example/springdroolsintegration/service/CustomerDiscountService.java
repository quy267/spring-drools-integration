package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for customer discount rule execution.
 * This service handles the execution of discount rules for customer orders.
 */
public interface CustomerDiscountService {
    
    /**
     * Calculates discount for a customer order based on rules.
     * 
     * @param request The customer discount request
     * @return The customer discount response with calculated discount
     */
    CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request);
    
    /**
     * Calculates discounts for multiple customer orders in batch.
     * 
     * @param requests The list of customer discount requests
     * @return The list of customer discount responses with calculated discounts
     */
    List<CustomerDiscountResponse> calculateDiscountBatch(List<CustomerDiscountRequest> requests);
    
    /**
     * Asynchronously calculates discount for a customer order.
     * 
     * @param request The customer discount request
     * @return A CompletableFuture that will complete with the customer discount response
     */
    CompletableFuture<CustomerDiscountResponse> calculateDiscountAsync(CustomerDiscountRequest request);
    
    /**
     * Gets statistics about discount rule executions.
     * 
     * @return A map of statistics about discount rule executions
     */
    java.util.Map<String, Object> getDiscountStatistics();
}