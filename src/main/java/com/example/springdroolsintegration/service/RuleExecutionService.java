package com.example.springdroolsintegration.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.api.runtime.KieSession;

/**
 * Service interface for rule execution.
 * This service handles the execution of Drools rules and manages rule sessions.
 */
public interface RuleExecutionService {
    
    /**
     * Executes rules on a single fact object.
     * 
     * @param <T> The type of the fact object
     * @param fact The fact object to execute rules on
     * @return The fact object after rule execution
     */
    <T> T executeRules(T fact);
    
    /**
     * Executes rules on a single fact object with a specific rule session name.
     * 
     * @param <T> The type of the fact object
     * @param fact The fact object to execute rules on
     * @param sessionName The name of the rule session to use
     * @return The fact object after rule execution
     */
    <T> T executeRules(T fact, String sessionName);
    
    /**
     * Executes rules on a collection of fact objects.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @return The collection of fact objects after rule execution
     */
    <T> List<T> executeRulesForBatch(List<T> facts);
    
    /**
     * Executes rules on a collection of fact objects with a specific rule session name.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @param sessionName The name of the rule session to use
     * @return The collection of fact objects after rule execution
     */
    <T> List<T> executeRulesForBatch(List<T> facts, String sessionName);
    
    /**
     * Executes rules asynchronously on a single fact object.
     * 
     * @param <T> The type of the fact object
     * @param fact The fact object to execute rules on
     * @return A CompletableFuture that will complete with the fact object after rule execution
     */
    <T> CompletableFuture<T> executeRulesAsync(T fact);
    
    /**
     * Executes rules asynchronously on a collection of fact objects.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @return A CompletableFuture that will complete with the collection of fact objects after rule execution
     */
    <T> CompletableFuture<List<T>> executeRulesForBatchAsync(List<T> facts);
    
    /**
     * Creates a new KieSession.
     * 
     * @return A new KieSession
     */
    KieSession createSession();
    
    /**
     * Creates a new KieSession with a specific name.
     * 
     * @param sessionName The name of the session to create
     * @return A new KieSession
     */
    KieSession createSession(String sessionName);
    
    /**
     * Disposes a KieSession.
     * 
     * @param session The KieSession to dispose
     */
    void disposeSession(KieSession session);
    
    /**
     * Executes rules on a large collection of fact objects by processing them in smaller chunks.
     * This method is optimized for memory usage with large datasets.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @param chunkSize The number of facts to process in each chunk
     * @return The collection of fact objects after rule execution
     */
    <T> List<T> executeRulesForChunkedBatch(List<T> facts, int chunkSize);
    
    /**
     * Executes rules on a large collection of fact objects by processing them in smaller chunks,
     * with a specific rule session name. This method is optimized for memory usage with large datasets.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @param chunkSize The number of facts to process in each chunk
     * @param sessionName The name of the rule session to use
     * @return The collection of fact objects after rule execution
     */
    <T> List<T> executeRulesForChunkedBatch(List<T> facts, int chunkSize, String sessionName);
    
    /**
     * Executes rules asynchronously on a large collection of fact objects by processing them in smaller chunks.
     * This method is optimized for memory usage with large datasets.
     * 
     * @param <T> The type of the fact objects
     * @param facts The collection of fact objects to execute rules on
     * @param chunkSize The number of facts to process in each chunk
     * @return A CompletableFuture that will complete with the collection of fact objects after rule execution
     */
    <T> CompletableFuture<List<T>> executeRulesForChunkedBatchAsync(List<T> facts, int chunkSize);
    
    /**
     * Gets rule execution statistics.
     * 
     * @return A map of rule execution statistics
     */
    Map<String, Object> getExecutionStatistics();
}