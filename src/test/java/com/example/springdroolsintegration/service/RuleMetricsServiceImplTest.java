package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.service.impl.RuleMetricsServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RuleMetricsServiceImplTest {

    private MeterRegistry meterRegistry;
    private RuleMetricsServiceImpl ruleMetricsService;

    @BeforeEach
    void setUp() {
        // Use SimpleMeterRegistry for testing - no mocking needed
        meterRegistry = new SimpleMeterRegistry();
        ruleMetricsService = new RuleMetricsServiceImpl(meterRegistry);
    }

    @Test
    void testRecordRuleExecutionTime() {
        // Arrange
        String ruleName = "testRule";
        long executionTimeMs = 100L;
        
        // Act
        ruleMetricsService.recordRuleExecutionTime(ruleName, executionTimeMs);
        
        // Assert
        Timer timer = meterRegistry.find("drools.rule.execution.time").tag("rule", ruleName).timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= executionTimeMs);
    }

    @Test
    void testRecordPackageExecutionTime() {
        // Arrange
        String packageName = "com.example.rules";
        long executionTimeMs = 200L;
        
        // Act
        ruleMetricsService.recordPackageExecutionTime(packageName, executionTimeMs);
        
        // Assert
        Timer timer = meterRegistry.find("drools.package.execution.time").tag("package", packageName).timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= executionTimeMs);
    }

    @Test
    void testRecordRuleHit() {
        // Arrange
        String ruleName = "testRule";
        
        // Act
        ruleMetricsService.recordRuleHit(ruleName);
        
        // Assert
        Counter counter = meterRegistry.find("drools.rule.hits").tag("rule", ruleName).counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void testRecordRuleMiss() {
        // Arrange
        String ruleName = "testRule";
        
        // Act
        ruleMetricsService.recordRuleMiss(ruleName);
        
        // Assert
        Counter counter = meterRegistry.find("drools.rule.misses").tag("rule", ruleName).counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void testRecordRuleError() {
        // Arrange
        String ruleName = "testRule";
        String errorType = "validation";
        
        // Act
        ruleMetricsService.recordRuleError(ruleName, errorType);
        
        // Assert
        Counter counter = meterRegistry.find("drools.rule.errors")
                .tag("rule", ruleName)
                .tag("errorType", errorType)
                .counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void testGetRuleMetrics() {
        // Arrange
        String ruleName = "testRule";
        
        // Record some metrics
        ruleMetricsService.recordRuleHit(ruleName);
        ruleMetricsService.recordRuleHit(ruleName);
        ruleMetricsService.recordRuleMiss(ruleName);
        ruleMetricsService.recordRuleError(ruleName, "validation");
        ruleMetricsService.recordRuleExecutionTime(ruleName, 100);
        ruleMetricsService.recordRuleExecutionTime(ruleName, 200);
        
        // Act
        Map<String, Object> metrics = ruleMetricsService.getRuleMetrics(ruleName);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(2L, metrics.get("hitCount"));
        assertEquals(1L, metrics.get("missCount"));
        assertEquals(1L, metrics.get("errorCount"));
        assertEquals(2L, metrics.get("totalExecutions")); // Only execution time calls increment this
        assertEquals(100.0, (double) metrics.get("hitRate"), 0.01); // 2 hits out of 2 executions
        assertEquals(50.0, (double) metrics.get("errorRate"), 0.01); // 1 error out of 2 executions
        assertEquals(2L, metrics.get("executionCount"));
        assertTrue((double) metrics.get("meanExecutionTimeMs") > 0);
    }

    @Test
    void testGetPackageMetrics() {
        // Arrange
        String packageName = "com.example.rules";
        
        // Record some metrics
        ruleMetricsService.recordPackageExecutionTime(packageName, 100);
        ruleMetricsService.recordPackageExecutionTime(packageName, 200);
        
        // Act
        Map<String, Object> metrics = ruleMetricsService.getPackageMetrics(packageName);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(2L, metrics.get("executionCount"));
        assertTrue((double) metrics.get("meanExecutionTimeMs") > 0);
        assertTrue((double) metrics.get("totalExecutionTimeMs") > 0);
    }

    @Test
    void testGetOverallMetrics() {
        // Arrange
        // Record some metrics for different rules
        ruleMetricsService.recordRuleHit("rule1");
        ruleMetricsService.recordRuleHit("rule1");
        ruleMetricsService.recordRuleMiss("rule1");
        ruleMetricsService.recordRuleExecutionTime("rule1", 100);
        ruleMetricsService.recordRuleExecutionTime("rule1", 200);
        
        ruleMetricsService.recordRuleHit("rule2");
        ruleMetricsService.recordRuleMiss("rule2");
        ruleMetricsService.recordRuleMiss("rule2");
        ruleMetricsService.recordRuleError("rule2", "validation");
        ruleMetricsService.recordRuleExecutionTime("rule2", 300);
        
        // Act
        Map<String, Object> metrics = ruleMetricsService.getOverallMetrics();
        
        // Assert
        assertNotNull(metrics);
        assertEquals(3L, metrics.get("totalHitCount"));
        assertEquals(3L, metrics.get("totalMissCount"));
        assertEquals(1L, metrics.get("totalErrorCount"));
        assertEquals(3L, metrics.get("totalExecutions")); // Only execution time calls count: 2 for rule1 + 1 for rule2
        assertTrue((double) metrics.get("overallHitRate") > 0);
        assertTrue((double) metrics.get("overallErrorRate") > 0);
    }

    @Test
    void testResetMetrics() {
        // Arrange
        // Record some metrics
        ruleMetricsService.recordRuleHit("rule1");
        ruleMetricsService.recordRuleMiss("rule1");
        ruleMetricsService.recordRuleError("rule1", "validation");
        
        // Verify metrics exist
        Map<String, Object> metricsBefore = ruleMetricsService.getOverallMetrics();
        assertEquals(1L, metricsBefore.get("totalHitCount"));
        assertEquals(1L, metricsBefore.get("totalMissCount"));
        assertEquals(1L, metricsBefore.get("totalErrorCount"));
        
        // Act
        ruleMetricsService.resetMetrics();
        
        // Assert
        Map<String, Object> metricsAfter = ruleMetricsService.getOverallMetrics();
        assertEquals(0L, metricsAfter.get("totalHitCount"));
        assertEquals(0L, metricsAfter.get("totalMissCount"));
        assertEquals(0L, metricsAfter.get("totalErrorCount"));
    }
}