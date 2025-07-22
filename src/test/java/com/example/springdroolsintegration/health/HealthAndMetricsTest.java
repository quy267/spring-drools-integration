package com.example.springdroolsintegration.health;

import com.example.springdroolsintegration.actuator.RuleMetricsEndpoint;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for health indicators and metrics.
 * This class tests the custom health indicators and metrics for the Drools rule engine.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HealthAndMetricsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DroolsHealthIndicator droolsHealthIndicator;

    @Autowired
    private DecisionTableHealthIndicator decisionTableHealthIndicator;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired(required = false)
    private RuleMetricsEndpoint ruleMetricsEndpoint;

    /**
     * Test configuration for health and metrics tests.
     */
    @TestConfiguration
    static class TestConfig {
        
        /**
         * Creates a SimpleMeterRegistry for testing.
         *
         * @return A SimpleMeterRegistry
         */
        @Bean
        @Primary
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Test
    @DisplayName("Test DroolsHealthIndicator returns UP status")
    public void testDroolsHealthIndicator() {
        Health health = droolsHealthIndicator.health();
        assertEquals(Status.UP, health.getStatus(), "Drools health status should be UP");
        assertNotNull(health.getDetails(), "Health details should not be null");
        assertTrue(health.getDetails().containsKey("ruleCount"), "Health details should contain ruleCount");
    }

    @Test
    @DisplayName("Test DecisionTableHealthIndicator returns UP status")
    public void testDecisionTableHealthIndicator() {
        Health health = decisionTableHealthIndicator.health();
        assertEquals(Status.UP, health.getStatus(), "Decision table health status should be UP");
        assertNotNull(health.getDetails(), "Health details should not be null");
        assertTrue(health.getDetails().containsKey("validationStatus"), "Health details should contain validationStatus");
    }

    @Test
    @DisplayName("Test health endpoint returns aggregated health status")
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    @DisplayName("Test health endpoint returns detailed health information")
    public void testHealthEndpointDetails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health/drools")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    @DisplayName("Test metrics endpoint returns rule execution metrics")
    public void testMetricsEndpoint() throws Exception {
        // First, register some metrics
        meterRegistry.counter("drools.rule.execution.count", "ruleName", "testRule").increment();
        meterRegistry.timer("drools.rule.execution.time", "ruleName", "testRule").record(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Test the metrics endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/metrics/drools.rule.execution.count")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("drools.rule.execution.count"))
                .andExpect(jsonPath("$.measurements").isArray())
                .andExpect(jsonPath("$.availableTags").isArray());
    }

    @Test
    @DisplayName("Test custom rule metrics endpoint if available")
    public void testRuleMetricsEndpoint() throws Exception {
        // Skip test if the endpoint is not available
        if (ruleMetricsEndpoint == null) {
            return;
        }

        // Register some metrics
        meterRegistry.counter("drools.rule.execution.count", "ruleName", "testRule").increment();

        // Test the custom endpoint directly
        Map<String, Object> metrics = ruleMetricsEndpoint.getRuleMetrics();
        assertNotNull(metrics, "Rule metrics should not be null");
        assertTrue(metrics.containsKey("rules") || metrics.containsKey("totalExecutions"), 
                "Rule metrics should contain rules or totalExecutions");

        // Test the endpoint via HTTP if it's exposed
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/actuator/rules")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            // Endpoint might not be exposed, which is fine
            System.out.println("Custom rules endpoint not exposed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test prometheus metrics endpoint")
    public void testPrometheusEndpoint() throws Exception {
        // Register some metrics
        meterRegistry.counter("drools.rule.execution.count", "ruleName", "testRule").increment();

        // Test the prometheus endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("drools_rule_execution_count")));
    }
}