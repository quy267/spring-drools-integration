package com.example.springdroolsintegration.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utility class for structured logging with correlation IDs.
 * This class provides methods for logging with correlation IDs and other structured information.
 */
public class LoggingUtils {

    private static final String CORRELATION_ID = "correlationId";
    
    /**
     * Gets the current correlation ID from MDC or creates a new one if not present.
     *
     * @return The correlation ID
     */
    public static String getOrCreateCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID, correlationId);
        }
        return correlationId;
    }
    
    /**
     * Sets the correlation ID in MDC.
     *
     * @param correlationId The correlation ID to set
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId != null) {
            MDC.put(CORRELATION_ID, correlationId);
        }
    }
    
    /**
     * Clears the correlation ID from MDC.
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID);
    }
    
    /**
     * Logs an info message with the current correlation ID.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param args The message arguments
     */
    public static void logInfo(Logger logger, String message, Object... args) {
        String correlationId = getOrCreateCorrelationId();
        logger.info("[correlationId={}] {}", correlationId, formatMessage(message, args));
    }
    
    /**
     * Logs a warning message with the current correlation ID.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param args The message arguments
     */
    public static void logWarn(Logger logger, String message, Object... args) {
        String correlationId = getOrCreateCorrelationId();
        logger.warn("[correlationId={}] {}", correlationId, formatMessage(message, args));
    }
    
    /**
     * Logs an error message with the current correlation ID.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param args The message arguments
     */
    public static void logError(Logger logger, String message, Object... args) {
        String correlationId = getOrCreateCorrelationId();
        logger.error("[correlationId={}] {}", correlationId, formatMessage(message, args));
    }
    
    /**
     * Logs an error message with the current correlation ID and an exception.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param throwable The exception to log
     * @param args The message arguments
     */
    public static void logError(Logger logger, String message, Throwable throwable, Object... args) {
        String correlationId = getOrCreateCorrelationId();
        logger.error("[correlationId={}] {}", correlationId, formatMessage(message, args), throwable);
    }
    
    /**
     * Logs a debug message with the current correlation ID.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param args The message arguments
     */
    public static void logDebug(Logger logger, String message, Object... args) {
        if (logger.isDebugEnabled()) {
            String correlationId = getOrCreateCorrelationId();
            logger.debug("[correlationId={}] {}", correlationId, formatMessage(message, args));
        }
    }
    
    /**
     * Logs a trace message with the current correlation ID.
     *
     * @param logger The logger to use
     * @param message The message to log
     * @param args The message arguments
     */
    public static void logTrace(Logger logger, String message, Object... args) {
        if (logger.isTraceEnabled()) {
            String correlationId = getOrCreateCorrelationId();
            logger.trace("[correlationId={}] {}", correlationId, formatMessage(message, args));
        }
    }
    
    /**
     * Formats a message with arguments.
     *
     * @param message The message to format
     * @param args The message arguments
     * @return The formatted message
     */
    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        
        // Simple string formatting for arguments
        String formattedMessage = message;
        for (Object arg : args) {
            formattedMessage = formattedMessage.replaceFirst("\\{\\}", String.valueOf(arg));
        }
        
        return formattedMessage;
    }
}