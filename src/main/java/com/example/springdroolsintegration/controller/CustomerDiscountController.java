package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.dto.PagedResponse;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import com.example.springdroolsintegration.service.CustomerDiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for customer discount endpoints.
 * This controller handles requests for calculating customer discounts using Drools rules.
 */
@RestController
@RequestMapping("/api/v1/discounts")
@Tag(name = "Customer Discounts", description = "API for calculating customer discounts using Drools rules")
public class CustomerDiscountController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerDiscountController.class);
    
    private final CustomerDiscountService customerDiscountService;
    
    /**
     * Constructor for CustomerDiscountController.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param customerDiscountService The customer discount service
     */
    public CustomerDiscountController(CustomerDiscountService customerDiscountService) {
        this.customerDiscountService = customerDiscountService;
    }
    
    /**
     * Calculates discount for a customer order.
     * 
     * @param request The customer discount request
     * @return The customer discount response with calculated discount
     */
    @PostMapping("/calculate")
    @Operation(summary = "Calculate discount for a customer order", 
               description = "Calculates discount for a customer order based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Discount calculated successfully",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = CustomerDiscountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error calculating discount")
    })
    public ResponseEntity<CustomerDiscountResponse> calculateDiscount(@Valid @RequestBody CustomerDiscountRequest request) {
        logger.debug("Calculating discount for customer: {}", request.getCustomerName());
        
        try {
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error calculating discount for customer: {}", request.getCustomerName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Calculates discounts for multiple customer orders in batch with pagination support.
     * 
     * @param requests The list of customer discount requests
     * @param page The page number (0-based)
     * @param size The page size
     * @return A paged response containing the customer discount responses with calculated discounts
     */
    @PostMapping("/batch")
    @Operation(summary = "Calculate discounts for multiple customer orders", 
               description = "Calculates discounts for multiple customer orders in batch based on Drools rules with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Discounts calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error calculating discounts")
    })
    public ResponseEntity<PagedResponse<CustomerDiscountResponse>> calculateDiscountBatch(
            @Valid @RequestBody List<CustomerDiscountRequest> requests,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Calculating discounts for batch of {} requests with pagination (page={}, size={})", 
                requests.size(), page, size);
        
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        try {
            List<CustomerDiscountResponse> responses = customerDiscountService.calculateDiscountBatch(requests);
            
            // Create a paged response
            PagedResponse<CustomerDiscountResponse> pagedResponse = PagedResponse.of(responses, page, size);
            
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            logger.error("Error calculating discounts for batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Asynchronously calculates discount for a customer order.
     * 
     * @param request The customer discount request
     * @return A CompletableFuture that will complete with the customer discount response
     */
    @PostMapping("/async")
    @Operation(summary = "Asynchronously calculate discount for a customer order", 
               description = "Asynchronously calculates discount for a customer order based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Discount calculation accepted"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error calculating discount")
    })
    public ResponseEntity<Void> calculateDiscountAsync(@Valid @RequestBody CustomerDiscountRequest request) {
        logger.debug("Asynchronously calculating discount for customer: {}", request.getCustomerName());
        
        try {
            CompletableFuture<CustomerDiscountResponse> future = 
                    customerDiscountService.calculateDiscountAsync(request);
            
            // Handle the result asynchronously (could add a callback, store in a cache, etc.)
            future.thenAccept(response -> 
                logger.debug("Async discount calculation completed for customer: {}, discount: {}%", 
                        response.getCustomerName(), response.getDiscountPercentage()));
            
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("Error initiating async discount calculation for customer: {}", 
                    request.getCustomerName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets statistics about discount rule executions.
     * 
     * @return A map of statistics about discount rule executions
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get discount statistics", 
               description = "Returns statistics about discount rule executions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Error retrieving statistics")
    })
    public ResponseEntity<Map<String, Object>> getDiscountStatistics() {
        logger.debug("Getting discount statistics");
        
        try {
            Map<String, Object> statistics = customerDiscountService.getDiscountStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting discount statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}