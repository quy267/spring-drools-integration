package com.example.springdroolsintegration.actuator;

import com.example.springdroolsintegration.config.AlertingConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom metrics for the rule engine.
 * This component registers and manages Micrometer metrics for rule execution.
 */
@Component
public class RuleEngineMetrics {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineMetrics.class);
    
    private final MeterRegistry meterRegistry;
    private final AlertingConfig alertingConfig;
    private final MemoryMXBean memoryMXBean;
    
    // Execution counters
    private final Counter totalExecutionsCounter;
    private final Counter successfulExecutionsCounter;
    private final Counter failedExecutionsCounter;
    private final Counter batchExecutionsCounter;
    private final Counter asyncExecutionsCounter;
    
    // Session metrics
    private final Counter sessionsCreatedCounter;
    private final Counter sessionsDisposedCounter;
    
    // Execution timers
    private final Timer ruleExecutionTimer;
    private final Timer batchExecutionTimer;
    private final Timer sessionCreationTimer;
    
    // Cache metrics
    private final Counter cacheHitsCounter;
    private final Counter cacheMissesCounter;
    private final AtomicLong cacheSize = new AtomicLong(0);
    private final AtomicLong cacheEvictions = new AtomicLong(0);
    
    // Rule-specific metrics
    private final Map<String, Counter> ruleExecutionCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> ruleExecutionTimers = new ConcurrentHashMap<>();
    
    // Threshold breach counters
    private final Counter executionTimeWarningCounter;
    private final Counter executionTimeCriticalCounter;
    private final Counter sessionPoolWarningCounter;
    private final Counter sessionPoolCriticalCounter;
    private final Counter successRateWarningCounter;
    private final Counter successRateCriticalCounter;
    
    /**
     * Constructor for RuleEngineMetrics.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param meterRegistry The Micrometer meter registry
     * @param alertingConfig The alerting configuration
     */
    public RuleEngineMetrics(MeterRegistry meterRegistry, AlertingConfig alertingConfig) {
        this.meterRegistry = meterRegistry;
        this.alertingConfig = alertingConfig;
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        
        logger.info("Initializing rule engine metrics with alerting thresholds: executionTimeWarning={}ms, executionTimeCritical={}ms",
                alertingConfig.getExecutionTime().getWarningMs(), alertingConfig.getExecutionTime().getCriticalMs());
        
        // Register execution counters
        totalExecutionsCounter = Counter.builder("drools.rule.executions.total")
                .description("Total number of rule executions")
                .register(meterRegistry);
        
        successfulExecutionsCounter = Counter.builder("drools.rule.executions.successful")
                .description("Number of successful rule executions")
                .register(meterRegistry);
        
        failedExecutionsCounter = Counter.builder("drools.rule.executions.failed")
                .description("Number of failed rule executions")
                .register(meterRegistry);
        
        batchExecutionsCounter = Counter.builder("drools.rule.executions.batch")
                .description("Number of batch rule executions")
                .register(meterRegistry);
        
        asyncExecutionsCounter = Counter.builder("drools.rule.executions.async")
                .description("Number of asynchronous rule executions")
                .register(meterRegistry);
        
        // Register session metrics
        sessionsCreatedCounter = Counter.builder("drools.session.created")
                .description("Number of KieSessions created")
                .register(meterRegistry);
        
        sessionsDisposedCounter = Counter.builder("drools.session.disposed")
                .description("Number of KieSessions disposed")
                .register(meterRegistry);
        
        // Register execution timers
        ruleExecutionTimer = Timer.builder("drools.rule.execution.time")
                .description("Rule execution time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
        
        batchExecutionTimer = Timer.builder("drools.rule.execution.batch.time")
                .description("Batch rule execution time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
        
        sessionCreationTimer = Timer.builder("drools.session.creation.time")
                .description("KieSession creation time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .register(meterRegistry);
        
        // Register cache metrics
        cacheHitsCounter = Counter.builder("drools.cache.hits")
                .description("Number of cache hits")
                .register(meterRegistry);
        
        cacheMissesCounter = Counter.builder("drools.cache.misses")
                .description("Number of cache misses")
                .register(meterRegistry);
        
        Gauge.builder("drools.cache.size", cacheSize::get)
                .description("Current size of the rule cache")
                .register(meterRegistry);
        
        Gauge.builder("drools.cache.evictions", cacheEvictions::get)
                .description("Number of cache evictions")
                .register(meterRegistry);
        
        Gauge.builder("drools.cache.hit.ratio", this::getCacheHitRatio)
                .description("Cache hit ratio (percentage)")
                .register(meterRegistry);
        
        // Register session pool gauge
        Gauge.builder("drools.session.pool.size", () -> 0)
                .description("Current size of the KieSession pool")
                .register(meterRegistry);
        
        // Register memory metrics
        Gauge.builder("drools.memory.heap.used", () -> memoryMXBean.getHeapMemoryUsage().getUsed())
                .description("Heap memory used (bytes)")
                .register(meterRegistry);
        
        Gauge.builder("drools.memory.heap.max", () -> memoryMXBean.getHeapMemoryUsage().getMax())
                .description("Maximum heap memory (bytes)")
                .register(meterRegistry);
        
        Gauge.builder("drools.memory.heap.usage", this::getHeapUsagePercentage)
                .description("Heap memory usage (percentage)")
                .register(meterRegistry);
        
        // Register threshold breach counters
        executionTimeWarningCounter = Counter.builder("drools.alerts.execution.time.warning")
                .description("Number of execution time warning threshold breaches")
                .register(meterRegistry);
        
        executionTimeCriticalCounter = Counter.builder("drools.alerts.execution.time.critical")
                .description("Number of execution time critical threshold breaches")
                .register(meterRegistry);
        
        sessionPoolWarningCounter = Counter.builder("drools.alerts.session.pool.warning")
                .description("Number of session pool warning threshold breaches")
                .register(meterRegistry);
        
        sessionPoolCriticalCounter = Counter.builder("drools.alerts.session.pool.critical")
                .description("Number of session pool critical threshold breaches")
                .register(meterRegistry);
        
        successRateWarningCounter = Counter.builder("drools.alerts.success.rate.warning")
                .description("Number of success rate warning threshold breaches")
                .register(meterRegistry);
        
        successRateCriticalCounter = Counter.builder("drools.alerts.success.rate.critical")
                .description("Number of success rate critical threshold breaches")
                .register(meterRegistry);
        
        logger.info("Rule engine metrics initialized with alerting enabled: {}", alertingConfig.isEnabled());
    }
    
    /**
     * Records a rule execution.
     *
     * @param ruleName The name of the rule
     * @param rulePackage The package of the rule
     * @param executionTimeMs The execution time in milliseconds
     * @param successful Whether the execution was successful
     */
    public void recordRuleExecution(String ruleName, String rulePackage, long executionTimeMs, boolean successful) {
        // Increment total executions counter
        totalExecutionsCounter.increment();
        
        // Increment success/failure counter
        if (successful) {
            successfulExecutionsCounter.increment();
        } else {
            failedExecutionsCounter.increment();
        }
        
        // Record execution time
        ruleExecutionTimer.record(executionTimeMs, TimeUnit.MILLISECONDS);
        
        // Record rule-specific metrics
        Tags tags = Tags.of(
                Tag.of("rule", ruleName),
                Tag.of("package", rulePackage),
                Tag.of("successful", String.valueOf(successful))
        );
        
        // Get or create rule-specific counter
        String counterKey = ruleName + ":" + rulePackage;
        Counter ruleCounter = ruleExecutionCounters.computeIfAbsent(counterKey, k -> 
                Counter.builder("drools.rule.executions.rule")
                        .description("Rule-specific execution count")
                        .tags(tags)
                        .register(meterRegistry)
        );
        ruleCounter.increment();
        
        // Get or create rule-specific timer
        Timer ruleTimer = ruleExecutionTimers.computeIfAbsent(counterKey, k -> 
                Timer.builder("drools.rule.execution.time.rule")
                        .description("Rule-specific execution time")
                        .tags(tags)
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .register(meterRegistry)
        );
        ruleTimer.record(executionTimeMs, TimeUnit.MILLISECONDS);
        
        // Check execution time thresholds
        checkExecutionTimeThresholds(executionTimeMs);
        
        // Periodically check success rate thresholds (every 100 executions)
        if (totalExecutionsCounter.count() % 100 == 0) {
            checkSuccessRateThresholds();
        }
        
        logger.debug("Recorded rule execution: rule={}, package={}, time={}ms, successful={}", 
                ruleName, rulePackage, executionTimeMs, successful);
    }
    
    /**
     * Records a batch rule execution.
     *
     * @param batchSize The size of the batch
     * @param executionTimeMs The execution time in milliseconds
     * @param successful Whether the execution was successful
     */
    public void recordBatchExecution(int batchSize, long executionTimeMs, boolean successful) {
        // Increment batch executions counter
        batchExecutionsCounter.increment();
        
        // Record batch execution time
        batchExecutionTimer.record(executionTimeMs, TimeUnit.MILLISECONDS);
        
        // Record per-item metrics
        if (batchSize > 0) {
            double timePerItem = (double) executionTimeMs / batchSize;
            Timer.builder("drools.rule.execution.batch.time.per.item")
                    .description("Batch rule execution time per item")
                    .tag("batchSize", String.valueOf(batchSize))
                    .tag("successful", String.valueOf(successful))
                    .register(meterRegistry)
                    .record((long) timePerItem, TimeUnit.MILLISECONDS);
        }
        
        logger.debug("Recorded batch execution: batchSize={}, time={}ms, successful={}", 
                batchSize, executionTimeMs, successful);
    }
    
    /**
     * Records an asynchronous rule execution.
     */
    public void recordAsyncExecution() {
        asyncExecutionsCounter.increment();
        logger.debug("Recorded async execution");
    }
    
    /**
     * Records a session creation.
     *
     * @param creationTimeMs The creation time in milliseconds
     */
    public void recordSessionCreation(long creationTimeMs) {
        sessionsCreatedCounter.increment();
        sessionCreationTimer.record(creationTimeMs, TimeUnit.MILLISECONDS);
        logger.debug("Recorded session creation: time={}ms", creationTimeMs);
    }
    
    /**
     * Records a session disposal.
     */
    public void recordSessionDisposal() {
        sessionsDisposedCounter.increment();
        logger.debug("Recorded session disposal");
    }
    
    /**
     * Updates the session pool size gauge.
     *
     * @param poolSize The current pool size
     * @param maxPoolSize The maximum pool size
     */
    public void updateSessionPoolSize(int poolSize, int maxPoolSize) {
        Gauge.builder("drools.session.pool.size", () -> poolSize)
                .description("Current size of the KieSession pool")
                .register(meterRegistry);
        
        // Check if pool size exceeds thresholds
        if (alertingConfig.isEnabled()) {
            if (alertingConfig.isSessionPoolCritical(poolSize, maxPoolSize)) {
                sessionPoolCriticalCounter.increment();
                logger.warn("SESSION POOL CRITICAL ALERT: Pool size {} exceeds critical threshold ({}% of max {})",
                        poolSize, alertingConfig.getSessionPool().getCriticalUtilizationPercent(), maxPoolSize);
            } else if (alertingConfig.isSessionPoolWarning(poolSize, maxPoolSize)) {
                sessionPoolWarningCounter.increment();
                logger.warn("SESSION POOL WARNING: Pool size {} exceeds warning threshold ({}% of max {})",
                        poolSize, alertingConfig.getSessionPool().getWarningUtilizationPercent(), maxPoolSize);
            }
        }
        
        logger.debug("Updated session pool size: {}/{} ({}%)", 
                poolSize, maxPoolSize, (double) poolSize / maxPoolSize * 100);
    }
    
    /**
     * Records a cache hit.
     */
    public void recordCacheHit() {
        cacheHitsCounter.increment();
    }
    
    /**
     * Records a cache miss.
     */
    public void recordCacheMiss() {
        cacheMissesCounter.increment();
    }
    
    /**
     * Updates the cache size.
     *
     * @param size The current cache size
     */
    public void updateCacheSize(long size) {
        cacheSize.set(size);
    }
    
    /**
     * Records a cache eviction.
     */
    public void recordCacheEviction() {
        cacheEvictions.incrementAndGet();
    }
    
    /**
     * Calculates the cache hit ratio.
     *
     * @return The cache hit ratio as a percentage
     */
    private double getCacheHitRatio() {
        double hits = cacheHitsCounter.count();
        double misses = cacheMissesCounter.count();
        double total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return hits / total * 100.0;
    }
    
    /**
     * Calculates the heap usage percentage.
     *
     * @return The heap usage as a percentage
     */
    private double getHeapUsagePercentage() {
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        long used = heapUsage.getUsed();
        long max = heapUsage.getMax();
        
        if (max <= 0) {
            return 0.0;
        }
        
        return (double) used / max * 100.0;
    }
    
    /**
     * Checks if the execution time exceeds thresholds and records alerts if necessary.
     *
     * @param executionTimeMs The execution time in milliseconds
     */
    private void checkExecutionTimeThresholds(long executionTimeMs) {
        if (!alertingConfig.isEnabled()) {
            return;
        }
        
        if (alertingConfig.isExecutionTimeCritical(executionTimeMs)) {
            executionTimeCriticalCounter.increment();
            logger.warn("EXECUTION TIME CRITICAL ALERT: Execution time {}ms exceeds critical threshold ({}ms)",
                    executionTimeMs, alertingConfig.getExecutionTime().getCriticalMs());
        } else if (alertingConfig.isExecutionTimeWarning(executionTimeMs)) {
            executionTimeWarningCounter.increment();
            logger.warn("EXECUTION TIME WARNING: Execution time {}ms exceeds warning threshold ({}ms)",
                    executionTimeMs, alertingConfig.getExecutionTime().getWarningMs());
        }
    }
    
    /**
     * Checks if the success rate is below thresholds and records alerts if necessary.
     */
    private void checkSuccessRateThresholds() {
        if (!alertingConfig.isEnabled()) {
            return;
        }
        
        double successful = successfulExecutionsCounter.count();
        double failed = failedExecutionsCounter.count();
        double total = successful + failed;
        
        if (total == 0) {
            return;
        }
        
        double successRate = successful / total * 100.0;
        
        if (alertingConfig.isSuccessRateCritical(successRate)) {
            successRateCriticalCounter.increment();
            logger.warn("SUCCESS RATE CRITICAL ALERT: Success rate {}% is below critical threshold ({}%)",
                    String.format("%.2f", successRate), alertingConfig.getSuccessRate().getCriticalPercent());
        } else if (alertingConfig.isSuccessRateWarning(successRate)) {
            successRateWarningCounter.increment();
            logger.warn("SUCCESS RATE WARNING: Success rate {}% is below warning threshold ({}%)",
                    String.format("%.2f", successRate), alertingConfig.getSuccessRate().getWarningPercent());
        }
    }
}