package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.dto.PagedResponse;
import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import com.example.springdroolsintegration.service.ProductRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for product recommendation endpoints.
 * This controller handles requests for generating product recommendations using Drools rules.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
@Tag(name = "Product Recommendations", description = "API for generating product recommendations using Drools rules")
@Validated
public class ProductRecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductRecommendationController.class);
    
    private final ProductRecommendationService productRecommendationService;
    
    /**
     * Constructor for ProductRecommendationController.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param productRecommendationService The product recommendation service
     */
    public ProductRecommendationController(ProductRecommendationService productRecommendationService) {
        this.productRecommendationService = productRecommendationService;
    }
    
    /**
     * Gets product recommendations for a customer.
     * 
     * @param request The product recommendation request
     * @return The product recommendation response with recommended products
     */
    @PostMapping
    @Operation(summary = "Get product recommendations for a customer", 
               description = "Generates product recommendations for a customer based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommendations generated successfully",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = ProductRecommendationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error generating recommendations")
    })
    public ResponseEntity<ProductRecommendationResponse> getRecommendations(@Valid @RequestBody ProductRecommendationRequest request) {
        logger.debug("Getting recommendations for customer ID: {}", request.getCustomerId());
        
        try {
            ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting recommendations for customer ID: {}", request.getCustomerId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Gets product recommendations for multiple customers in batch with pagination support.
     * 
     * @param requests The list of product recommendation requests
     * @param page The page number (0-based)
     * @param size The page size
     * @return A paged response containing the product recommendation responses with recommended products
     */
    @PostMapping("/batch")
    @Operation(summary = "Get product recommendations for multiple customers", 
               description = "Generates product recommendations for multiple customers in batch based on Drools rules with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommendations generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error generating recommendations")
    })
    public ResponseEntity<PagedResponse<ProductRecommendationResponse>> getRecommendationsBatch(
            @Valid @RequestBody List<ProductRecommendationRequest> requests,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Getting recommendations for batch of {} requests with pagination (page={}, size={})", 
                requests.size(), page, size);
        
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        try {
            List<ProductRecommendationResponse> responses = productRecommendationService.getRecommendationsBatch(requests);
            
            // Create a paged response
            PagedResponse<ProductRecommendationResponse> pagedResponse = PagedResponse.of(responses, page, size);
            
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            logger.error("Error getting recommendations for batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Asynchronously gets product recommendations for a customer.
     * 
     * @param request The product recommendation request
     * @return A CompletableFuture that will complete with the product recommendation response
     */
    @PostMapping("/async")
    @Operation(summary = "Asynchronously get product recommendations for a customer", 
               description = "Asynchronously generates product recommendations for a customer based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Recommendation generation accepted"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error generating recommendations")
    })
    public ResponseEntity<Void> getRecommendationsAsync(@Valid @RequestBody ProductRecommendationRequest request) {
        logger.debug("Asynchronously getting recommendations for customer ID: {}", request.getCustomerId());
        
        try {
            CompletableFuture<ProductRecommendationResponse> future = 
                    productRecommendationService.getRecommendationsAsync(request);
            
            // Handle the result asynchronously (could add a callback, store in a cache, etc.)
            future.thenAccept(response -> 
                logger.debug("Async recommendation generation completed for customer ID: {}, recommendations: {}", 
                        response.getCustomerId(), response.getRecommendationCount()));
            
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("Error initiating async recommendation generation for customer ID: {}", 
                    request.getCustomerId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets product-based recommendations (similar or complementary products).
     * 
     * @param productId The ID of the product
     * @param maxRecommendations The maximum number of recommendations to return
     * @return The product recommendation response with recommended products
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get product-based recommendations", 
               description = "Gets recommendations for similar or complementary products based on a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommendations generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Error generating recommendations")
    })
    public ResponseEntity<ProductRecommendationResponse> getProductBasedRecommendations(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable String productId,
            
            @Parameter(description = "Maximum number of recommendations to return", example = "5")
            @RequestParam(defaultValue = "5") @Min(1) int maxRecommendations) {
        logger.debug("Getting product-based recommendations for product ID: {}", productId);
        
        try {
            ProductRecommendationResponse response = 
                    productRecommendationService.getProductBasedRecommendations(productId, maxRecommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting product-based recommendations for product ID: {}", productId, e);
            
            // If the product doesn't exist, return 404 Not Found
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Gets statistics about product recommendation rule executions.
     * 
     * @return A map of statistics about product recommendation rule executions
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get recommendation statistics", 
               description = "Returns statistics about product recommendation rule executions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Error retrieving statistics")
    })
    public ResponseEntity<Map<String, Object>> getRecommendationStatistics() {
        logger.debug("Getting recommendation statistics");
        
        try {
            Map<String, Object> statistics = productRecommendationService.getRecommendationStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting recommendation statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}