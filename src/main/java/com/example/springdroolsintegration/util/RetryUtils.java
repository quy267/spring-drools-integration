package com.example.springdroolsintegration.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

/**
 * Utility class for retrying operations that might fail transiently.
 * This class provides methods for retrying operations with configurable retry policies.
 */
public class RetryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);
    
    /**
     * Default maximum number of retry attempts.
     */
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    
    /**
     * Default delay between retry attempts in milliseconds.
     */
    private static final long DEFAULT_DELAY_MS = 1000;
    
    /**
     * Default backoff multiplier for exponential backoff.
     */
    private static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0;
    
    /**
     * Retries an operation with the default retry policy.
     *
     * @param operation The operation to retry
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts
     */
    public static <T> T retry(Callable<T> operation) throws Exception {
        return retry(operation, DEFAULT_MAX_ATTEMPTS, DEFAULT_DELAY_MS, DEFAULT_BACKOFF_MULTIPLIER, null);
    }
    
    /**
     * Retries an operation with a custom retry policy.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of retry attempts
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts) throws Exception {
        return retry(operation, maxAttempts, DEFAULT_DELAY_MS, DEFAULT_BACKOFF_MULTIPLIER, null);
    }
    
    /**
     * Retries an operation with a custom retry policy.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of retry attempts
     * @param delayMs The delay between retry attempts in milliseconds
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts, long delayMs) throws Exception {
        return retry(operation, maxAttempts, delayMs, DEFAULT_BACKOFF_MULTIPLIER, null);
    }
    
    /**
     * Retries an operation with a custom retry policy.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of retry attempts
     * @param delayMs The delay between retry attempts in milliseconds
     * @param backoffMultiplier The multiplier for exponential backoff
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts, long delayMs, double backoffMultiplier) throws Exception {
        return retry(operation, maxAttempts, delayMs, backoffMultiplier, null);
    }
    
    /**
     * Retries an operation with a custom retry policy.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of retry attempts
     * @param delayMs The delay between retry attempts in milliseconds
     * @param backoffMultiplier The multiplier for exponential backoff
     * @param retryableExceptions The exceptions that should trigger a retry
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts, long delayMs, double backoffMultiplier,
                             List<Class<? extends Exception>> retryableExceptions) throws Exception {
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxAttempts) {
            try {
                return operation.call();
            } catch (Exception e) {
                attempts++;
                lastException = e;
                
                // Check if the exception is retryable
                if (retryableExceptions != null && !isRetryable(e, retryableExceptions)) {
                    LoggingUtils.logError(logger, "Non-retryable exception occurred, aborting retry: {}", e, e.getMessage());
                    throw e;
                }
                
                if (attempts >= maxAttempts) {
                    LoggingUtils.logError(logger, "Operation failed after {} attempts: {}", e, attempts, e.getMessage());
                    throw e;
                }
                
                // Calculate delay with exponential backoff
                long currentDelay = (long) (delayMs * Math.pow(backoffMultiplier, attempts - 1));
                
                LoggingUtils.logWarn(logger, "Retry attempt {} failed: {}. Retrying in {} ms...", 
                        attempts, e.getMessage(), currentDelay);
                
                // Wait before retrying
                try {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        
        // This should never happen, but just in case
        throw lastException != null ? lastException : new RuntimeException("Retry failed for unknown reason");
    }
    
    /**
     * Retries an operation with a custom retry policy and a predicate to determine if the result is valid.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of retry attempts
     * @param delayMs The delay between retry attempts in milliseconds
     * @param backoffMultiplier The multiplier for exponential backoff
     * @param resultValidator A predicate to determine if the result is valid
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception if the operation fails after all retry attempts or if the result is invalid
     */
    public static <T> T retryUntilValid(Callable<T> operation, int maxAttempts, long delayMs, double backoffMultiplier,
                                      Predicate<T> resultValidator) throws Exception {
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxAttempts) {
            try {
                T result = operation.call();
                
                // Check if the result is valid
                if (resultValidator.test(result)) {
                    return result;
                }
                
                attempts++;
                
                if (attempts >= maxAttempts) {
                    LoggingUtils.logError(logger, "Operation failed to produce valid result after {} attempts", attempts);
                    throw new RuntimeException("Operation failed to produce valid result after " + attempts + " attempts");
                }
                
                // Calculate delay with exponential backoff
                long currentDelay = (long) (delayMs * Math.pow(backoffMultiplier, attempts - 1));
                
                LoggingUtils.logWarn(logger, "Retry attempt {} produced invalid result. Retrying in {} ms...", 
                        attempts, currentDelay);
                
                // Wait before retrying
                try {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            } catch (Exception e) {
                attempts++;
                lastException = e;
                
                if (attempts >= maxAttempts) {
                    LoggingUtils.logError(logger, "Operation failed after {} attempts: {}", e, attempts, e.getMessage());
                    throw e;
                }
                
                // Calculate delay with exponential backoff
                long currentDelay = (long) (delayMs * Math.pow(backoffMultiplier, attempts - 1));
                
                LoggingUtils.logWarn(logger, "Retry attempt {} failed: {}. Retrying in {} ms...", 
                        attempts, e.getMessage(), currentDelay);
                
                // Wait before retrying
                try {
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        
        // This should never happen, but just in case
        throw lastException != null ? lastException : new RuntimeException("Retry failed for unknown reason");
    }
    
    /**
     * Checks if an exception is retryable.
     *
     * @param exception The exception to check
     * @param retryableExceptions The list of retryable exception classes
     * @return true if the exception is retryable, false otherwise
     */
    private static boolean isRetryable(Exception exception, List<Class<? extends Exception>> retryableExceptions) {
        return retryableExceptions.stream()
                .anyMatch(clazz -> clazz.isInstance(exception));
    }
    
    /**
     * Creates a list of retryable exceptions.
     *
     * @param exceptions The exception classes to include
     * @return A list of retryable exception classes
     */
    @SafeVarargs
    public static List<Class<? extends Exception>> retryableExceptions(Class<? extends Exception>... exceptions) {
        return Arrays.asList(exceptions);
    }
}