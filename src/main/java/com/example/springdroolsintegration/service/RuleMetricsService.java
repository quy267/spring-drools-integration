package com.example.springdroolsintegration.service;

import java.util.Map;

/**
 * Service interface for rule execution metrics.
 * This service handles recording and retrieving metrics for rule execution.
 */
public interface RuleMetricsService {

    /**
     * Records the execution time of a rule.
     *
     * @param ruleName The name of the rule
     * @param executionTimeMs The execution time in milliseconds
     */
    void recordRuleExecutionTime(String ruleName, long executionTimeMs);
    
    /**
     * Records the execution time of a rule package.
     *
     * @param packageName The name of the rule package
     * @param executionTimeMs The execution time in milliseconds
     */
    void recordPackageExecutionTime(String packageName, long executionTimeMs);
    
    /**
     * Records a rule hit.
     *
     * @param ruleName The name of the rule
     */
    void recordRuleHit(String ruleName);
    
    /**
     * Records a rule miss.
     *
     * @param ruleName The name of the rule
     */
    void recordRuleMiss(String ruleName);
    
    /**
     * Records a rule execution error.
     *
     * @param ruleName The name of the rule
     * @param errorType The type of error
     */
    void recordRuleError(String ruleName, String errorType);
    
    /**
     * Gets metrics for a specific rule.
     *
     * @param ruleName The name of the rule
     * @return A map of metric names to values
     */
    Map<String, Object> getRuleMetrics(String ruleName);
    
    /**
     * Gets metrics for a specific rule package.
     *
     * @param packageName The name of the rule package
     * @return A map of metric names to values
     */
    Map<String, Object> getPackageMetrics(String packageName);
    
    /**
     * Gets overall rule execution metrics.
     *
     * @return A map of metric names to values
     */
    Map<String, Object> getOverallMetrics();
    
    /**
     * Resets all metrics.
     */
    void resetMetrics();
}