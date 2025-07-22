package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for product recommendation rule execution.
 * This service handles the execution of product recommendation rules for customers.
 */
public interface ProductRecommendationService {
    
    /**
     * Gets product recommendations for a customer based on rules.
     * 
     * @param request The product recommendation request
     * @return The product recommendation response with recommended products
     */
    ProductRecommendationResponse getRecommendations(ProductRecommendationRequest request);
    
    /**
     * Gets product recommendations for multiple customers in batch.
     * 
     * @param requests The list of product recommendation requests
     * @return The list of product recommendation responses with recommended products
     */
    List<ProductRecommendationResponse> getRecommendationsBatch(List<ProductRecommendationRequest> requests);
    
    /**
     * Asynchronously gets product recommendations for a customer.
     * 
     * @param request The product recommendation request
     * @return A CompletableFuture that will complete with the product recommendation response
     */
    CompletableFuture<ProductRecommendationResponse> getRecommendationsAsync(ProductRecommendationRequest request);
    
    /**
     * Gets recommendations for a specific product (similar or complementary products).
     * 
     * @param productId The ID of the product
     * @param maxRecommendations The maximum number of recommendations to return
     * @return The product recommendation response with recommended products
     */
    ProductRecommendationResponse getProductBasedRecommendations(String productId, int maxRecommendations);
    
    /**
     * Gets statistics about product recommendation rule executions.
     * 
     * @return A map of statistics about product recommendation rule executions
     */
    Map<String, Object> getRecommendationStatistics();
}