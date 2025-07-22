package com.example.springdroolsintegration.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Component for monitoring error rates in the application.
 * This class tracks error rates for different types of operations and provides methods
 * for checking if error thresholds have been exceeded.
 */
@Component
public class ErrorRateMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ErrorRateMonitor.class);
    
    /**
     * Default error threshold (percentage).
     */
    private static final double DEFAULT_ERROR_THRESHOLD = 10.0;
    
    /**
     * Default time window for error rate calculation (in milliseconds).
     */
    private static final long DEFAULT_TIME_WINDOW_MS = 60000; // 1 minute
    
    /**
     * Map of operation names to error counters.
     */
    private final Map<String, ErrorCounter> errorCounters = new ConcurrentHashMap<>();
    
    /**
     * Micrometer registry for metrics.
     */
    private final MeterRegistry meterRegistry;
    
    /**
     * Constructor for ErrorRateMonitor.
     *
     * @param meterRegistry The Micrometer registry for metrics
     */
    @Autowired
    public ErrorRateMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        LoggingUtils.logInfo(logger, "ErrorRateMonitor initialized with MeterRegistry");
    }
    
    /**
     * Records a successful operation.
     *
     * @param operationName The name of the operation
     */
    public void recordSuccess(String operationName) {
        getOrCreateErrorCounter(operationName).recordSuccess();
        
        // Update Micrometer metrics
        Counter.builder("app.operations.success")
                .tag("operation", operationName)
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * Records a failed operation.
     *
     * @param operationName The name of the operation
     * @param exception The exception that caused the failure
     */
    public void recordError(String operationName, Throwable exception) {
        getOrCreateErrorCounter(operationName).recordError();
        
        // Update Micrometer metrics
        Counter.builder("app.operations.error")
                .tag("operation", operationName)
                .tag("exception", exception.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
        
        // Log the error with correlation ID
        LoggingUtils.logError(logger, "Operation '{}' failed: {}", exception, operationName, exception.getMessage());
    }
    
    /**
     * Records a timed operation with success or failure.
     *
     * @param operationName The name of the operation
     * @param durationMs The duration of the operation in milliseconds
     * @param success Whether the operation was successful
     * @param exception The exception that caused the failure, or null if successful
     */
    public void recordOperation(String operationName, long durationMs, boolean success, Throwable exception) {
        if (success) {
            recordSuccess(operationName);
        } else {
            recordError(operationName, exception != null ? exception : new RuntimeException("Unknown error"));
        }
        
        // Update Micrometer metrics
        Timer.builder("app.operations.duration")
                .tag("operation", operationName)
                .tag("status", success ? "success" : "error")
                .register(meterRegistry)
                .record(Duration.ofMillis(durationMs));
    }
    
    /**
     * Gets the error rate for an operation.
     *
     * @param operationName The name of the operation
     * @return The error rate as a percentage (0-100)
     */
    public double getErrorRate(String operationName) {
        return getOrCreateErrorCounter(operationName).getErrorRate();
    }
    
    /**
     * Checks if the error rate for an operation exceeds the default threshold.
     *
     * @param operationName The name of the operation
     * @return true if the error rate exceeds the threshold, false otherwise
     */
    public boolean isErrorRateExceeded(String operationName) {
        return isErrorRateExceeded(operationName, DEFAULT_ERROR_THRESHOLD);
    }
    
    /**
     * Checks if the error rate for an operation exceeds a custom threshold.
     *
     * @param operationName The name of the operation
     * @param threshold The error threshold as a percentage (0-100)
     * @return true if the error rate exceeds the threshold, false otherwise
     */
    public boolean isErrorRateExceeded(String operationName, double threshold) {
        double errorRate = getErrorRate(operationName);
        boolean exceeded = errorRate > threshold;
        
        if (exceeded) {
            LoggingUtils.logWarn(logger, "Error rate for operation '{}' ({:.2f}%) exceeds threshold ({:.2f}%)",
                    operationName, errorRate, threshold);
        }
        
        return exceeded;
    }
    
    /**
     * Resets the error counter for an operation.
     *
     * @param operationName The name of the operation
     */
    public void resetErrorCounter(String operationName) {
        errorCounters.remove(operationName);
        LoggingUtils.logInfo(logger, "Error counter reset for operation '{}'", operationName);
    }
    
    /**
     * Gets or creates an error counter for an operation.
     *
     * @param operationName The name of the operation
     * @return The error counter
     */
    private ErrorCounter getOrCreateErrorCounter(String operationName) {
        return errorCounters.computeIfAbsent(operationName, name -> new ErrorCounter(DEFAULT_TIME_WINDOW_MS));
    }
    
    /**
     * Inner class for tracking error counts and rates.
     */
    private static class ErrorCounter {
        private final AtomicLong totalCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);
        private final long timeWindowMs;
        private final Map<Long, WindowedCount> windowedCounts = new ConcurrentHashMap<>();
        
        /**
         * Constructor for ErrorCounter.
         *
         * @param timeWindowMs The time window for error rate calculation in milliseconds
         */
        public ErrorCounter(long timeWindowMs) {
            this.timeWindowMs = timeWindowMs;
        }
        
        /**
         * Records a successful operation.
         */
        public void recordSuccess() {
            totalCount.incrementAndGet();
            long windowKey = getCurrentWindowKey();
            getOrCreateWindowedCount(windowKey).incrementTotal();
            removeExpiredWindows();
        }
        
        /**
         * Records a failed operation.
         */
        public void recordError() {
            totalCount.incrementAndGet();
            errorCount.incrementAndGet();
            long windowKey = getCurrentWindowKey();
            WindowedCount windowedCount = getOrCreateWindowedCount(windowKey);
            windowedCount.incrementTotal();
            windowedCount.incrementError();
            removeExpiredWindows();
        }
        
        /**
         * Gets the error rate as a percentage (0-100).
         *
         * @return The error rate
         */
        public double getErrorRate() {
            removeExpiredWindows();
            
            long windowedTotal = 0;
            long windowedErrors = 0;
            
            for (WindowedCount count : windowedCounts.values()) {
                windowedTotal += count.getTotal();
                windowedErrors += count.getErrors();
            }
            
            if (windowedTotal == 0) {
                return 0.0;
            }
            
            return (double) windowedErrors / windowedTotal * 100.0;
        }
        
        /**
         * Gets the current window key based on the current time.
         *
         * @return The window key
         */
        private long getCurrentWindowKey() {
            return System.currentTimeMillis() / timeWindowMs;
        }
        
        /**
         * Gets or creates a windowed count for a window key.
         *
         * @param windowKey The window key
         * @return The windowed count
         */
        private WindowedCount getOrCreateWindowedCount(long windowKey) {
            return windowedCounts.computeIfAbsent(windowKey, k -> new WindowedCount());
        }
        
        /**
         * Removes expired windows from the map.
         */
        private void removeExpiredWindows() {
            long currentKey = getCurrentWindowKey();
            windowedCounts.keySet().removeIf(key -> key < currentKey - 1);
        }
    }
    
    /**
     * Inner class for tracking counts within a time window.
     */
    private static class WindowedCount {
        private final AtomicLong total = new AtomicLong(0);
        private final AtomicLong errors = new AtomicLong(0);
        
        /**
         * Increments the total count.
         */
        public void incrementTotal() {
            total.incrementAndGet();
        }
        
        /**
         * Increments the error count.
         */
        public void incrementError() {
            errors.incrementAndGet();
        }
        
        /**
         * Gets the total count.
         *
         * @return The total count
         */
        public long getTotal() {
            return total.get();
        }
        
        /**
         * Gets the error count.
         *
         * @return The error count
         */
        public long getErrors() {
            return errors.get();
        }
    }
}