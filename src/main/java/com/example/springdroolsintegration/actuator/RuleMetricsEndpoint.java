package com.example.springdroolsintegration.actuator;

import com.example.springdroolsintegration.service.RuleManagementService;
import com.example.springdroolsintegration.service.RuleMetricsService;
import com.example.springdroolsintegration.util.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Actuator endpoint for rule metrics and operations.
 * This endpoint exposes rule metrics and allows for operations like refreshing rules.
 */
@Component
@Endpoint(id = "rules")
public class RuleMetricsEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(RuleMetricsEndpoint.class);
    
    private final RuleMetricsService ruleMetricsService;
    private final RuleManagementService ruleManagementService;
    
    /**
     * Constructor for RuleMetricsEndpoint.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleMetricsService The rule metrics service
     * @param ruleManagementService The rule management service
     */
    public RuleMetricsEndpoint(RuleMetricsService ruleMetricsService,
                              RuleManagementService ruleManagementService) {
        this.ruleMetricsService = ruleMetricsService;
        this.ruleManagementService = ruleManagementService;
        
        LoggingUtils.logInfo(logger, "RuleMetricsEndpoint initialized");
    }
    
    /**
     * Gets overall rule metrics.
     *
     * @return A map of overall rule metrics
     */
    @ReadOperation
    public Map<String, Object> metrics() {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Getting overall rule metrics");
        return ruleMetricsService.getOverallMetrics();
    }
    
    /**
     * Gets metrics for a specific rule.
     *
     * @param ruleName The name of the rule
     * @return A map of rule metrics
     */
    @ReadOperation
    public Map<String, Object> ruleMetrics(@Selector String ruleName) {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Getting metrics for rule: {}", ruleName);
        return ruleMetricsService.getRuleMetrics(ruleName);
    }
    
    /**
     * Gets metrics for a specific rule package.
     *
     * @param packageName The name of the rule package
     * @return A map of package metrics
     */
    @ReadOperation
    public Map<String, Object> packageMetrics(@Selector String packageName) {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Getting metrics for package: {}", packageName);
        return ruleMetricsService.getPackageMetrics(packageName);
    }
    
    /**
     * Gets the current status of the rule engine.
     *
     * @return A map containing rule engine status information
     */
    @ReadOperation
    public Map<String, Object> status() {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Getting rule status");
        return ruleManagementService.getRuleStatus();
    }
    
    /**
     * Reloads all rules from their sources.
     *
     * @return A map containing the reload result information
     */
    @WriteOperation
    public Map<String, Object> reload() {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Reloading rules");
        try {
            return ruleManagementService.reloadRules();
        } catch (IOException e) {
            LoggingUtils.logError(logger, "Error reloading rules", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Error reloading rules: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Resets all rule metrics.
     *
     * @return A map containing the reset result information
     */
    @DeleteOperation
    public Map<String, Object> resetMetrics() {
        LoggingUtils.logInfo(logger, "Actuator endpoint: Resetting rule metrics");
        ruleMetricsService.resetMetrics();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Rule metrics reset successfully");
        return result;
    }
}