package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.service.KieSessionPoolService;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the KieSessionPoolService interface.
 * This service manages a pool of KieSession objects to improve performance
 * by reusing sessions instead of creating and disposing them for each rule execution.
 */
@Service
public class KieSessionPoolServiceImpl implements KieSessionPoolService {

    private static final Logger logger = LoggerFactory.getLogger(KieSessionPoolServiceImpl.class);

    private final KieBase kieBase;
    private final DroolsProperties droolsProperties;
    private final Map<String, Queue<KieSession>> sessionPools = new ConcurrentHashMap<>();
    
    // Statistics tracking
    private final AtomicLong totalSessionsCreated = new AtomicLong(0);
    private final AtomicLong totalSessionsBorrowed = new AtomicLong(0);
    private final AtomicLong totalSessionsReturned = new AtomicLong(0);

    /**
     * Constructor for KieSessionPoolServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieBase The KieBase for creating rule sessions
     * @param droolsProperties Configuration properties for Drools
     */
    public KieSessionPoolServiceImpl(KieBase kieBase, DroolsProperties droolsProperties) {
        this.kieBase = kieBase;
        this.droolsProperties = droolsProperties;
        
        logger.info("KieSessionPoolService initialized with pool size: {}", droolsProperties.getSessionPoolSize());
    }

    @Override
    public KieSession borrowSession() {
        return borrowSession(droolsProperties.getKieSessionName());
    }

    @Override
    public KieSession borrowSession(String sessionName) {
        totalSessionsBorrowed.incrementAndGet();
        
        // Get or create the pool for this session name
        Queue<KieSession> pool = sessionPools.computeIfAbsent(sessionName, 
                k -> new ConcurrentLinkedQueue<>());
        
        // Try to get a session from the pool
        KieSession session = pool.poll();
        
        // If no session is available, create a new one
        if (session == null) {
            session = createNewSession();
            logger.debug("Created new KieSession for pool (name: {})", sessionName);
        } else {
            logger.debug("Borrowed KieSession from pool (name: {})", sessionName);
        }
        
        return session;
    }

    @Override
    public void returnSession(KieSession session) {
        if (session == null) {
            return;
        }
        
        totalSessionsReturned.incrementAndGet();
        
        try {
            // Reset the session by removing all facts and clearing the agenda
            session.getFactHandles().forEach(session::delete);
            session.getAgenda().clear();
            
            // Get the default pool
            Queue<KieSession> pool = sessionPools.get(droolsProperties.getKieSessionName());
            
            // If the pool is full, dispose the session
            if (pool != null && pool.size() >= droolsProperties.getSessionPoolSize()) {
                session.dispose();
                logger.debug("Pool is full, disposed KieSession");
            } else {
                // Otherwise, add it back to the pool
                if (pool != null) {
                    pool.offer(session);
                    logger.debug("Returned KieSession to pool");
                } else {
                    // If the pool doesn't exist (should not happen), dispose the session
                    session.dispose();
                    logger.warn("Pool not found, disposed KieSession");
                }
            }
        } catch (Exception e) {
            // If there's an error resetting the session, dispose it
            logger.warn("Error resetting KieSession, disposing it", e);
            try {
                session.dispose();
            } catch (Exception disposeEx) {
                logger.error("Error disposing KieSession", disposeEx);
            }
        }
    }

    @Override
    public void clearPool() {
        logger.info("Clearing KieSession pool");
        
        // Dispose all sessions in all pools
        sessionPools.values().forEach(pool -> {
            KieSession session;
            while ((session = pool.poll()) != null) {
                try {
                    session.dispose();
                } catch (Exception e) {
                    logger.warn("Error disposing KieSession during pool clear", e);
                }
            }
        });
        
        // Clear all pools
        sessionPools.clear();
    }

    @Override
    public int getPoolSize() {
        return sessionPools.values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    @Override
    public long getTotalSessionsCreated() {
        return totalSessionsCreated.get();
    }

    @Override
    public long getTotalSessionsBorrowed() {
        return totalSessionsBorrowed.get();
    }

    @Override
    public long getTotalSessionsReturned() {
        return totalSessionsReturned.get();
    }

    /**
     * Creates a new KieSession.
     *
     * @return A new KieSession
     */
    private KieSession createNewSession() {
        KieSession session = kieBase.newKieSession();
        totalSessionsCreated.incrementAndGet();
        return session;
    }

    /**
     * Cleans up resources when the application is shutting down.
     */
    @PreDestroy
    public void destroy() {
        clearPool();
    }
}