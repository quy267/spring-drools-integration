package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.dto.PagedResponse;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import com.example.springdroolsintegration.service.LoanApprovalService;
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
 * Controller for loan approval endpoints.
 * This controller handles requests for evaluating loan applications using Drools rules.
 */
@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan Approval", description = "API for evaluating loan applications using Drools rules")
public class LoanApprovalController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoanApprovalController.class);
    
    private final LoanApprovalService loanApprovalService;
    
    /**
     * Constructor for LoanApprovalController.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param loanApprovalService The loan approval service
     */
    public LoanApprovalController(LoanApprovalService loanApprovalService) {
        this.loanApprovalService = loanApprovalService;
    }
    
    /**
     * Evaluates a loan application.
     * 
     * @param request The loan approval request
     * @return The loan approval response with approval decision
     */
    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate a loan application", 
               description = "Evaluates a loan application based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan application evaluated successfully",
                     content = @Content(mediaType = "application/json", 
                     schema = @Schema(implementation = LoanApprovalResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error evaluating loan application")
    })
    public ResponseEntity<LoanApprovalResponse> evaluateLoanApplication(@Valid @RequestBody LoanApprovalRequest request) {
        logger.debug("Evaluating loan application for: {}", request.getFullName());
        
        try {
            LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error evaluating loan application for: {}", request.getFullName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Evaluates multiple loan applications in batch with pagination support.
     * 
     * @param requests The list of loan approval requests
     * @param page The page number (0-based)
     * @param size The page size
     * @return A paged response containing the loan approval responses with approval decisions
     */
    @PostMapping("/batch")
    @Operation(summary = "Evaluate multiple loan applications", 
               description = "Evaluates multiple loan applications in batch based on Drools rules with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loan applications evaluated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error evaluating loan applications")
    })
    public ResponseEntity<PagedResponse<LoanApprovalResponse>> evaluateLoanApplicationBatch(
            @Valid @RequestBody List<LoanApprovalRequest> requests,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Evaluating batch of {} loan applications with pagination (page={}, size={})", 
                requests.size(), page, size);
        
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        try {
            List<LoanApprovalResponse> responses = loanApprovalService.evaluateLoanApplicationBatch(requests);
            
            // Create a paged response
            PagedResponse<LoanApprovalResponse> pagedResponse = PagedResponse.of(responses, page, size);
            
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            logger.error("Error evaluating batch of loan applications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Asynchronously evaluates a loan application.
     * 
     * @param request The loan approval request
     * @return A CompletableFuture that will complete with the loan approval response
     */
    @PostMapping("/async")
    @Operation(summary = "Asynchronously evaluate a loan application", 
               description = "Asynchronously evaluates a loan application based on Drools rules")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Loan application evaluation accepted"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error evaluating loan application")
    })
    public ResponseEntity<Void> evaluateLoanApplicationAsync(@Valid @RequestBody LoanApprovalRequest request) {
        logger.debug("Asynchronously evaluating loan application for: {}", request.getFullName());
        
        try {
            CompletableFuture<LoanApprovalResponse> future = 
                    loanApprovalService.evaluateLoanApplicationAsync(request);
            
            // Handle the result asynchronously (could add a callback, store in a cache, etc.)
            future.thenAccept(response -> 
                logger.debug("Async loan application evaluation completed for: {}, approved: {}", 
                        response.getApplicantName(), response.isApproved()));
            
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("Error initiating async loan application evaluation for: {}", 
                    request.getFullName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Gets statistics about loan approval rule executions.
     * 
     * @return A map of statistics about loan approval rule executions
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get loan approval statistics", 
               description = "Returns statistics about loan approval rule executions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Error retrieving statistics")
    })
    public ResponseEntity<Map<String, Object>> getLoanApprovalStatistics() {
        logger.debug("Getting loan approval statistics");
        
        try {
            Map<String, Object> statistics = loanApprovalService.getLoanApprovalStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error getting loan approval statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}