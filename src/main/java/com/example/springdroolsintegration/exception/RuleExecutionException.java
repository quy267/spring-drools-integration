package com.example.springdroolsintegration.exception;

/**
 * Exception thrown when there is an error during rule execution.
 * This exception will be used to handle and report rule execution failures.
 */
public class RuleExecutionException extends RuntimeException {
    
    // Placeholder for RuleExecutionException implementation
    // Will be implemented in Phase 6: Error Handling and Observability
    
    public RuleExecutionException(String message) {
        super(message);
    }
    
    public RuleExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}