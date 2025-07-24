package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.dto.PagedResponse;
import com.example.springdroolsintegration.model.request.RuleExecutionBatchRequest;
import com.example.springdroolsintegration.model.request.RuleExecutionRequest;
import com.example.springdroolsintegration.service.RuleExecutionService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller for rule execution endpoints.
 * This controller handles requests for executing Drools rules.
 */
@RestController
@RequestMapping("/api/v1/rules")
@Tag(name = "Rule Execution", description = "API for executing Drools rules")
@Validated
public class RuleExecutionController {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleExecutionController.class);
    
    private final RuleExecutionService ruleExecutionService;
    
    /**
     * Constructor for RuleExecutionController.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleExecutionService The rule execution service
     */
    public RuleExecutionController(RuleExecutionService ruleExecutionService) {
        this.ruleExecutionService = ruleExecutionService;
    }
    
    /**
     * Executes rules on a fact object.
     * 
     * @param request The rule execution request containing the fact object
     * @return The fact object after rule execution
     */
    @PostMapping("/execute")
    @Operation(summary = "Execute rules on a fact object", 
               description = "Executes Drools rules on the provided fact object and returns the modified object")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rules executed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error executing rules")
    })
    public ResponseEntity<Object> executeRules(@Valid @RequestBody RuleExecutionRequest request) {
        logger.debug("Executing rules on fact object of type: {}", 
                request.getFact() != null ? request.getFact().getClass().getSimpleName() : "null");
        
        try {
            Object result;
            if (request.getRulePackage() != null && !request.getRulePackage().isEmpty()) {
                // Execute rules from a specific package
                result = ruleExecutionService.executeRules(request.getFact(), request.getRulePackage());
            } else {
                // Execute all applicable rules
                result = ruleExecutionService.executeRules(request.getFact());
            }
            return ResponseEntity.ok(result);
        } catch (com.example.springdroolsintegration.exception.RuleExecutionException e) {
            // Let RuleExecutionException bubble up to GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            logger.error("Error executing rules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Error executing rules",
                        "message", e.getMessage()
                    ));
        }
    }
    
    /**
     * Executes rules on a batch of fact objects with pagination support.
     * 
     * @param request The batch rule execution request containing the list of fact objects
     * @param page The page number (0-based)
     * @param size The page size
     * @return A paged response containing the fact objects after rule execution
     */
    @PostMapping("/batch")
    @Operation(summary = "Execute rules on a batch of fact objects", 
               description = "Executes Drools rules on the provided batch of fact objects and returns the modified objects with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rules executed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Error executing rules")
    })
    public ResponseEntity<PagedResponse<Object>> executeRulesBatch(
            @Valid @RequestBody RuleExecutionBatchRequest request,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        logger.debug("Executing rules on batch of {} fact objects with pagination (page={}, size={})", 
                request.getFacts().size(), page, size);
        
        try {
            List<Object> results;
            if (request.getRulePackage() != null && !request.getRulePackage().isEmpty()) {
                // Execute rules from a specific package
                results = ruleExecutionService.executeRulesForBatch(request.getFacts(), request.getRulePackage());
            } else {
                // Execute all applicable rules
                results = ruleExecutionService.executeRulesForBatch(request.getFacts());
            }
            
            // Create a paged response
            PagedResponse<Object> pagedResponse = PagedResponse.of(results, page, size);
            
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            logger.error("Error executing rules for batch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Gets rule execution metadata and statistics.
     * 
     * @return A map of rule execution statistics
     */
    @GetMapping("/metadata")
    @Operation(summary = "Get rule execution metadata", 
               description = "Returns metadata and statistics about rule executions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Error retrieving statistics")
    })
    public ResponseEntity<Map<String, Object>> getRuleMetadata() {
        logger.debug("Getting rule execution metadata and statistics");
        
        try {
            Map<String, Object> statistics = ruleExecutionService.getExecutionStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error retrieving rule execution statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Failed to retrieve rule execution statistics",
                "message", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}