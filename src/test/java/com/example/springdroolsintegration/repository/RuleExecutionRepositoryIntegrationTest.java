package com.example.springdroolsintegration.repository;

import com.example.springdroolsintegration.config.TestContainersConfig;
import com.example.springdroolsintegration.model.entity.RuleExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RuleExecutionRepository using TestContainers.
 * These tests verify that the repository methods work correctly with a real PostgreSQL database.
 */
@DataJpaTest
@Testcontainers
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RuleExecutionRepositoryIntegrationTest {

    @Autowired
    private RuleExecutionRepository ruleExecutionRepository;

    private RuleExecution ruleExecution1;
    private RuleExecution ruleExecution2;
    private RuleExecution ruleExecution3;

    @BeforeEach
    void setUp() {
        // Clear the repository before each test
        ruleExecutionRepository.deleteAll();

        // Create test data
        ruleExecution1 = RuleExecution.builder()
                .ruleName("DiscountRule")
                .rulePackage("com.example.discount")
                .factType("Customer")
                .executionResult("10% discount applied")
                .executionTimeMs(100L)
                .executionDate(new Date())
                .correlationId("corr-123")
                .successful(true)
                .errorMessage(null)
                .build();

        ruleExecution2 = RuleExecution.builder()
                .ruleName("LoanRule")
                .rulePackage("com.example.loan")
                .factType("LoanApplication")
                .executionResult("Loan approved")
                .executionTimeMs(150L)
                .executionDate(new Date())
                .correlationId("corr-456")
                .successful(true)
                .errorMessage(null)
                .build();

        ruleExecution3 = RuleExecution.builder()
                .ruleName("DiscountRule")
                .rulePackage("com.example.discount")
                .factType("Order")
                .executionResult("Error applying discount")
                .executionTimeMs(200L)
                .executionDate(new Date())
                .correlationId("corr-789")
                .successful(false)
                .errorMessage("Invalid discount percentage")
                .build();

        // Save test data
        ruleExecutionRepository.saveAll(Arrays.asList(ruleExecution1, ruleExecution2, ruleExecution3));
    }

    @Test
    @DisplayName("Test findByRuleName")
    void testFindByRuleName() {
        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByRuleName("DiscountRule");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(re -> re.getRuleName().equals("DiscountRule")));
    }

    @Test
    @DisplayName("Test findByRulePackage")
    void testFindByRulePackage() {
        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByRulePackage("com.example.loan");

        // Assert
        assertEquals(1, result.size());
        assertEquals("LoanRule", result.get(0).getRuleName());
    }

    @Test
    @DisplayName("Test findByFactType")
    void testFindByFactType() {
        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByFactType("Customer");

        // Assert
        assertEquals(1, result.size());
        assertEquals("DiscountRule", result.get(0).getRuleName());
    }

    @Test
    @DisplayName("Test findByCorrelationId")
    void testFindByCorrelationId() {
        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByCorrelationId("corr-456");

        // Assert
        assertEquals(1, result.size());
        assertEquals("LoanRule", result.get(0).getRuleName());
    }

    @Test
    @DisplayName("Test findBySuccessful")
    void testFindBySuccessful() {
        // Act
        List<RuleExecution> successfulResults = ruleExecutionRepository.findBySuccessful(true);
        List<RuleExecution> failedResults = ruleExecutionRepository.findBySuccessful(false);

        // Assert
        assertEquals(2, successfulResults.size());
        assertEquals(1, failedResults.size());
        assertTrue(successfulResults.stream().allMatch(RuleExecution::getSuccessful));
        assertFalse(failedResults.get(0).getSuccessful());
    }

    @Test
    @DisplayName("Test findByExecutionDateBetween")
    void testFindByExecutionDateBetween() {
        // Arrange
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        Date endDate = calendar.getTime();

        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByExecutionDateBetween(startDate, endDate);

        // Assert
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test findByExecutionTimeMsGreaterThan")
    void testFindByExecutionTimeMsGreaterThan() {
        // Act
        List<RuleExecution> result = ruleExecutionRepository.findByExecutionTimeMsGreaterThan(120L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(re -> re.getExecutionTimeMs() > 120L));
    }

    @Test
    @DisplayName("Test findAverageExecutionTimeByRuleName")
    void testFindAverageExecutionTimeByRuleName() {
        // Act
        Double avgTime = ruleExecutionRepository.findAverageExecutionTimeByRuleName("DiscountRule");

        // Assert
        assertNotNull(avgTime);
        assertEquals(150.0, avgTime, 0.01); // Average of 100ms and 200ms
    }

    @Test
    @DisplayName("Test findMostFrequentlyExecutedRules")
    void testFindMostFrequentlyExecutedRules() {
        // Act
        List<Object[]> result = ruleExecutionRepository.findMostFrequentlyExecutedRules(10);

        // Assert
        assertEquals(2, result.size());
        
        // First result should be DiscountRule with count 2
        assertEquals("DiscountRule", result.get(0)[0]);
        assertEquals(2L, ((Number) result.get(0)[1]).longValue());
        
        // Second result should be LoanRule with count 1
        assertEquals("LoanRule", result.get(1)[0]);
        assertEquals(1L, ((Number) result.get(1)[1]).longValue());
    }

    @Test
    @DisplayName("Test CRUD operations")
    void testCrudOperations() {
        // Test Create
        RuleExecution newExecution = RuleExecution.builder()
                .ruleName("NewRule")
                .rulePackage("com.example.new")
                .factType("NewFact")
                .executionResult("New result")
                .executionTimeMs(300L)
                .executionDate(new Date())
                .correlationId("corr-new")
                .successful(true)
                .errorMessage(null)
                .build();
        
        RuleExecution savedExecution = ruleExecutionRepository.save(newExecution);
        assertNotNull(savedExecution.getId());
        
        // Test Read
        RuleExecution foundExecution = ruleExecutionRepository.findById(savedExecution.getId()).orElse(null);
        assertNotNull(foundExecution);
        assertEquals("NewRule", foundExecution.getRuleName());
        
        // Test Update
        foundExecution.setExecutionResult("Updated result");
        RuleExecution updatedExecution = ruleExecutionRepository.save(foundExecution);
        assertEquals("Updated result", updatedExecution.getExecutionResult());
        
        // Test Delete
        ruleExecutionRepository.delete(updatedExecution);
        assertFalse(ruleExecutionRepository.existsById(updatedExecution.getId()));
    }
}