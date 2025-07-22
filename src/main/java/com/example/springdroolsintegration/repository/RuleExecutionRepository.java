package com.example.springdroolsintegration.repository;

import com.example.springdroolsintegration.model.entity.RuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Repository for RuleExecution entities.
 * This repository demonstrates database connection pooling.
 */
@Repository
public interface RuleExecutionRepository extends JpaRepository<RuleExecution, Long> {

    /**
     * Find rule executions by rule name.
     *
     * @param ruleName The name of the rule
     * @return List of rule executions
     */
    List<RuleExecution> findByRuleName(String ruleName);

    /**
     * Find rule executions by rule package.
     *
     * @param rulePackage The package of the rule
     * @return List of rule executions
     */
    List<RuleExecution> findByRulePackage(String rulePackage);

    /**
     * Find rule executions by fact type.
     *
     * @param factType The type of fact
     * @return List of rule executions
     */
    List<RuleExecution> findByFactType(String factType);

    /**
     * Find rule executions by correlation ID.
     *
     * @param correlationId The correlation ID
     * @return List of rule executions
     */
    List<RuleExecution> findByCorrelationId(String correlationId);

    /**
     * Find rule executions by success status.
     *
     * @param successful Whether the execution was successful
     * @return List of rule executions
     */
    List<RuleExecution> findBySuccessful(Boolean successful);

    /**
     * Find rule executions within a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return List of rule executions
     */
    @Query("SELECT re FROM RuleExecution re WHERE re.executionDate BETWEEN :startDate AND :endDate")
    List<RuleExecution> findByExecutionDateBetween(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * Find rule executions with execution time greater than the specified value.
     *
     * @param executionTimeMs The execution time threshold in milliseconds
     * @return List of rule executions
     */
    List<RuleExecution> findByExecutionTimeMsGreaterThan(Long executionTimeMs);

    /**
     * Find the average execution time for a specific rule.
     *
     * @param ruleName The name of the rule
     * @return The average execution time in milliseconds
     */
    @Query("SELECT AVG(re.executionTimeMs) FROM RuleExecution re WHERE re.ruleName = :ruleName")
    Double findAverageExecutionTimeByRuleName(@Param("ruleName") String ruleName);

    /**
     * Find the most frequently executed rules.
     *
     * @param limit The maximum number of results to return
     * @return List of rule names and their execution counts
     */
    @Query(value = "SELECT rule_name, COUNT(*) as count FROM rule_executions GROUP BY rule_name ORDER BY count DESC LIMIT :limit", 
           nativeQuery = true)
    List<Object[]> findMostFrequentlyExecutedRules(@Param("limit") int limit);
}