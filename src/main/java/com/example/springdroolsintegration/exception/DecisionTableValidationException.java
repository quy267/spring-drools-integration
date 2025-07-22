package com.example.springdroolsintegration.exception;

/**
 * Exception thrown when a decision table validation fails.
 * This exception is used to provide detailed information about validation errors in decision tables.
 */
public class DecisionTableValidationException extends RuntimeException {
    
    private final String filename;
    private final String sheetName;
    private final ValidationErrorType errorType;
    
    /**
     * Constructs a new DecisionTableValidationException with the specified detail message.
     *
     * @param message the detail message
     * @param filename the name of the file that failed validation
     * @param errorType the type of validation error
     */
    public DecisionTableValidationException(String message, String filename, ValidationErrorType errorType) {
        super(message);
        this.filename = filename;
        this.sheetName = null;
        this.errorType = errorType;
    }
    
    /**
     * Constructs a new DecisionTableValidationException with the specified detail message.
     *
     * @param message the detail message
     * @param filename the name of the file that failed validation
     * @param sheetName the name of the sheet that failed validation
     * @param errorType the type of validation error
     */
    public DecisionTableValidationException(String message, String filename, String sheetName, ValidationErrorType errorType) {
        super(message);
        this.filename = filename;
        this.sheetName = sheetName;
        this.errorType = errorType;
    }
    
    /**
     * Constructs a new DecisionTableValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param filename the name of the file that failed validation
     * @param errorType the type of validation error
     */
    public DecisionTableValidationException(String message, Throwable cause, String filename, ValidationErrorType errorType) {
        super(message, cause);
        this.filename = filename;
        this.sheetName = null;
        this.errorType = errorType;
    }
    
    /**
     * Constructs a new DecisionTableValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     * @param filename the name of the file that failed validation
     * @param sheetName the name of the sheet that failed validation
     * @param errorType the type of validation error
     */
    public DecisionTableValidationException(String message, Throwable cause, String filename, String sheetName, ValidationErrorType errorType) {
        super(message, cause);
        this.filename = filename;
        this.sheetName = sheetName;
        this.errorType = errorType;
    }
    
    /**
     * Gets the name of the file that failed validation.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Gets the name of the sheet that failed validation.
     *
     * @return the sheet name, or null if not applicable
     */
    public String getSheetName() {
        return sheetName;
    }
    
    /**
     * Gets the type of validation error.
     *
     * @return the error type
     */
    public ValidationErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Enum representing different types of validation errors.
     */
    public enum ValidationErrorType {
        /**
         * The file format is invalid (not an Excel file).
         */
        INVALID_FILE_FORMAT,
        
        /**
         * The file is corrupted or cannot be read.
         */
        CORRUPTED_FILE,
        
        /**
         * The decision table is missing required headers.
         */
        MISSING_HEADERS,
        
        /**
         * The decision table has an invalid structure.
         */
        INVALID_STRUCTURE,
        
        /**
         * The decision table contains invalid data.
         */
        INVALID_DATA,
        
        /**
         * The decision table is empty.
         */
        EMPTY_TABLE,
        
        /**
         * Other validation error.
         */
        OTHER
    }
}