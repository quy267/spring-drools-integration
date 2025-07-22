package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.service.impl.RuleMetricsServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleMetricsServiceImplTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private Counter.Builder counterBuilder;

    @Mock
    private Timer timer;

    @Mock
    private Timer.Builder timerBuilder;

    private RuleMetricsServiceImpl ruleMetricsService;

    @BeforeEach
    void setUp() {
        // Setup counter mock chain
        when(meterRegistry.counter(anyString(), anyList())).thenReturn(counter);
        when(meterRegistry.counter(anyString())).thenReturn(counter);
        
        // Setup timer mock chain
        when(meterRegistry.timer(anyString(), anyList())).thenReturn(timer);
        when(meterRegistry.timer(anyString())).thenReturn(timer);
        
        // Create service instance
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
        verify(meterRegistry).timer(eq("rules.execution.time"), anyList());
        verify(timer).record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    @Test
    void testRecordPackageExecutionTime() {
        // Arrange
        String packageName = "com.example.rules";
        long executionTimeMs = 200L;
        
        // Act
        ruleMetricsService.recordPackageExecutionTime(packageName, executionTimeMs);
        
        // Assert
        verify(meterRegistry).timer(eq("rules.package.execution.time"), anyList());
        verify(timer).record(executionTimeMs, TimeUnit.MILLISECONDS);
    }

    @Test
    void testRecordRuleHit() {
        // Arrange
        String ruleName = "testRule";
        
        // Act
        ruleMetricsService.recordRuleHit(ruleName);
        
        // Assert
        verify(meterRegistry).counter(eq("rules.hit.count"), anyList());
        verify(counter).increment();
    }

    @Test
    void testRecordRuleMiss() {
        // Arrange
        String ruleName = "testRule";
        
        // Act
        ruleMetricsService.recordRuleMiss(ruleName);
        
        // Assert
        verify(meterRegistry).counter(eq("rules.miss.count"), anyList());
        verify(counter).increment();
    }

    @Test
    void testRecordRuleError() {
        // Arrange
        String ruleName = "testRule";
        String errorType = "validation";
        
        // Act
        ruleMetricsService.recordRuleError(ruleName, errorType);
        
        // Assert
        verify(meterRegistry).counter(eq("rules.error.count"), anyList());
        verify(counter).increment();
    }

    @Test
    void testGetRuleMetrics() {
        // Arrange
        String ruleName = "testRule";
        
        // Setup counters to return values
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        RuleMetricsServiceImpl realService = new RuleMetricsServiceImpl(registry);
        
        // Record some metrics
        realService.recordRuleHit(ruleName);
        realService.recordRuleHit(ruleName);
        realService.recordRuleMiss(ruleName);
        realService.recordRuleError(ruleName, "validation");
        realService.recordRuleExecutionTime(ruleName, 100);
        realService.recordRuleExecutionTime(ruleName, 200);
        
        // Act
        Map<String, Object> metrics = realService.getRuleMetrics(ruleName);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(ruleName, metrics.get("ruleName"));
        assertEquals(2L, metrics.get("hitCount"));
        assertEquals(1L, metrics.get("missCount"));
        assertEquals(1L, metrics.get("errorCount"));
        assertEquals(3L, metrics.get("totalExecutions"));
        assertEquals(150.0, metrics.get("averageExecutionTimeMs"));
        assertEquals(66.67, (double) metrics.get("hitRatio"), 0.01);
    }

    @Test
    void testGetPackageMetrics() {
        // Arrange
        String packageName = "com.example.rules";
        
        // Setup counters to return values
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        RuleMetricsServiceImpl realService = new RuleMetricsServiceImpl(registry);
        
        // Record some metrics
        realService.recordPackageExecutionTime(packageName, 100);
        realService.recordPackageExecutionTime(packageName, 200);
        
        // Act
        Map<String, Object> metrics = realService.getPackageMetrics(packageName);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(packageName, metrics.get("packageName"));
        assertEquals(2L, metrics.get("executionCount"));
        assertEquals(150.0, metrics.get("averageExecutionTimeMs"));
    }

    @Test
    void testGetOverallMetrics() {
        // Arrange
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        RuleMetricsServiceImpl realService = new RuleMetricsServiceImpl(registry);
        
        // Record some metrics for different rules
        realService.recordRuleHit("rule1");
        realService.recordRuleHit("rule1");
        realService.recordRuleMiss("rule1");
        realService.recordRuleExecutionTime("rule1", 100);
        realService.recordRuleExecutionTime("rule1", 200);
        
        realService.recordRuleHit("rule2");
        realService.recordRuleMiss("rule2");
        realService.recordRuleMiss("rule2");
        realService.recordRuleError("rule2", "validation");
        realService.recordRuleExecutionTime("rule2", 300);
        
        // Act
        Map<String, Object> metrics = realService.getOverallMetrics();
        
        // Assert
        assertNotNull(metrics);
        assertEquals(3L, metrics.get("totalHits"));
        assertEquals(3L, metrics.get("totalMisses"));
        assertEquals(1L, metrics.get("totalErrors"));
        assertEquals(7L, metrics.get("totalExecutions"));
        assertEquals(200.0, metrics.get("averageExecutionTimeMs"));
        assertEquals(42.86, (double) metrics.get("overallHitRatio"), 0.01);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> ruleMetrics = (Map<String, Object>) metrics.get("ruleMetrics");
        assertNotNull(ruleMetrics);
        assertEquals(2, ruleMetrics.size());
        assertTrue(ruleMetrics.containsKey("rule1"));
        assertTrue(ruleMetrics.containsKey("rule2"));
    }

    @Test
    void testResetMetrics() {
        // Arrange
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        RuleMetricsServiceImpl realService = new RuleMetricsServiceImpl(registry);
        
        // Record some metrics
        realService.recordRuleHit("rule1");
        realService.recordRuleMiss("rule1");
        realService.recordRuleError("rule1", "validation");
        
        // Verify metrics exist
        Map<String, Object> metricsBefore = realService.getOverallMetrics();
        assertEquals(1L, metricsBefore.get("totalHits"));
        assertEquals(1L, metricsBefore.get("totalMisses"));
        assertEquals(1L, metricsBefore.get("totalErrors"));
        
        // Act
        realService.resetMetrics();
        
        // Assert
        Map<String, Object> metricsAfter = realService.getOverallMetrics();
        assertEquals(0L, metricsAfter.get("totalHits"));
        assertEquals(0L, metricsAfter.get("totalMisses"));
        assertEquals(0L, metricsAfter.get("totalErrors"));
    }
}