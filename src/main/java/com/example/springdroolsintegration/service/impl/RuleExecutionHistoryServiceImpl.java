package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.model.entity.RuleExecution;
import com.example.springdroolsintegration.repository.RuleExecutionRepository;
import com.example.springdroolsintegration.service.RuleExecutionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the RuleExecutionHistoryService interface.
 * This service demonstrates database connection pooling.
 */
@Service
public class RuleExecutionHistoryServiceImpl implements RuleExecutionHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(RuleExecutionHistoryServiceImpl.class);
    
    private final RuleExecutionRepository ruleExecutionRepository;

    /**
     * Constructor for RuleExecutionHistoryServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleExecutionRepository The repository for rule executions
     */
    public RuleExecutionHistoryServiceImpl(RuleExecutionRepository ruleExecutionRepository) {
        this.ruleExecutionRepository = ruleExecutionRepository;
        logger.info("RuleExecutionHistoryService initialized");
    }

    @Override
    @Transactional
    public RuleExecution recordRuleExecution(
            String ruleName,
            String rulePackage,
            String factType,
            String executionResult,
            Long executionTimeMs,
            String correlationId,
            Boolean successful,
            String errorMessage) {
        
        logger.debug("Recording rule execution: ruleName={}, factType={}, executionTime={}ms", 
                ruleName, factType, executionTimeMs);
        
        RuleExecution ruleExecution = RuleExecution.builder()
                .ruleName(ruleName)
                .rulePackage(rulePackage)
                .factType(factType)
                .executionResult(executionResult)
                .executionTimeMs(executionTimeMs)
                .executionDate(new Date())
                .correlationId(correlationId)
                .successful(successful)
                .errorMessage(errorMessage)
                .build();
        
        return ruleExecutionRepository.save(ruleExecution);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecution> getRuleExecutionsByName(String ruleName) {
        logger.debug("Getting rule executions by name: {}", ruleName);
        return ruleExecutionRepository.findByRuleName(ruleName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecution> getRuleExecutionsByFactType(String factType) {
        logger.debug("Getting rule executions by fact type: {}", factType);
        return ruleExecutionRepository.findByFactType(factType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecution> getRuleExecutionsByCorrelationId(String correlationId) {
        logger.debug("Getting rule executions by correlation ID: {}", correlationId);
        return ruleExecutionRepository.findByCorrelationId(correlationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecution> getRuleExecutionsInDateRange(Date startDate, Date endDate) {
        logger.debug("Getting rule executions in date range: {} to {}", startDate, endDate);
        return ruleExecutionRepository.findByExecutionDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RuleExecution> getSlowRuleExecutions(Long executionTimeMs) {
        logger.debug("Getting slow rule executions (> {}ms)", executionTimeMs);
        return ruleExecutionRepository.findByExecutionTimeMsGreaterThan(executionTimeMs);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageExecutionTimeForRule(String ruleName) {
        logger.debug("Getting average execution time for rule: {}", ruleName);
        return ruleExecutionRepository.findAverageExecutionTimeByRuleName(ruleName);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getMostFrequentlyExecutedRules(int limit) {
        logger.debug("Getting most frequently executed rules (limit: {})", limit);
        
        List<Object[]> results = ruleExecutionRepository.findMostFrequentlyExecutedRules(limit);
        
        Map<String, Long> resultMap = new HashMap<>();
        for (Object[] result : results) {
            String ruleName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            resultMap.put(ruleName, count);
        }
        
        return resultMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExecutionStatistics() {
        logger.debug("Getting execution statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Total executions
        long totalExecutions = ruleExecutionRepository.count();
        statistics.put("totalExecutions", totalExecutions);
        
        // Successful executions
        long successfulExecutions = ruleExecutionRepository.findBySuccessful(true).size();
        statistics.put("successfulExecutions", successfulExecutions);
        
        // Failed executions
        long failedExecutions = ruleExecutionRepository.findBySuccessful(false).size();
        statistics.put("failedExecutions", failedExecutions);
        
        // Success rate
        double successRate = totalExecutions > 0 ? (double) successfulExecutions / totalExecutions * 100 : 0;
        statistics.put("successRate", successRate);
        
        // Average execution time (all rules)
        List<RuleExecution> allExecutions = ruleExecutionRepository.findAll();
        double avgExecutionTime = allExecutions.stream()
                .mapToLong(RuleExecution::getExecutionTimeMs)
                .average()
                .orElse(0);
        statistics.put("averageExecutionTimeMs", avgExecutionTime);
        
        // Most frequently executed rules (top 5)
        Map<String, Long> topRules = getMostFrequentlyExecutedRules(5);
        statistics.put("topRules", topRules);
        
        return statistics;
    }
}