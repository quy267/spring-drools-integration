package com.example.springdroolsintegration.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Custom metrics for the rule engine.
 * This component registers and manages Micrometer metrics for rule execution.
 */
@Component
public class RuleEngineMetrics {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineMetrics.class);
    
    private final MeterRegistry meterRegistry;
    
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
    
    // Rule-specific metrics
    private final Map<String, Counter> ruleExecutionCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> ruleExecutionTimers = new ConcurrentHashMap<>();
    
    /**
     * Constructor for RuleEngineMetrics.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param meterRegistry The Micrometer meter registry
     */
    public RuleEngineMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        logger.info("Initializing rule engine metrics");
        
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
        
        // Register session pool gauge
        Gauge.builder("drools.session.pool.size", () -> 0)
                .description("Current size of the KieSession pool")
                .register(meterRegistry);
        
        logger.info("Rule engine metrics initialized");
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
     */
    public void updateSessionPoolSize(int poolSize) {
        Gauge.builder("drools.session.pool.size", () -> poolSize)
                .description("Current size of the KieSession pool")
                .register(meterRegistry);
        logger.debug("Updated session pool size: {}", poolSize);
    }
}