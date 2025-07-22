package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the RuleExecutionService interface.
 * This service handles the execution of Drools rules and manages rule sessions.
 */
@Service
public class RuleExecutionServiceImpl implements RuleExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(RuleExecutionServiceImpl.class);
    
    private final KieBase kieBase;
    private final DroolsProperties droolsProperties;
    private final ExecutorService executorService;
    
    // Statistics tracking
    private final ConcurrentHashMap<String, AtomicLong> ruleExecutionCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> ruleExecutionTimes = new ConcurrentHashMap<>();
    private final AtomicLong totalExecutions = new AtomicLong(0);
    private final AtomicLong totalBatchExecutions = new AtomicLong(0);
    private final AtomicLong totalAsyncExecutions = new AtomicLong(0);
    private final AtomicLong sessionsCreated = new AtomicLong(0);
    private final AtomicLong sessionsDisposed = new AtomicLong(0);
    
    /**
     * Constructor for RuleExecutionServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieBase The KieBase for creating rule sessions
     * @param droolsProperties Configuration properties for Drools
     */
    public RuleExecutionServiceImpl(KieBase kieBase, DroolsProperties droolsProperties) {
        this.kieBase = kieBase;
        this.droolsProperties = droolsProperties;
        this.executorService = Executors.newFixedThreadPool(droolsProperties.getMaxExecutionThreads());
        
        logger.info("RuleExecutionService initialized with max threads: {}", droolsProperties.getMaxExecutionThreads());
    }
    
    @Override
    public <T> T executeRules(T fact) {
        return executeRules(fact, droolsProperties.getKieSessionName());
    }
    
    @Override
    public <T> T executeRules(T fact, String sessionName) {
        if (fact == null) {
            throw new RuleExecutionException("Cannot execute rules on null fact object");
        }
        
        String executionId = generateExecutionId();
        MDC.put("executionId", executionId);
        logger.debug("Starting rule execution for fact type: {}, execution ID: {}", fact.getClass().getSimpleName(), executionId);
        
        long startTime = System.nanoTime();
        KieSession kieSession = null;
        
        try {
            kieSession = createSession(sessionName);
            kieSession.insert(fact);
            
            int rulesFired = kieSession.fireAllRules();
            totalExecutions.incrementAndGet();
            
            logger.debug("Rule execution completed. Rules fired: {}, execution ID: {}", rulesFired, executionId);
            
            // Update statistics
            long executionTime = System.nanoTime() - startTime;
            updateExecutionStatistics(fact.getClass().getSimpleName(), executionTime);
            
            return fact;
        } catch (Exception e) {
            logger.error("Error executing rules for fact type: {}, execution ID: {}", fact.getClass().getSimpleName(), executionId, e);
            throw new RuleExecutionException("Error executing rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                disposeSession(kieSession);
            }
            MDC.remove("executionId");
        }
    }
    
    @Override
    public <T> List<T> executeRulesForBatch(List<T> facts) {
        return executeRulesForBatch(facts, droolsProperties.getKieSessionName());
    }
    
    @Override
    public <T> List<T> executeRulesForBatch(List<T> facts, String sessionName) {
        if (facts == null || facts.isEmpty()) {
            throw new RuleExecutionException("Cannot execute rules on null or empty facts list");
        }
        
        String executionId = generateExecutionId();
        MDC.put("executionId", executionId);
        String factType = facts.get(0).getClass().getSimpleName();
        logger.debug("Starting batch rule execution for fact type: {}, batch size: {}, execution ID: {}", 
                factType, facts.size(), executionId);
        
        long startTime = System.nanoTime();
        KieSession kieSession = null;
        
        try {
            kieSession = createSession(sessionName);
            
            // Insert all facts
            for (T fact : facts) {
                kieSession.insert(fact);
            }
            
            int rulesFired = kieSession.fireAllRules();
            totalBatchExecutions.incrementAndGet();
            
            logger.debug("Batch rule execution completed. Rules fired: {}, execution ID: {}", rulesFired, executionId);
            
            // Update statistics
            long executionTime = System.nanoTime() - startTime;
            updateExecutionStatistics(factType + ".batch", executionTime);
            
            return facts;
        } catch (Exception e) {
            logger.error("Error executing batch rules for fact type: {}, execution ID: {}", factType, executionId, e);
            throw new RuleExecutionException("Error executing batch rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                disposeSession(kieSession);
            }
            MDC.remove("executionId");
        }
    }
    
    @Override
    public <T> CompletableFuture<T> executeRulesAsync(T fact) {
        totalAsyncExecutions.incrementAndGet();
        return CompletableFuture.supplyAsync(() -> executeRules(fact), executorService);
    }
    
    @Override
    public <T> CompletableFuture<List<T>> executeRulesForBatchAsync(List<T> facts) {
        totalAsyncExecutions.incrementAndGet();
        return CompletableFuture.supplyAsync(() -> executeRulesForBatch(facts), executorService);
    }
    
    @Override
    public KieSession createSession() {
        return createSession(droolsProperties.getKieSessionName());
    }
    
    @Override
    public KieSession createSession(String sessionName) {
        logger.debug("Creating KieSession with name: {}", sessionName);
        KieSession kieSession = kieBase.newKieSession();
        sessionsCreated.incrementAndGet();
        return kieSession;
    }
    
    @Override
    public void disposeSession(KieSession session) {
        if (session != null) {
            try {
                session.dispose();
                sessionsDisposed.incrementAndGet();
                logger.debug("KieSession disposed");
            } catch (Exception e) {
                logger.warn("Error disposing KieSession", e);
            }
        }
    }
    
    @Override
    public Map<String, Object> getExecutionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalExecutions", totalExecutions.get());
        statistics.put("totalBatchExecutions", totalBatchExecutions.get());
        statistics.put("totalAsyncExecutions", totalAsyncExecutions.get());
        statistics.put("sessionsCreated", sessionsCreated.get());
        statistics.put("sessionsDisposed", sessionsDisposed.get());
        
        Map<String, Long> executionCounts = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : ruleExecutionCounts.entrySet()) {
            executionCounts.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("executionCounts", executionCounts);
        
        Map<String, Long> executionTimes = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : ruleExecutionTimes.entrySet()) {
            executionTimes.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("executionTimes", executionTimes);
        
        return Collections.unmodifiableMap(statistics);
    }
    
    /**
     * Generates a unique execution ID for tracking rule executions.
     * 
     * @return A unique execution ID
     */
    private String generateExecutionId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Updates execution statistics for a specific fact type.
     * 
     * @param factType The type of fact being processed
     * @param executionTimeNanos The execution time in nanoseconds
     */
    private void updateExecutionStatistics(String factType, long executionTimeNanos) {
        ruleExecutionCounts.computeIfAbsent(factType, k -> new AtomicLong(0)).incrementAndGet();
        ruleExecutionTimes.computeIfAbsent(factType, k -> new AtomicLong(0)).addAndGet(executionTimeNanos / 1_000_000); // Convert to milliseconds
    }
}