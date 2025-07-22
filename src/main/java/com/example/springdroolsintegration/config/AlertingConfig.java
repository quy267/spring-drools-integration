package com.example.springdroolsintegration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * Configuration properties for alerting thresholds.
 * This class defines thresholds for various metrics that trigger alerts when exceeded.
 */
@Configuration
@ConfigurationProperties(prefix = "app.alerting")
@Validated
public class AlertingConfig {

    private static final Logger logger = LoggerFactory.getLogger(AlertingConfig.class);
    
    /**
     * Execution time thresholds in milliseconds.
     */
    @NotNull
    private ExecutionTimeThresholds executionTime = new ExecutionTimeThresholds();
    
    /**
     * Session pool thresholds.
     */
    @NotNull
    private SessionPoolThresholds sessionPool = new SessionPoolThresholds();
    
    /**
     * Success rate thresholds in percentage.
     */
    @NotNull
    private SuccessRateThresholds successRate = new SuccessRateThresholds();
    
    /**
     * Whether alerting is enabled.
     */
    private boolean enabled = true;
    
    /**
     * Execution time thresholds in milliseconds.
     */
    public static class ExecutionTimeThresholds {
        /**
         * Warning threshold for rule execution time in milliseconds.
         */
        @Min(1)
        private long warningMs = 100;
        
        /**
         * Critical threshold for rule execution time in milliseconds.
         */
        @Min(1)
        private long criticalMs = 500;
        
        public long getWarningMs() {
            return warningMs;
        }
        
        public void setWarningMs(long warningMs) {
            this.warningMs = warningMs;
        }
        
        public long getCriticalMs() {
            return criticalMs;
        }
        
        public void setCriticalMs(long criticalMs) {
            this.criticalMs = criticalMs;
        }
    }
    
    /**
     * Session pool thresholds.
     */
    public static class SessionPoolThresholds {
        /**
         * Warning threshold for session pool utilization percentage.
         */
        @Min(1)
        @Max(100)
        private int warningUtilizationPercent = 80;
        
        /**
         * Critical threshold for session pool utilization percentage.
         */
        @Min(1)
        @Max(100)
        private int criticalUtilizationPercent = 95;
        
        /**
         * Maximum allowed session pool size.
         */
        @Min(1)
        private int maxPoolSize = 20;
        
        public int getWarningUtilizationPercent() {
            return warningUtilizationPercent;
        }
        
        public void setWarningUtilizationPercent(int warningUtilizationPercent) {
            this.warningUtilizationPercent = warningUtilizationPercent;
        }
        
        public int getCriticalUtilizationPercent() {
            return criticalUtilizationPercent;
        }
        
        public void setCriticalUtilizationPercent(int criticalUtilizationPercent) {
            this.criticalUtilizationPercent = criticalUtilizationPercent;
        }
        
        public int getMaxPoolSize() {
            return maxPoolSize;
        }
        
        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }
    
    /**
     * Success rate thresholds in percentage.
     */
    public static class SuccessRateThresholds {
        /**
         * Warning threshold for rule execution success rate percentage.
         */
        @Min(0)
        @Max(100)
        private double warningPercent = 95.0;
        
        /**
         * Critical threshold for rule execution success rate percentage.
         */
        @Min(0)
        @Max(100)
        private double criticalPercent = 90.0;
        
        public double getWarningPercent() {
            return warningPercent;
        }
        
        public void setWarningPercent(double warningPercent) {
            this.warningPercent = warningPercent;
        }
        
        public double getCriticalPercent() {
            return criticalPercent;
        }
        
        public void setCriticalPercent(double criticalPercent) {
            this.criticalPercent = criticalPercent;
        }
    }
    
    public ExecutionTimeThresholds getExecutionTime() {
        return executionTime;
    }
    
    public void setExecutionTime(ExecutionTimeThresholds executionTime) {
        this.executionTime = executionTime;
    }
    
    public SessionPoolThresholds getSessionPool() {
        return sessionPool;
    }
    
    public void setSessionPool(SessionPoolThresholds sessionPool) {
        this.sessionPool = sessionPool;
    }
    
    public SuccessRateThresholds getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(SuccessRateThresholds successRate) {
        this.successRate = successRate;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Checks if the execution time exceeds the warning threshold.
     *
     * @param executionTimeMs The execution time in milliseconds
     * @return true if the execution time exceeds the warning threshold
     */
    public boolean isExecutionTimeWarning(long executionTimeMs) {
        return executionTimeMs >= executionTime.getWarningMs();
    }
    
    /**
     * Checks if the execution time exceeds the critical threshold.
     *
     * @param executionTimeMs The execution time in milliseconds
     * @return true if the execution time exceeds the critical threshold
     */
    public boolean isExecutionTimeCritical(long executionTimeMs) {
        return executionTimeMs >= executionTime.getCriticalMs();
    }
    
    /**
     * Checks if the session pool utilization exceeds the warning threshold.
     *
     * @param poolSize The current pool size
     * @param maxSize The maximum pool size
     * @return true if the pool utilization exceeds the warning threshold
     */
    public boolean isSessionPoolWarning(int poolSize, int maxSize) {
        double utilizationPercent = (double) poolSize / maxSize * 100;
        return utilizationPercent >= sessionPool.getWarningUtilizationPercent();
    }
    
    /**
     * Checks if the session pool utilization exceeds the critical threshold.
     *
     * @param poolSize The current pool size
     * @param maxSize The maximum pool size
     * @return true if the pool utilization exceeds the critical threshold
     */
    public boolean isSessionPoolCritical(int poolSize, int maxSize) {
        double utilizationPercent = (double) poolSize / maxSize * 100;
        return utilizationPercent >= sessionPool.getCriticalUtilizationPercent();
    }
    
    /**
     * Checks if the success rate is below the warning threshold.
     *
     * @param successRatePercent The success rate percentage
     * @return true if the success rate is below the warning threshold
     */
    public boolean isSuccessRateWarning(double successRatePercent) {
        return successRatePercent <= successRate.getWarningPercent();
    }
    
    /**
     * Checks if the success rate is below the critical threshold.
     *
     * @param successRatePercent The success rate percentage
     * @return true if the success rate is below the critical threshold
     */
    public boolean isSuccessRateCritical(double successRatePercent) {
        return successRatePercent <= successRate.getCriticalPercent();
    }
}