package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.service.RuleMetricsService;
import com.example.springdroolsintegration.util.LoggingUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the RuleMetricsService interface.
 * This service uses Micrometer to record and track metrics for rule execution.
 */
@Service
public class RuleMetricsServiceImpl implements RuleMetricsService {

    private static final Logger logger = LoggerFactory.getLogger(RuleMetricsServiceImpl.class);
    
    private final MeterRegistry meterRegistry;
    
    // In-memory counters for quick access to current values
    private final Map<String, AtomicLong> ruleHitCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> ruleMissCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> ruleErrorCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalExecutionCounters = new ConcurrentHashMap<>();
    
    /**
     * Constructor for RuleMetricsServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param meterRegistry The Micrometer registry for metrics
     */
    public RuleMetricsServiceImpl(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        LoggingUtils.logInfo(logger, "RuleMetricsService initialized");
    }
    
    @Override
    public void recordRuleExecutionTime(String ruleName, long executionTimeMs) {
        if (ruleName == null || ruleName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot record execution time for null or empty rule name");
            return;
        }
        
        LoggingUtils.logDebug(logger, "Recording execution time for rule {}: {} ms", ruleName, executionTimeMs);
        
        // Record the execution time using Micrometer Timer
        Timer.builder("drools.rule.execution.time")
                .tag("rule", ruleName)
                .description("Rule execution time")
                .register(meterRegistry)
                .record(executionTimeMs, TimeUnit.MILLISECONDS);
        
        // Increment the total execution counter
        getOrCreateTotalExecutionCounter(ruleName).incrementAndGet();
    }
    
    @Override
    public void recordPackageExecutionTime(String packageName, long executionTimeMs) {
        if (packageName == null || packageName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot record execution time for null or empty package name");
            return;
        }
        
        LoggingUtils.logDebug(logger, "Recording execution time for package {}: {} ms", packageName, executionTimeMs);
        
        // Record the execution time using Micrometer Timer
        Timer.builder("drools.package.execution.time")
                .tag("package", packageName)
                .description("Package execution time")
                .register(meterRegistry)
                .record(executionTimeMs, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void recordRuleHit(String ruleName) {
        if (ruleName == null || ruleName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot record hit for null or empty rule name");
            return;
        }
        
        LoggingUtils.logDebug(logger, "Recording hit for rule {}", ruleName);
        
        // Increment the hit counter using Micrometer Counter
        Counter.builder("drools.rule.hits")
                .tag("rule", ruleName)
                .description("Rule hit count")
                .register(meterRegistry)
                .increment();
        
        // Increment the in-memory counter
        getOrCreateRuleHitCounter(ruleName).incrementAndGet();
    }
    
    @Override
    public void recordRuleMiss(String ruleName) {
        if (ruleName == null || ruleName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot record miss for null or empty rule name");
            return;
        }
        
        LoggingUtils.logDebug(logger, "Recording miss for rule {}", ruleName);
        
        // Increment the miss counter using Micrometer Counter
        Counter.builder("drools.rule.misses")
                .tag("rule", ruleName)
                .description("Rule miss count")
                .register(meterRegistry)
                .increment();
        
        // Increment the in-memory counter
        getOrCreateRuleMissCounter(ruleName).incrementAndGet();
    }
    
    @Override
    public void recordRuleError(String ruleName, String errorType) {
        if (ruleName == null || ruleName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot record error for null or empty rule name");
            return;
        }
        
        LoggingUtils.logDebug(logger, "Recording error for rule {}: {}", ruleName, errorType);
        
        // Increment the error counter using Micrometer Counter
        Counter.builder("drools.rule.errors")
                .tag("rule", ruleName)
                .tag("errorType", errorType != null ? errorType : "unknown")
                .description("Rule error count")
                .register(meterRegistry)
                .increment();
        
        // Increment the in-memory counter
        getOrCreateRuleErrorCounter(ruleName).incrementAndGet();
    }
    
    @Override
    public Map<String, Object> getRuleMetrics(String ruleName) {
        if (ruleName == null || ruleName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot get metrics for null or empty rule name");
            return Map.of("error", "Rule name cannot be null or empty");
        }
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Get hit count
        long hitCount = getOrCreateRuleHitCounter(ruleName).get();
        metrics.put("hitCount", hitCount);
        
        // Get miss count
        long missCount = getOrCreateRuleMissCounter(ruleName).get();
        metrics.put("missCount", missCount);
        
        // Get error count
        long errorCount = getOrCreateRuleErrorCounter(ruleName).get();
        metrics.put("errorCount", errorCount);
        
        // Get total execution count
        long totalCount = getOrCreateTotalExecutionCounter(ruleName).get();
        metrics.put("totalExecutions", totalCount);
        
        // Calculate hit rate
        double hitRate = totalCount > 0 ? (double) hitCount / totalCount * 100.0 : 0.0;
        metrics.put("hitRate", hitRate);
        
        // Calculate error rate
        double errorRate = totalCount > 0 ? (double) errorCount / totalCount * 100.0 : 0.0;
        metrics.put("errorRate", errorRate);
        
        // Get execution time statistics from Micrometer
        Timer timer = meterRegistry.find("drools.rule.execution.time")
                .tag("rule", ruleName)
                .timer();
        
        if (timer != null) {
            metrics.put("executionCount", timer.count());
            metrics.put("totalExecutionTimeMs", timer.totalTime(TimeUnit.MILLISECONDS));
            metrics.put("meanExecutionTimeMs", timer.mean(TimeUnit.MILLISECONDS));
            metrics.put("maxExecutionTimeMs", timer.max(TimeUnit.MILLISECONDS));
        } else {
            metrics.put("executionCount", 0L);
            metrics.put("totalExecutionTimeMs", 0.0);
            metrics.put("meanExecutionTimeMs", 0.0);
            metrics.put("maxExecutionTimeMs", 0.0);
        }
        
        return metrics;
    }
    
    @Override
    public Map<String, Object> getPackageMetrics(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            LoggingUtils.logWarn(logger, "Cannot get metrics for null or empty package name");
            return Map.of("error", "Package name cannot be null or empty");
        }
        
        Map<String, Object> metrics = new HashMap<>();
        
        // Get execution time statistics from Micrometer
        Timer timer = meterRegistry.find("drools.package.execution.time")
                .tag("package", packageName)
                .timer();
        
        if (timer != null) {
            metrics.put("executionCount", timer.count());
            metrics.put("totalExecutionTimeMs", timer.totalTime(TimeUnit.MILLISECONDS));
            metrics.put("meanExecutionTimeMs", timer.mean(TimeUnit.MILLISECONDS));
            metrics.put("maxExecutionTimeMs", timer.max(TimeUnit.MILLISECONDS));
        } else {
            metrics.put("executionCount", 0L);
            metrics.put("totalExecutionTimeMs", 0.0);
            metrics.put("meanExecutionTimeMs", 0.0);
            metrics.put("maxExecutionTimeMs", 0.0);
        }
        
        return metrics;
    }
    
    @Override
    public Map<String, Object> getOverallMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Calculate total hit count
        long totalHitCount = ruleHitCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        metrics.put("totalHitCount", totalHitCount);
        
        // Calculate total miss count
        long totalMissCount = ruleMissCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        metrics.put("totalMissCount", totalMissCount);
        
        // Calculate total error count
        long totalErrorCount = ruleErrorCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        metrics.put("totalErrorCount", totalErrorCount);
        
        // Calculate total execution count
        long totalExecutionCount = totalExecutionCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
        metrics.put("totalExecutions", totalExecutionCount);
        
        // Calculate overall hit rate
        double overallHitRate = totalExecutionCount > 0 ? 
                (double) totalHitCount / totalExecutionCount * 100.0 : 0.0;
        metrics.put("overallHitRate", overallHitRate);
        
        // Calculate overall error rate
        double overallErrorRate = totalExecutionCount > 0 ? 
                (double) totalErrorCount / totalExecutionCount * 100.0 : 0.0;
        metrics.put("overallErrorRate", overallErrorRate);
        
        // Get rule execution time statistics from Micrometer
        Timer ruleTimer = meterRegistry.find("drools.rule.execution.time").timer();
        if (ruleTimer != null) {
            metrics.put("ruleExecutionCount", ruleTimer.count());
            metrics.put("totalRuleExecutionTimeMs", ruleTimer.totalTime(TimeUnit.MILLISECONDS));
            metrics.put("meanRuleExecutionTimeMs", ruleTimer.mean(TimeUnit.MILLISECONDS));
            metrics.put("maxRuleExecutionTimeMs", ruleTimer.max(TimeUnit.MILLISECONDS));
        } else {
            metrics.put("ruleExecutionCount", 0L);
            metrics.put("totalRuleExecutionTimeMs", 0.0);
            metrics.put("meanRuleExecutionTimeMs", 0.0);
            metrics.put("maxRuleExecutionTimeMs", 0.0);
        }
        
        // Get package execution time statistics from Micrometer
        Timer packageTimer = meterRegistry.find("drools.package.execution.time").timer();
        if (packageTimer != null) {
            metrics.put("packageExecutionCount", packageTimer.count());
            metrics.put("totalPackageExecutionTimeMs", packageTimer.totalTime(TimeUnit.MILLISECONDS));
            metrics.put("meanPackageExecutionTimeMs", packageTimer.mean(TimeUnit.MILLISECONDS));
            metrics.put("maxPackageExecutionTimeMs", packageTimer.max(TimeUnit.MILLISECONDS));
        } else {
            metrics.put("packageExecutionCount", 0L);
            metrics.put("totalPackageExecutionTimeMs", 0.0);
            metrics.put("meanPackageExecutionTimeMs", 0.0);
            metrics.put("maxPackageExecutionTimeMs", 0.0);
        }
        
        return metrics;
    }
    
    @Override
    public void resetMetrics() {
        LoggingUtils.logInfo(logger, "Resetting all rule metrics");
        
        // Clear in-memory counters
        ruleHitCounters.clear();
        ruleMissCounters.clear();
        ruleErrorCounters.clear();
        totalExecutionCounters.clear();
        
        // Note: Micrometer doesn't provide a way to reset metrics
        // In a production environment, you might want to use a different approach
        // such as creating new meters with different tags
    }
    
    /**
     * Gets or creates a rule hit counter.
     *
     * @param ruleName The name of the rule
     * @return The rule hit counter
     */
    private AtomicLong getOrCreateRuleHitCounter(String ruleName) {
        return ruleHitCounters.computeIfAbsent(ruleName, k -> new AtomicLong(0));
    }
    
    /**
     * Gets or creates a rule miss counter.
     *
     * @param ruleName The name of the rule
     * @return The rule miss counter
     */
    private AtomicLong getOrCreateRuleMissCounter(String ruleName) {
        return ruleMissCounters.computeIfAbsent(ruleName, k -> new AtomicLong(0));
    }
    
    /**
     * Gets or creates a rule error counter.
     *
     * @param ruleName The name of the rule
     * @return The rule error counter
     */
    private AtomicLong getOrCreateRuleErrorCounter(String ruleName) {
        return ruleErrorCounters.computeIfAbsent(ruleName, k -> new AtomicLong(0));
    }
    
    /**
     * Gets or creates a total execution counter.
     *
     * @param ruleName The name of the rule
     * @return The total execution counter
     */
    private AtomicLong getOrCreateTotalExecutionCounter(String ruleName) {
        return totalExecutionCounters.computeIfAbsent(ruleName, k -> new AtomicLong(0));
    }
}