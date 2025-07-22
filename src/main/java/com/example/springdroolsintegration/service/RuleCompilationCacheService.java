package com.example.springdroolsintegration.service;

import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

import java.util.Map;
import java.util.Set;

/**
 * Service interface for caching compiled rules.
 * This service provides methods to store and retrieve compiled rules,
 * improving performance by avoiding unnecessary recompilation.
 */
public interface RuleCompilationCacheService {

    /**
     * Gets a cached KieBuilder for the given KieFileSystem.
     *
     * @param kieFileSystem The KieFileSystem containing rule resources
     * @param cacheKey A unique identifier for the KieFileSystem
     * @return The cached KieBuilder, or null if not found
     */
    KieBuilder getCachedKieBuilder(KieFileSystem kieFileSystem, String cacheKey);

    /**
     * Caches a KieBuilder for the given KieFileSystem.
     *
     * @param kieFileSystem The KieFileSystem containing rule resources
     * @param cacheKey A unique identifier for the KieFileSystem
     * @param kieBuilder The KieBuilder to cache
     * @param results The compilation results
     */
    void cacheKieBuilder(KieFileSystem kieFileSystem, String cacheKey, KieBuilder kieBuilder, Results results);

    /**
     * Gets a cached KieContainer for the given KieBuilder.
     *
     * @param kieBuilder The KieBuilder
     * @param cacheKey A unique identifier for the KieBuilder
     * @return The cached KieContainer, or null if not found
     */
    KieContainer getCachedKieContainer(KieBuilder kieBuilder, String cacheKey);

    /**
     * Caches a KieContainer for the given KieBuilder.
     *
     * @param kieBuilder The KieBuilder
     * @param cacheKey A unique identifier for the KieBuilder
     * @param kieContainer The KieContainer to cache
     */
    void cacheKieContainer(KieBuilder kieBuilder, String cacheKey, KieContainer kieContainer);

    /**
     * Gets a cached KieBase for the given KieContainer.
     *
     * @param kieContainer The KieContainer
     * @param kieBaseName The name of the KieBase
     * @param cacheKey A unique identifier for the KieContainer and KieBase
     * @return The cached KieBase, or null if not found
     */
    KieBase getCachedKieBase(KieContainer kieContainer, String kieBaseName, String cacheKey);

    /**
     * Caches a KieBase for the given KieContainer.
     *
     * @param kieContainer The KieContainer
     * @param kieBaseName The name of the KieBase
     * @param cacheKey A unique identifier for the KieContainer and KieBase
     * @param kieBase The KieBase to cache
     */
    void cacheKieBase(KieContainer kieContainer, String kieBaseName, String cacheKey, KieBase kieBase);

    /**
     * Checks if a resource has changed since it was last cached.
     *
     * @param resource The rule resource
     * @param resourceId A unique identifier for the resource
     * @return true if the resource has changed, false otherwise
     */
    boolean hasResourceChanged(Resource resource, String resourceId);

    /**
     * Evicts a resource from the cache.
     *
     * @param resourceId A unique identifier for the resource
     */
    void evictResource(String resourceId);

    /**
     * Evicts all resources from the cache.
     */
    void evictAllResources();

    /**
     * Gets the cache hit count.
     *
     * @return The number of cache hits
     */
    long getCacheHitCount();

    /**
     * Gets the cache miss count.
     *
     * @return The number of cache misses
     */
    long getCacheMissCount();

    /**
     * Gets the cache size.
     *
     * @return The number of entries in the cache
     */
    int getCacheSize();

    /**
     * Gets the cache statistics.
     *
     * @return A map of cache statistics
     */
    Map<String, Object> getCacheStatistics();

    /**
     * Gets the set of cached resource IDs.
     *
     * @return The set of cached resource IDs
     */
    Set<String> getCachedResourceIds();
}