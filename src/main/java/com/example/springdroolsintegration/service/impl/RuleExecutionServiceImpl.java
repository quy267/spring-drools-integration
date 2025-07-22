package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.service.KieSessionPoolService;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the RuleExecutionService interface.
 * This service handles the execution of Drools rules and manages rule sessions.
 * Uses KieSessionPoolService for efficient session pooling and reuse.
 */
@Service
public class RuleExecutionServiceImpl implements RuleExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(RuleExecutionServiceImpl.class);
    
    private final KieBase kieBase;
    private final DroolsProperties droolsProperties;
    private final ExecutorService executorService;
    private final KieSessionPoolService sessionPoolService;
    private final com.example.springdroolsintegration.actuator.RuleEngineMetrics metrics;
    
    /**
     * Constructor for RuleExecutionServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieBase The KieBase for creating rule sessions
     * @param droolsProperties Configuration properties for Drools
     * @param sessionPoolService The service for managing KieSession pooling
     * @param metrics The metrics service for recording rule execution metrics
     */
    public RuleExecutionServiceImpl(
            KieBase kieBase, 
            DroolsProperties droolsProperties, 
            KieSessionPoolService sessionPoolService,
            com.example.springdroolsintegration.actuator.RuleEngineMetrics metrics) {
        this.kieBase = kieBase;
        this.droolsProperties = droolsProperties;
        this.sessionPoolService = sessionPoolService;
        this.metrics = metrics;
        
        // Create a bounded queue to prevent excessive memory usage
        int maxThreads = droolsProperties.getMaxExecutionThreads();
        int queueCapacity = maxThreads * 10; // Limit queue size to prevent OOM
        this.executorService = new ThreadPoolExecutor(
                maxThreads,                  // Core pool size
                maxThreads,                  // Maximum pool size
                60L, TimeUnit.SECONDS,       // Keep alive time for idle threads
                new LinkedBlockingQueue<>(queueCapacity), // Bounded queue
                new ThreadPoolExecutor.CallerRunsPolicy() // Rejection policy
        );
        
        logger.info("RuleExecutionService initialized with max threads: {}, queue capacity: {}", 
                maxThreads, queueCapacity);
        
        // Update session pool metrics
        metrics.updateSessionPoolSize(sessionPoolService.getPoolSize(), droolsProperties.getSessionPoolSize());
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
        String factType = fact.getClass().getSimpleName();
        logger.debug("Starting rule execution for fact type: {}, execution ID: {}", factType, executionId);
        
        long startTime = System.nanoTime();
        KieSession kieSession = null;
        boolean successful = false;
        
        try {
            kieSession = createSession(sessionName);
            kieSession.insert(fact);
            
            int rulesFired = kieSession.fireAllRules();
            
            logger.debug("Rule execution completed. Rules fired: {}, execution ID: {}", rulesFired, executionId);
            
            // Mark as successful
            successful = true;
            return fact;
        } catch (Exception e) {
            logger.error("Error executing rules for fact type: {}, execution ID: {}", factType, executionId, e);
            throw new RuleExecutionException("Error executing rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                disposeSession(kieSession);
            }
            
            // Record metrics
            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
            metrics.recordRuleExecution("executeRules", factType, executionTime, successful);
            
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
        boolean successful = false;
        
        try {
            kieSession = createSession(sessionName);
            
            // Insert all facts
            for (T fact : facts) {
                kieSession.insert(fact);
            }
            
            int rulesFired = kieSession.fireAllRules();
            
            logger.debug("Batch rule execution completed. Rules fired: {}, execution ID: {}", rulesFired, executionId);
            
            // Mark as successful
            successful = true;
            return facts;
        } catch (Exception e) {
            logger.error("Error executing batch rules for fact type: {}, execution ID: {}", factType, executionId, e);
            throw new RuleExecutionException("Error executing batch rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                disposeSession(kieSession);
            }
            
            // Record metrics
            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
            metrics.recordBatchExecution(facts.size(), executionTime, successful);
            
            MDC.remove("executionId");
        }
    }
    
    @Override
    public <T> CompletableFuture<T> executeRulesAsync(T fact) {
        metrics.recordAsyncExecution();
        return CompletableFuture.supplyAsync(() -> executeRules(fact), executorService);
    }
    
    @Override
    public <T> CompletableFuture<List<T>> executeRulesForBatchAsync(List<T> facts) {
        metrics.recordAsyncExecution();
        return CompletableFuture.supplyAsync(() -> executeRulesForBatch(facts), executorService);
    }
    
    @Override
    public KieSession createSession() {
        return createSession(droolsProperties.getKieSessionName());
    }
    
    @Override
    public KieSession createSession(String sessionName) {
        logger.debug("Borrowing KieSession from pool with name: {}", sessionName);
        long startTime = System.nanoTime();
        KieSession kieSession = sessionPoolService.borrowSession(sessionName);
        long creationTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
        metrics.recordSessionCreation(creationTime);
        return kieSession;
    }
    
    @Override
    public void disposeSession(KieSession session) {
        if (session != null) {
            try {
                logger.debug("Returning KieSession to pool");
                sessionPoolService.returnSession(session);
                metrics.recordSessionDisposal();
            } catch (Exception e) {
                logger.warn("Error returning KieSession to pool", e);
            }
        }
    }
    
    @Override
    public <T> List<T> executeRulesForChunkedBatch(List<T> facts, int chunkSize) {
        return executeRulesForChunkedBatch(facts, chunkSize, droolsProperties.getKieSessionName());
    }
    
    @Override
    public <T> List<T> executeRulesForChunkedBatch(List<T> facts, int chunkSize, String sessionName) {
        if (facts == null || facts.isEmpty()) {
            throw new RuleExecutionException("Cannot execute rules on null or empty facts list");
        }
        
        if (chunkSize <= 0) {
            throw new RuleExecutionException("Chunk size must be greater than zero");
        }
        
        String executionId = generateExecutionId();
        MDC.put("executionId", executionId);
        String factType = facts.get(0).getClass().getSimpleName();
        
        logger.debug("Starting chunked batch rule execution for fact type: {}, total size: {}, chunk size: {}, execution ID: {}", 
                factType, facts.size(), chunkSize, executionId);
        
        long startTime = System.nanoTime();
        KieSession kieSession = null;
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        long initialMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
        boolean successful = false;
        
        try {
            // Create a single session for all chunks to maintain rule state
            kieSession = createSession(sessionName);
            int totalFacts = facts.size();
            int totalChunks = (totalFacts + chunkSize - 1) / chunkSize; // Ceiling division
            
            logger.debug("Processing {} facts in {} chunks of size {}", totalFacts, totalChunks, chunkSize);
            
            // Process facts in chunks
            for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                int fromIndex = chunkIndex * chunkSize;
                int toIndex = Math.min(fromIndex + chunkSize, totalFacts);
                List<T> chunk = facts.subList(fromIndex, toIndex);
                
                // Log memory usage before processing chunk
                MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
                long usedMemory = heapUsage.getUsed();
                long maxMemory = heapUsage.getMax();
                double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
                
                logger.debug("Processing chunk {}/{}: {} facts, memory usage: {}/{} MB ({}%)", 
                        chunkIndex + 1, totalChunks, chunk.size(), 
                        usedMemory / (1024 * 1024), maxMemory / (1024 * 1024), 
                        String.format("%.2f", memoryUsagePercent));
                
                // Insert facts from this chunk
                for (T fact : chunk) {
                    kieSession.insert(fact);
                }
                
                // Fire rules for this chunk
                int rulesFired = kieSession.fireAllRules();
                
                logger.debug("Chunk {}/{} processed, rules fired: {}", chunkIndex + 1, totalChunks, rulesFired);
                
                // Suggest garbage collection after each chunk if memory usage is high
                if (memoryUsagePercent > 70) {
                    logger.debug("High memory usage detected ({}%), suggesting garbage collection", 
                            String.format("%.2f", memoryUsagePercent));
                    System.gc();
                }
            }
            
            // Calculate memory usage
            long finalMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
            long memoryDelta = finalMemory - initialMemory;
            
            logger.debug("Chunked batch rule execution completed. Memory delta: {} MB, execution ID: {}", 
                    memoryDelta / (1024 * 1024), executionId);
            
            // Mark as successful
            successful = true;
            return facts;
        } catch (Exception e) {
            logger.error("Error executing chunked batch rules for fact type: {}, execution ID: {}", 
                    factType, executionId, e);
            throw new RuleExecutionException("Error executing chunked batch rules: " + e.getMessage(), e);
        } finally {
            if (kieSession != null) {
                disposeSession(kieSession);
            }
            
            // Record metrics
            long executionTime = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds
            metrics.recordBatchExecution(facts.size(), executionTime, successful);
            
            MDC.remove("executionId");
        }
    }
    
    @Override
    public <T> CompletableFuture<List<T>> executeRulesForChunkedBatchAsync(List<T> facts, int chunkSize) {
        metrics.recordAsyncExecution();
        return CompletableFuture.supplyAsync(() -> executeRulesForChunkedBatch(facts, chunkSize), executorService);
    }
    
    @Override
    public Map<String, Object> getExecutionStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // Add memory usage statistics
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        Map<String, Object> memoryStats = new HashMap<>();
        memoryStats.put("heapUsed", heapUsage.getUsed());
        memoryStats.put("heapMax", heapUsage.getMax());
        memoryStats.put("heapUsedMB", heapUsage.getUsed() / (1024 * 1024));
        memoryStats.put("heapMaxMB", heapUsage.getMax() / (1024 * 1024));
        memoryStats.put("heapUsagePercent", (double) heapUsage.getUsed() / heapUsage.getMax() * 100);
        statistics.put("memory", memoryStats);
        
        // Add session pool statistics
        Map<String, Object> poolStatistics = new HashMap<>();
        poolStatistics.put("poolSize", sessionPoolService.getPoolSize());
        poolStatistics.put("totalSessionsCreated", sessionPoolService.getTotalSessionsCreated());
        poolStatistics.put("totalSessionsBorrowed", sessionPoolService.getTotalSessionsBorrowed());
        poolStatistics.put("totalSessionsReturned", sessionPoolService.getTotalSessionsReturned());
        statistics.put("sessionPool", poolStatistics);
        
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
}