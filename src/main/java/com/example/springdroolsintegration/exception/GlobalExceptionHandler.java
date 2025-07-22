package com.example.springdroolsintegration.exception;

import com.example.springdroolsintegration.util.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles all exceptions thrown by the application and returns appropriate responses.
 * It uses ProblemDetail (RFC 7807) for error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles RuleExecutionException.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(RuleExecutionException.class)
    public ResponseEntity<ProblemDetail> handleRuleExecutionException(
            RuleExecutionException ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(ex.getCorrelationId());
        
        LoggingUtils.logError(logger, "Rule execution error: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        
        problemDetail.setTitle("Rule Execution Error");
        problemDetail.setType(URI.create("https://example.com/problems/rule-execution"));
        
        enrichProblemDetail(problemDetail, ex, correlationId);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Handles DecisionTableValidationException.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(DecisionTableValidationException.class)
    public ResponseEntity<ProblemDetail> handleDecisionTableValidationException(
            DecisionTableValidationException ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(null);
        
        LoggingUtils.logError(logger, "Decision table validation error: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        
        problemDetail.setTitle("Decision Table Validation Error");
        problemDetail.setType(URI.create("https://example.com/problems/decision-table-validation"));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", ex.getErrorType().name());
        properties.put("filename", ex.getFilename());
        if (ex.getSheetName() != null) {
            properties.put("sheetName", ex.getSheetName());
        }
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        problemDetail.setProperties(properties);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Handles RuleConfigurationException.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(RuleConfigurationException.class)
    public ResponseEntity<ProblemDetail> handleRuleConfigurationException(
            RuleConfigurationException ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(ex.getCorrelationId());
        
        LoggingUtils.logError(logger, "Rule configuration error: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        
        problemDetail.setTitle("Rule Configuration Error");
        problemDetail.setType(URI.create("https://example.com/problems/rule-configuration"));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", ex.getErrorType().name());
        properties.put("configKey", ex.getConfigKey());
        if (ex.getConfigValue() != null) {
            properties.put("configValue", ex.getConfigValue());
        }
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        problemDetail.setProperties(properties);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Handles MaxUploadSizeExceededException.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ProblemDetail> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(null);
        
        LoggingUtils.logError(logger, "File upload size exceeded: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYLOAD_TOO_LARGE, 
                "The uploaded file exceeds the maximum allowed size");
        
        problemDetail.setTitle("File Upload Error");
        problemDetail.setType(URI.create("https://example.com/problems/file-upload"));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", "MAX_UPLOAD_SIZE_EXCEEDED");
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        problemDetail.setProperties(properties);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Handles MethodArgumentNotValidException.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(null);
        
        LoggingUtils.logError(logger, "Validation error: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        
        problemDetail.setTitle("Validation Error");
        problemDetail.setType(URI.create("https://example.com/problems/validation"));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", "VALIDATION_ERROR");
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        // Add validation errors
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
        properties.put("errors", errors);
        
        problemDetail.setProperties(properties);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Handles all other exceptions.
     * 
     * @param ex The exception
     * @param request The web request
     * @return A ResponseEntity with a ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(
            Exception ex, WebRequest request) {
        
        String correlationId = getOrCreateCorrelationId(null);
        
        LoggingUtils.logError(logger, "Unexpected error: {}", ex, ex.getMessage());
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://example.com/problems/internal-error"));
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", "INTERNAL_ERROR");
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        problemDetail.setProperties(properties);
        
        return ResponseEntity.of(problemDetail).build();
    }
    
    /**
     * Enriches a ProblemDetail with additional information from a RuleExecutionException.
     * 
     * @param problemDetail The ProblemDetail to enrich
     * @param ex The exception
     * @param correlationId The correlation ID
     */
    private void enrichProblemDetail(ProblemDetail problemDetail, RuleExecutionException ex, String correlationId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("errorType", ex.getErrorType().name());
        properties.put("ruleName", ex.getRuleName());
        if (ex.getRulePackage() != null) {
            properties.put("rulePackage", ex.getRulePackage());
        }
        properties.put("correlationId", correlationId);
        properties.put("timestamp", Instant.now());
        
        problemDetail.setProperties(properties);
    }
    
    /**
     * Gets an existing correlation ID or creates a new one.
     *
     * @param existingCorrelationId An existing correlation ID, or null
     * @return The correlation ID
     */
    private String getOrCreateCorrelationId(String existingCorrelationId) {
        if (existingCorrelationId != null) {
            LoggingUtils.setCorrelationId(existingCorrelationId);
            return existingCorrelationId;
        }
        
        return LoggingUtils.getOrCreateCorrelationId();
    }
}