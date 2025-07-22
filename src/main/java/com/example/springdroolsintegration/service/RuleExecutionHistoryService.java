package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.entity.RuleExecution;
import com.example.springdroolsintegration.repository.RuleExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Service for managing rule execution history.
 * This service demonstrates database connection pooling.
 */
public interface RuleExecutionHistoryService {

    /**
     * Records a rule execution in the database.
     *
     * @param ruleName The name of the rule
     * @param rulePackage The package of the rule
     * @param factType The type of fact
     * @param executionResult The result of the execution
     * @param executionTimeMs The execution time in milliseconds
     * @param correlationId The correlation ID
     * @param successful Whether the execution was successful
     * @param errorMessage The error message (if any)
     * @return The saved RuleExecution entity
     */
    RuleExecution recordRuleExecution(
            String ruleName,
            String rulePackage,
            String factType,
            String executionResult,
            Long executionTimeMs,
            String correlationId,
            Boolean successful,
            String errorMessage);

    /**
     * Gets rule executions by rule name.
     *
     * @param ruleName The name of the rule
     * @return List of rule executions
     */
    List<RuleExecution> getRuleExecutionsByName(String ruleName);

    /**
     * Gets rule executions by fact type.
     *
     * @param factType The type of fact
     * @return List of rule executions
     */
    List<RuleExecution> getRuleExecutionsByFactType(String factType);

    /**
     * Gets rule executions by correlation ID.
     *
     * @param correlationId The correlation ID
     * @return List of rule executions
     */
    List<RuleExecution> getRuleExecutionsByCorrelationId(String correlationId);

    /**
     * Gets rule executions within a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of rule executions
     */
    List<RuleExecution> getRuleExecutionsInDateRange(Date startDate, Date endDate);

    /**
     * Gets rule executions with execution time greater than the specified value.
     *
     * @param executionTimeMs The execution time threshold in milliseconds
     * @return List of rule executions
     */
    List<RuleExecution> getSlowRuleExecutions(Long executionTimeMs);

    /**
     * Gets the average execution time for a specific rule.
     *
     * @param ruleName The name of the rule
     * @return The average execution time in milliseconds
     */
    Double getAverageExecutionTimeForRule(String ruleName);

    /**
     * Gets the most frequently executed rules.
     *
     * @param limit The maximum number of results to return
     * @return Map of rule names to execution counts
     */
    Map<String, Long> getMostFrequentlyExecutedRules(int limit);

    /**
     * Gets execution statistics.
     *
     * @return Map of statistic names to values
     */
    Map<String, Object> getExecutionStatistics();
}