package com.example.springdroolsintegration.controller;

import com.example.springdroolsintegration.service.RuleManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Controller for rule management endpoints.
 * This controller handles requests for rule upload, validation, status, and reload operations.
 */
@RestController
@RequestMapping("/api/v1/rules")
@Tag(name = "Rule Management", description = "API for managing Drools rules")
public class RuleManagementController {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleManagementController.class);
    
    private final RuleManagementService ruleManagementService;
    
    /**
     * Constructor for RuleManagementController.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleManagementService The rule management service
     */
    public RuleManagementController(RuleManagementService ruleManagementService) {
        this.ruleManagementService = ruleManagementService;
    }
    
    /**
     * Uploads a rule file (DRL or Excel decision table) to the system.
     *
     * @param file The rule file to upload
     * @param version Optional version identifier for the rule
     * @return A response containing the upload result information
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a rule file", 
               description = "Uploads a rule file (DRL or Excel decision table) to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule file uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or validation failed"),
            @ApiResponse(responseCode = "500", description = "Server error during upload")
    })
    public ResponseEntity<Map<String, Object>> uploadRuleFile(
            @Parameter(description = "Rule file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional version identifier for the rule")
            @RequestParam(value = "version", required = false) String version) {
        
        logger.info("Received request to upload rule file: {}, version: {}", file.getOriginalFilename(), version);
        
        try {
            Map<String, Object> result = ruleManagementService.uploadRuleFile(file, version);
            
            if (result.containsKey("success") && result.get("success").equals(false)) {
                return ResponseEntity.badRequest().body(result);
            }
            
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            logger.error("Error uploading rule file", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error uploading rule file: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Validates a rule file without adding it to the rule engine.
     *
     * @param file The rule file to validate
     * @return A response containing the validation result information
     */
    @PutMapping(value = "/validate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Validate a rule file", 
               description = "Validates a rule file without adding it to the rule engine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation result"),
            @ApiResponse(responseCode = "500", description = "Server error during validation")
    })
    public ResponseEntity<Map<String, Object>> validateRuleFile(
            @Parameter(description = "Rule file to validate", required = true)
            @RequestParam("file") MultipartFile file) {
        
        logger.info("Received request to validate rule file: {}", file.getOriginalFilename());
        
        try {
            Map<String, Object> result = ruleManagementService.validateRuleFile(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            logger.error("Error validating rule file", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "valid", false,
                    "message", "Error validating rule file: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Gets the current status of the rule engine and loaded rules.
     *
     * @return A response containing rule engine status information
     */
    @GetMapping("/status")
    @Operation(summary = "Get rule engine status", 
               description = "Gets the current status of the rule engine and loaded rules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule engine status"),
            @ApiResponse(responseCode = "500", description = "Server error retrieving status")
    })
    public ResponseEntity<Map<String, Object>> getRuleStatus() {
        logger.info("Received request to get rule status");
        
        try {
            Map<String, Object> status = ruleManagementService.getRuleStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting rule status", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "Error getting rule status: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Reloads all rules from their sources.
     *
     * @return A response containing the reload result information
     */
    @PostMapping("/reload")
    @Operation(summary = "Reload rules", 
               description = "Reloads all rules from their sources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rules reloaded successfully"),
            @ApiResponse(responseCode = "500", description = "Server error during reload")
    })
    public ResponseEntity<Map<String, Object>> reloadRules() {
        logger.info("Received request to reload rules");
        
        try {
            Map<String, Object> result = ruleManagementService.reloadRules();
            
            if (result.containsKey("success") && result.get("success").equals(false)) {
                return ResponseEntity.internalServerError().body(result);
            }
            
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            logger.error("Error reloading rules", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error reloading rules: " + e.getMessage()
            ));
        }
    }
}