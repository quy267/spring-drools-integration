package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.DiscountRule;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.service.impl.RuleExecutionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RuleExecutionService.
 * These tests verify that the rule execution service can execute rules correctly.
 */
@SpringBootTest
@ActiveProfiles("test")
public class RuleExecutionServiceIntegrationTest {

    @Autowired
    private RuleExecutionService ruleExecutionService;

    @Autowired
    private KieBase kieBase;

    @Test
    @DisplayName("Test rule execution with a single fact")
    public void testExecuteRules() {
        // Create a test customer
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setAge(65); // Senior customer
        customer.setLoyaltyTier("GOLD");

        // Execute rules
        Customer result = ruleExecutionService.executeRules(customer);

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertEquals("Test Customer", result.getName(), "Customer name should be preserved");
        assertEquals(65, result.getAge(), "Customer age should be preserved");
        assertEquals("GOLD", result.getLoyaltyTier(), "Customer loyalty tier should be preserved");
    }

    @Test
    @DisplayName("Test rule execution with a specific session name")
    public void testExecuteRulesWithSessionName() {
        // Create a test order
        Order order = new Order();
        order.setAmount(200.0); // Large order
        order.setVolume(5);

        // Execute rules with a specific session name
        Order result = ruleExecutionService.executeRules(order, "testSession");

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertEquals(200.0, result.getAmount(), "Order amount should be preserved");
        assertEquals(5, result.getVolume(), "Order volume should be preserved");
    }

    @Test
    @DisplayName("Test batch rule execution")
    public void testExecuteRulesForBatch() {
        // Create a list of test customers
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Customer customer = new Customer();
            customer.setName("Customer " + i);
            customer.setAge(30 + i * 10); // Different ages
            customer.setLoyaltyTier(i % 2 == 0 ? "GOLD" : "SILVER"); // Different tiers
            customers.add(customer);
        }

        // Execute rules in batch
        List<Customer> results = ruleExecutionService.executeRulesForBatch(customers);

        // Verify the results
        assertNotNull(results, "Results should not be null");
        assertEquals(5, results.size(), "Should have 5 results");
        for (int i = 0; i < 5; i++) {
            Customer result = results.get(i);
            assertEquals("Customer " + i, result.getName(), "Customer name should be preserved");
            assertEquals(30 + i * 10, result.getAge(), "Customer age should be preserved");
            assertEquals(i % 2 == 0 ? "GOLD" : "SILVER", result.getLoyaltyTier(), "Customer loyalty tier should be preserved");
        }
    }

    @Test
    @DisplayName("Test batch rule execution with a specific session name")
    public void testExecuteRulesForBatchWithSessionName() {
        // Create a list of test orders
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setAmount(100.0 + i * 50); // Different amounts
            order.setVolume(i + 1); // Different volumes
            orders.add(order);
        }

        // Execute rules in batch with a specific session name
        List<Order> results = ruleExecutionService.executeRulesForBatch(orders, "testBatchSession");

        // Verify the results
        assertNotNull(results, "Results should not be null");
        assertEquals(5, results.size(), "Should have 5 results");
        for (int i = 0; i < 5; i++) {
            Order result = results.get(i);
            assertEquals(100.0 + i * 50, result.getAmount(), "Order amount should be preserved");
            assertEquals(i + 1, result.getVolume(), "Order volume should be preserved");
        }
    }

    @Test
    @DisplayName("Test asynchronous rule execution")
    public void testExecuteRulesAsync() throws ExecutionException, InterruptedException {
        // Create a test discount rule
        DiscountRule discountRule = new DiscountRule();
        discountRule.setName("Test Rule");
        discountRule.setDiscountPercentage(10.0);
        discountRule.setPriority(100);

        // Execute rules asynchronously
        CompletableFuture<DiscountRule> future = ruleExecutionService.executeRulesAsync(discountRule);

        // Wait for the result
        DiscountRule result = future.get();

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertEquals("Test Rule", result.getName(), "Rule name should be preserved");
        assertEquals(10.0, result.getDiscountPercentage(), "Discount percentage should be preserved");
        assertEquals(100, result.getPriority(), "Priority should be preserved");
    }

    @Test
    @DisplayName("Test asynchronous batch rule execution")
    public void testExecuteRulesForBatchAsync() throws ExecutionException, InterruptedException {
        // Create a list of test discount rules
        List<DiscountRule> rules = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DiscountRule rule = new DiscountRule();
            rule.setName("Rule " + i);
            rule.setDiscountPercentage(5.0 + i * 5); // Different percentages
            rule.setPriority(100 - i * 10); // Different priorities
            rules.add(rule);
        }

        // Execute rules in batch asynchronously
        CompletableFuture<List<DiscountRule>> future = ruleExecutionService.executeRulesForBatchAsync(rules);

        // Wait for the results
        List<DiscountRule> results = future.get();

        // Verify the results
        assertNotNull(results, "Results should not be null");
        assertEquals(5, results.size(), "Should have 5 results");
        for (int i = 0; i < 5; i++) {
            DiscountRule result = results.get(i);
            assertEquals("Rule " + i, result.getName(), "Rule name should be preserved");
            assertEquals(5.0 + i * 5, result.getDiscountPercentage(), "Discount percentage should be preserved");
            assertEquals(100 - i * 10, result.getPriority(), "Priority should be preserved");
        }
    }

    @Test
    @DisplayName("Test session creation and disposal")
    public void testSessionManagement() {
        // Create a session
        KieSession session = ruleExecutionService.createSession();

        // Verify the session
        assertNotNull(session, "Session should not be null");
        assertTrue(session.getId() > 0, "Session ID should be positive");

        // Dispose the session
        ruleExecutionService.disposeSession(session);
    }

    @Test
    @DisplayName("Test session creation with a specific name")
    public void testNamedSessionCreation() {
        // Create a session with a specific name
        KieSession session = ruleExecutionService.createSession("testNamedSession");

        // Verify the session
        assertNotNull(session, "Session should not be null");
        assertTrue(session.getId() > 0, "Session ID should be positive");

        // Dispose the session
        ruleExecutionService.disposeSession(session);
    }

    @Test
    @DisplayName("Test execution statistics")
    public void testExecutionStatistics() {
        // Create a test customer
        Customer customer = new Customer();
        customer.setName("Statistics Test Customer");
        customer.setAge(40);
        customer.setLoyaltyTier("SILVER");

        // Execute rules
        ruleExecutionService.executeRules(customer);

        // Get execution statistics
        Map<String, Object> statistics = ruleExecutionService.getExecutionStatistics();

        // Verify statistics
        assertNotNull(statistics, "Statistics should not be null");
        assertTrue(statistics.containsKey("totalExecutions"), "Statistics should contain totalExecutions");
        assertTrue(statistics.containsKey("sessionsCreated"), "Statistics should contain sessionsCreated");
        assertTrue(statistics.containsKey("sessionsDisposed"), "Statistics should contain sessionsDisposed");
    }
}