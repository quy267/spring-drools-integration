package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.entity.RuleExecution;
import com.example.springdroolsintegration.repository.RuleExecutionRepository;
import com.example.springdroolsintegration.service.impl.RuleExecutionHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleExecutionHistoryServiceImplTest {

    @Mock
    private RuleExecutionRepository ruleExecutionRepository;

    private RuleExecutionHistoryServiceImpl ruleExecutionHistoryService;

    @BeforeEach
    void setUp() {
        ruleExecutionHistoryService = new RuleExecutionHistoryServiceImpl(ruleExecutionRepository);
    }

    @Test
    void testRecordRuleExecution() {
        // Arrange
        String ruleName = "testRule";
        String rulePackage = "com.example.rules";
        String factType = "Customer";
        String executionResult = "Discount applied";
        Long executionTimeMs = 100L;
        String correlationId = "test-correlation-id";
        Boolean successful = true;
        String errorMessage = null;

        RuleExecution savedExecution = RuleExecution.builder()
                .id(1L)
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

        when(ruleExecutionRepository.save(any(RuleExecution.class))).thenReturn(savedExecution);

        // Act
        RuleExecution result = ruleExecutionHistoryService.recordRuleExecution(
                ruleName, rulePackage, factType, executionResult, executionTimeMs, correlationId, successful, errorMessage);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(ruleName, result.getRuleName());
        assertEquals(rulePackage, result.getRulePackage());
        assertEquals(factType, result.getFactType());
        assertEquals(executionResult, result.getExecutionResult());
        assertEquals(executionTimeMs, result.getExecutionTimeMs());
        assertEquals(correlationId, result.getCorrelationId());
        assertEquals(successful, result.getSuccessful());
        assertEquals(errorMessage, result.getErrorMessage());

        // Verify that save was called with the correct parameters
        ArgumentCaptor<RuleExecution> executionCaptor = ArgumentCaptor.forClass(RuleExecution.class);
        verify(ruleExecutionRepository).save(executionCaptor.capture());
        RuleExecution capturedExecution = executionCaptor.getValue();
        assertEquals(ruleName, capturedExecution.getRuleName());
        assertEquals(rulePackage, capturedExecution.getRulePackage());
        assertEquals(factType, capturedExecution.getFactType());
        assertEquals(executionResult, capturedExecution.getExecutionResult());
        assertEquals(executionTimeMs, capturedExecution.getExecutionTimeMs());
        assertEquals(correlationId, capturedExecution.getCorrelationId());
        assertEquals(successful, capturedExecution.getSuccessful());
        assertEquals(errorMessage, capturedExecution.getErrorMessage());
        assertNotNull(capturedExecution.getExecutionDate());
    }

    @Test
    void testGetRuleExecutionsByName() {
        // Arrange
        String ruleName = "testRule";
        List<RuleExecution> expectedExecutions = Arrays.asList(
                RuleExecution.builder().id(1L).ruleName(ruleName).build(),
                RuleExecution.builder().id(2L).ruleName(ruleName).build()
        );
        when(ruleExecutionRepository.findByRuleName(ruleName)).thenReturn(expectedExecutions);

        // Act
        List<RuleExecution> result = ruleExecutionHistoryService.getRuleExecutionsByName(ruleName);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ruleExecutionRepository).findByRuleName(ruleName);
    }

    @Test
    void testGetRuleExecutionsByFactType() {
        // Arrange
        String factType = "Customer";
        List<RuleExecution> expectedExecutions = Arrays.asList(
                RuleExecution.builder().id(1L).factType(factType).build(),
                RuleExecution.builder().id(2L).factType(factType).build()
        );
        when(ruleExecutionRepository.findByFactType(factType)).thenReturn(expectedExecutions);

        // Act
        List<RuleExecution> result = ruleExecutionHistoryService.getRuleExecutionsByFactType(factType);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ruleExecutionRepository).findByFactType(factType);
    }

    @Test
    void testGetRuleExecutionsByCorrelationId() {
        // Arrange
        String correlationId = "test-correlation-id";
        List<RuleExecution> expectedExecutions = Arrays.asList(
                RuleExecution.builder().id(1L).correlationId(correlationId).build(),
                RuleExecution.builder().id(2L).correlationId(correlationId).build()
        );
        when(ruleExecutionRepository.findByCorrelationId(correlationId)).thenReturn(expectedExecutions);

        // Act
        List<RuleExecution> result = ruleExecutionHistoryService.getRuleExecutionsByCorrelationId(correlationId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ruleExecutionRepository).findByCorrelationId(correlationId);
    }

    @Test
    void testGetRuleExecutionsInDateRange() {
        // Arrange
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Date endDate = new Date();
        List<RuleExecution> expectedExecutions = Arrays.asList(
                RuleExecution.builder().id(1L).executionDate(new Date()).build(),
                RuleExecution.builder().id(2L).executionDate(new Date()).build()
        );
        when(ruleExecutionRepository.findByExecutionDateBetween(startDate, endDate)).thenReturn(expectedExecutions);

        // Act
        List<RuleExecution> result = ruleExecutionHistoryService.getRuleExecutionsInDateRange(startDate, endDate);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ruleExecutionRepository).findByExecutionDateBetween(startDate, endDate);
    }

    @Test
    void testGetSlowRuleExecutions() {
        // Arrange
        Long executionTimeMs = 500L;
        List<RuleExecution> expectedExecutions = Arrays.asList(
                RuleExecution.builder().id(1L).executionTimeMs(600L).build(),
                RuleExecution.builder().id(2L).executionTimeMs(700L).build()
        );
        when(ruleExecutionRepository.findByExecutionTimeMsGreaterThan(executionTimeMs)).thenReturn(expectedExecutions);

        // Act
        List<RuleExecution> result = ruleExecutionHistoryService.getSlowRuleExecutions(executionTimeMs);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ruleExecutionRepository).findByExecutionTimeMsGreaterThan(executionTimeMs);
    }

    @Test
    void testGetAverageExecutionTimeForRule() {
        // Arrange
        String ruleName = "testRule";
        Double expectedAverage = 150.5;
        when(ruleExecutionRepository.findAverageExecutionTimeByRuleName(ruleName)).thenReturn(expectedAverage);

        // Act
        Double result = ruleExecutionHistoryService.getAverageExecutionTimeForRule(ruleName);

        // Assert
        assertEquals(expectedAverage, result);
        verify(ruleExecutionRepository).findAverageExecutionTimeByRuleName(ruleName);
    }

    @Test
    void testGetMostFrequentlyExecutedRules() {
        // Arrange
        int limit = 2;
        List<Object[]> repositoryResult = Arrays.asList(
                new Object[]{"rule1", 10L},
                new Object[]{"rule2", 5L}
        );
        when(ruleExecutionRepository.findMostFrequentlyExecutedRules(limit)).thenReturn(repositoryResult);

        // Act
        Map<String, Long> result = ruleExecutionHistoryService.getMostFrequentlyExecutedRules(limit);

        // Assert
        assertEquals(2, result.size());
        assertEquals(10L, result.get("rule1"));
        assertEquals(5L, result.get("rule2"));
        verify(ruleExecutionRepository).findMostFrequentlyExecutedRules(limit);
    }

    @Test
    void testGetExecutionStatistics() {
        // Arrange
        long totalExecutions = 3; // Updated to match actual data size
        List<RuleExecution> successfulExecutions = Arrays.asList(
                RuleExecution.builder().build(),
                RuleExecution.builder().build()
        );
        List<RuleExecution> failedExecutions = Arrays.asList(
                RuleExecution.builder().build()
        );
        List<RuleExecution> allExecutions = Arrays.asList(
                RuleExecution.builder().executionTimeMs(100L).build(),
                RuleExecution.builder().executionTimeMs(200L).build(),
                RuleExecution.builder().executionTimeMs(300L).build()
        );
        Map<String, Long> topRules = Map.of(
                "rule1", 10L,
                "rule2", 5L
        );

        when(ruleExecutionRepository.count()).thenReturn(totalExecutions);
        when(ruleExecutionRepository.findBySuccessful(true)).thenReturn(successfulExecutions);
        when(ruleExecutionRepository.findBySuccessful(false)).thenReturn(failedExecutions);
        when(ruleExecutionRepository.findAll()).thenReturn(allExecutions);
        when(ruleExecutionRepository.findMostFrequentlyExecutedRules(5)).thenReturn(Arrays.asList(
                new Object[]{"rule1", 10L},
                new Object[]{"rule2", 5L}
        ));

        // Act
        Map<String, Object> result = ruleExecutionHistoryService.getExecutionStatistics();

        // Assert
        assertEquals(totalExecutions, result.get("totalExecutions"));
        assertEquals(2L, result.get("successfulExecutions"));
        assertEquals(1L, result.get("failedExecutions"));
        assertEquals(66.66666666666666, result.get("successRate")); // 2/3 * 100 = 66.67%
        assertEquals(200.0, result.get("averageExecutionTimeMs"));
        
        @SuppressWarnings("unchecked")
        Map<String, Long> resultTopRules = (Map<String, Long>) result.get("topRules");
        assertEquals(2, resultTopRules.size());
        assertEquals(10L, resultTopRules.get("rule1"));
        assertEquals(5L, resultTopRules.get("rule2"));

        verify(ruleExecutionRepository).count();
        verify(ruleExecutionRepository).findBySuccessful(true);
        verify(ruleExecutionRepository).findBySuccessful(false);
        verify(ruleExecutionRepository).findAll();
        verify(ruleExecutionRepository).findMostFrequentlyExecutedRules(5);
    }
}