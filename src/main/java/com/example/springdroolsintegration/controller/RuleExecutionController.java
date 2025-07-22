package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.model.dto.PagedResponse;
import com.example.springdroolsintegration.service.RuleExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
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
public class RuleExecutionController {
    
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
     * @param fact The fact object to execute rules on
     * @return The fact object after rule execution
     */
    @PostMapping("/execute")
    @Operation(summary = "Execute rules on a fact object", 
               description = "Executes Drools rules on the provided fact object and returns the modified object")
    public ResponseEntity<Object> executeRules(@RequestBody Object fact) {
        if (fact == null) {
            return ResponseEntity.badRequest().body("Fact object cannot be null");
        }
        
        try {
            Object result = ruleExecutionService.executeRules(fact);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error executing rules: " + e.getMessage());
        }
    }
    
    /**
     * Executes rules on a batch of fact objects with pagination support.
     * 
     * @param facts The list of fact objects to execute rules on
     * @param page The page number (0-based)
     * @param size The page size
     * @return A paged response containing the fact objects after rule execution
     */
    @PostMapping("/batch")
    @Operation(summary = "Execute rules on a batch of fact objects", 
               description = "Executes Drools rules on the provided batch of fact objects and returns the modified objects with pagination")
    public ResponseEntity<PagedResponse<Object>> executeRulesBatch(
            @RequestBody List<Object> facts,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        if (facts == null || facts.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        try {
            // Cast to List<Object> is safe because we're using generic types
            @SuppressWarnings("unchecked")
            List<Object> results = (List<Object>) ruleExecutionService.executeRulesForBatch(facts);
            
            // Create a paged response
            PagedResponse<Object> pagedResponse = PagedResponse.of(results, page, size);
            
            return ResponseEntity.ok(pagedResponse);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
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
    public ResponseEntity<Map<String, Object>> getRuleMetadata() {
        try {
            Map<String, Object> statistics = ruleExecutionService.getExecutionStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to retrieve rule execution statistics",
                "message", e.getMessage()
            ));
        }
    }
}