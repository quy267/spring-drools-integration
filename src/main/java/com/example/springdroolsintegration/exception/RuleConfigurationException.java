package com.example.springdroolsintegration.exception;

/**
 * Exception thrown when there is an error in rule configuration.
 * This exception is used to provide detailed information about configuration errors in the rule engine.
 */
public class RuleConfigurationException extends RuntimeException {
    
    private final String configKey;
    private final String configValue;
    private final ErrorType errorType;
    private final String correlationId;
    
    /**
     * Constructs a new RuleConfigurationException with the specified detail message.
     *
     * @param message the detail message
     * @param configKey the configuration key that caused the error
     * @param errorType the type of configuration error
     */
    public RuleConfigurationException(String message, String configKey, ErrorType errorType) {
        super(message);
        this.configKey = configKey;
        this.configValue = null;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleConfigurationException with the specified detail message.
     *
     * @param message the detail message
     * @param configKey the configuration key that caused the error
     * @param configValue the configuration value that caused the error
     * @param errorType the type of configuration error
     */
    public RuleConfigurationException(String message, String configKey, String configValue, ErrorType errorType) {
        super(message);
        this.configKey = configKey;
        this.configValue = configValue;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleConfigurationException with the specified detail message and correlation ID.
     *
     * @param message the detail message
     * @param configKey the configuration key that caused the error
     * @param errorType the type of configuration error
     * @param correlationId the correlation ID for tracking the request
     */
    public RuleConfigurationException(String message, String configKey, ErrorType errorType, String correlationId) {
        super(message);
        this.configKey = configKey;
        this.configValue = null;
        this.errorType = errorType;
        this.correlationId = correlationId;
    }
    
    /**
     * Constructs a new RuleConfigurationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param configKey the configuration key that caused the error
     * @param errorType the type of configuration error
     */
    public RuleConfigurationException(String message, Throwable cause, String configKey, ErrorType errorType) {
        super(message, cause);
        this.configKey = configKey;
        this.configValue = null;
        this.errorType = errorType;
        this.correlationId = null;
    }
    
    /**
     * Constructs a new RuleConfigurationException with the specified detail message, cause, and correlation ID.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param configKey the configuration key that caused the error
     * @param configValue the configuration value that caused the error
     * @param errorType the type of configuration error
     * @param correlationId the correlation ID for tracking the request
     */
    public RuleConfigurationException(String message, Throwable cause, String configKey, String configValue, 
                                     ErrorType errorType, String correlationId) {
        super(message, cause);
        this.configKey = configKey;
        this.configValue = configValue;
        this.errorType = errorType;
        this.correlationId = correlationId;
    }
    
    /**
     * Gets the configuration key that caused the error.
     *
     * @return the configuration key
     */
    public String getConfigKey() {
        return configKey;
    }
    
    /**
     * Gets the configuration value that caused the error.
     *
     * @return the configuration value, or null if not applicable
     */
    public String getConfigValue() {
        return configValue;
    }
    
    /**
     * Gets the type of configuration error.
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
     * Enum representing different types of rule configuration errors.
     */
    public enum ErrorType {
        /**
         * A required configuration property is missing.
         */
        MISSING_PROPERTY,
        
        /**
         * A configuration property has an invalid value.
         */
        INVALID_PROPERTY_VALUE,
        
        /**
         * A configuration property has an invalid format.
         */
        INVALID_PROPERTY_FORMAT,
        
        /**
         * A configuration property conflicts with another property.
         */
        PROPERTY_CONFLICT,
        
        /**
         * The rule file path is invalid or not accessible.
         */
        INVALID_RULE_PATH,
        
        /**
         * The decision table path is invalid or not accessible.
         */
        INVALID_DECISION_TABLE_PATH,
        
        /**
         * The KieBase configuration is invalid.
         */
        INVALID_KIEBASE_CONFIG,
        
        /**
         * The KieSession configuration is invalid.
         */
        INVALID_KIESESSION_CONFIG,
        
        /**
         * Other configuration error.
         */
        OTHER
    }
}