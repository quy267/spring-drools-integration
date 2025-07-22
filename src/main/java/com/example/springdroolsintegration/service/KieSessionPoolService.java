package com.example.springdroolsintegration.service;

import org.kie.api.runtime.KieSession;

/**
 * Service interface for managing a pool of KieSession objects.
 * This service provides methods to borrow and return KieSession objects from a pool,
 * improving performance by reusing sessions instead of creating and disposing them for each rule execution.
 */
public interface KieSessionPoolService {

    /**
     * Borrows a KieSession from the pool.
     * If no session is available in the pool, a new one will be created.
     *
     * @return A KieSession from the pool
     */
    KieSession borrowSession();

    /**
     * Borrows a KieSession from the pool with a specific name.
     * If no session is available in the pool, a new one will be created.
     *
     * @param sessionName The name of the session
     * @return A KieSession from the pool
     */
    KieSession borrowSession(String sessionName);

    /**
     * Returns a KieSession to the pool.
     * The session will be reset (all facts removed) before being returned to the pool.
     *
     * @param session The KieSession to return to the pool
     */
    void returnSession(KieSession session);

    /**
     * Clears all sessions from the pool.
     * This method should be called when the application is shutting down.
     */
    void clearPool();

    /**
     * Gets the current size of the pool.
     *
     * @return The number of available sessions in the pool
     */
    int getPoolSize();

    /**
     * Gets the total number of sessions created by the pool.
     *
     * @return The total number of sessions created
     */
    long getTotalSessionsCreated();

    /**
     * Gets the total number of sessions borrowed from the pool.
     *
     * @return The total number of sessions borrowed
     */
    long getTotalSessionsBorrowed();

    /**
     * Gets the total number of sessions returned to the pool.
     *
     * @return The total number of sessions returned
     */
    long getTotalSessionsReturned();
}