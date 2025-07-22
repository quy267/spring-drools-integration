package com.example.springdroolsintegration.exception;

/**
 * Exception thrown when there is an error during rule execution.
 * This exception is used to provide detailed information about failures during rule execution.
 */
public class RuleExecutionException extends RuntimeException {
    
    private final String ruleName;
    private final String rulePackage;
    private final ErrorType errorType;
    private final String correlationId;
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message.
     *
     * @param message the detail message
     */
    public RuleExecutionException(String message) {
        super(message);
        this.ruleName = null;
        this.rulePackage = null;
        this.errorType = ErrorType.OTHER;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public RuleExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.ruleName = null;
        this.rulePackage = null;
        this.errorType = ErrorType.OTHER;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message.
     *
     * @param message the detail message
     * @param ruleName the name of the rule that failed execution
     * @param errorType the type of execution error
     */
    public RuleExecutionException(String message, String ruleName, ErrorType errorType) {
        super(message);
        this.ruleName = ruleName;
        this.rulePackage = null;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message.
     *
     * @param message the detail message
     * @param ruleName the name of the rule that failed execution
     * @param rulePackage the package of the rule that failed execution
     * @param errorType the type of execution error
     */
    public RuleExecutionException(String message, String ruleName, String rulePackage, ErrorType errorType) {
        super(message);
        this.ruleName = ruleName;
        this.rulePackage = rulePackage;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message and correlation ID.
     *
     * @param message the detail message
     * @param ruleName the name of the rule that failed execution
     * @param errorType the type of execution error
     * @param correlationId the correlation ID for tracking the request
     */
    public RuleExecutionException(String message, String ruleName, ErrorType errorType, String correlationId) {
        super(message);
        this.ruleName = ruleName;
        this.rulePackage = null;
        this.errorType = errorType;
        this.correlationId = correlationId;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param ruleName the name of the rule that failed execution
     * @param errorType the type of execution error
     */
    public RuleExecutionException(String message, Throwable cause, String ruleName, ErrorType errorType) {
        super(message, cause);
        this.ruleName = ruleName;
        this.rulePackage = null;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleExecutionException with the specified detail message, cause, and correlation ID.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param ruleName the name of the rule that failed execution
     * @param errorType the type of execution error
     * @param correlationId the correlation ID for tracking the request
     */
    public RuleExecutionException(String message, Throwable cause, String ruleName, ErrorType errorType, String correlationId) {
        super(message, cause);
        this.ruleName = ruleName;
        this.rulePackage = null;
        this.errorType = errorType;
        this.correlationId = correlationId;
    }
    
    /**
     * Gets the name of the rule that failed execution.
     *
     * @return the rule name
     */
    public String getRuleName() {
        return ruleName;
    }
    
    /**
     * Gets the package of the rule that failed execution.
     *
     * @return the rule package, or null if not applicable
     */
    public String getRulePackage() {
        return rulePackage;
    }
    
    /**
     * Gets the type of execution error.
     *
     * @return the error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Gets the correlation ID for tracking the request.
     *
     * @return the correlation ID, or null if not applicable
     */
    public String getCorrelationId() {
        return correlationId;
    }
    
    /**
     * Enum representing different types of rule execution errors.
     */
    public enum ErrorType {
        /**
         * The rule could not be found.
         */
        RULE_NOT_FOUND,
        
        /**
         * The rule execution timed out.
         */
        EXECUTION_TIMEOUT,
        
        /**
         * The rule execution failed due to a compilation error.
         */
        COMPILATION_ERROR,
        
        /**
         * The rule execution failed due to invalid input data.
         */
        INVALID_INPUT,
        
        /**
         * The rule execution failed due to a runtime error.
         */
        RUNTIME_ERROR,
        
        /**
         * The rule execution failed due to a conflict with another rule.
         */
        RULE_CONFLICT,
        
        /**
         * Other execution error.
         */
        OTHER
    }
}