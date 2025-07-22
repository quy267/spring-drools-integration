package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.service.RuleCompilationCacheService;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of the RuleCompilationCacheService interface.
 * This service caches compiled rules to improve performance by avoiding unnecessary recompilation.
 */
@Service
public class RuleCompilationCacheServiceImpl implements RuleCompilationCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RuleCompilationCacheServiceImpl.class);
    
    private final DroolsProperties droolsProperties;
    
    // Cache for KieBuilder objects
    private final Map<String, CachedKieBuilder> kieBuilderCache = new ConcurrentHashMap<>();
    
    // Cache for KieContainer objects
    private final Map<String, KieContainer> kieContainerCache = new ConcurrentHashMap<>();
    
    // Cache for KieBase objects
    private final Map<String, KieBase> kieBaseCache = new ConcurrentHashMap<>();
    
    // Cache for resource checksums
    private final Map<String, String> resourceChecksumCache = new ConcurrentHashMap<>();
    
    // Statistics
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    
    /**
     * Constructor for RuleCompilationCacheServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param droolsProperties The Drools configuration properties
     */
    public RuleCompilationCacheServiceImpl(DroolsProperties droolsProperties) {
        this.droolsProperties = droolsProperties;
        logger.info("RuleCompilationCacheService initialized with cacheEnabled={}", droolsProperties.isCacheEnabled());
    }
    
    @Override
    public KieBuilder getCachedKieBuilder(KieFileSystem kieFileSystem, String cacheKey) {
        if (!droolsProperties.isCacheEnabled()) {
            cacheMisses.incrementAndGet();
            return null;
        }
        
        CachedKieBuilder cachedKieBuilder = kieBuilderCache.get(cacheKey);
        if (cachedKieBuilder != null) {
            cacheHits.incrementAndGet();
            logger.debug("KieBuilder cache hit for key: {}", cacheKey);
            return cachedKieBuilder.getKieBuilder();
        }
        
        cacheMisses.incrementAndGet();
        logger.debug("KieBuilder cache miss for key: {}", cacheKey);
        return null;
    }
    
    @Override
    public void cacheKieBuilder(KieFileSystem kieFileSystem, String cacheKey, KieBuilder kieBuilder, Results results) {
        if (!droolsProperties.isCacheEnabled()) {
            return;
        }
        
        logger.debug("Caching KieBuilder for key: {}", cacheKey);
        kieBuilderCache.put(cacheKey, new CachedKieBuilder(kieBuilder, results));
    }
    
    @Override
    public KieContainer getCachedKieContainer(KieBuilder kieBuilder, String cacheKey) {
        if (!droolsProperties.isCacheEnabled()) {
            cacheMisses.incrementAndGet();
            return null;
        }
        
        KieContainer kieContainer = kieContainerCache.get(cacheKey);
        if (kieContainer != null) {
            cacheHits.incrementAndGet();
            logger.debug("KieContainer cache hit for key: {}", cacheKey);
            return kieContainer;
        }
        
        cacheMisses.incrementAndGet();
        logger.debug("KieContainer cache miss for key: {}", cacheKey);
        return null;
    }
    
    @Override
    public void cacheKieContainer(KieBuilder kieBuilder, String cacheKey, KieContainer kieContainer) {
        if (!droolsProperties.isCacheEnabled()) {
            return;
        }
        
        logger.debug("Caching KieContainer for key: {}", cacheKey);
        kieContainerCache.put(cacheKey, kieContainer);
    }
    
    @Override
    public KieBase getCachedKieBase(KieContainer kieContainer, String kieBaseName, String cacheKey) {
        if (!droolsProperties.isCacheEnabled()) {
            cacheMisses.incrementAndGet();
            return null;
        }
        
        KieBase kieBase = kieBaseCache.get(cacheKey);
        if (kieBase != null) {
            cacheHits.incrementAndGet();
            logger.debug("KieBase cache hit for key: {}", cacheKey);
            return kieBase;
        }
        
        cacheMisses.incrementAndGet();
        logger.debug("KieBase cache miss for key: {}", cacheKey);
        return null;
    }
    
    @Override
    public void cacheKieBase(KieContainer kieContainer, String kieBaseName, String cacheKey, KieBase kieBase) {
        if (!droolsProperties.isCacheEnabled()) {
            return;
        }
        
        logger.debug("Caching KieBase for key: {}", cacheKey);
        kieBaseCache.put(cacheKey, kieBase);
    }
    
    @Override
    public boolean hasResourceChanged(Resource resource, String resourceId) {
        if (!droolsProperties.isCacheEnabled()) {
            return true;
        }
        
        try {
            String currentChecksum = calculateResourceChecksum(resource);
            String cachedChecksum = resourceChecksumCache.get(resourceId);
            
            if (cachedChecksum == null) {
                // Resource not in cache
                resourceChecksumCache.put(resourceId, currentChecksum);
                return true;
            }
            
            boolean changed = !cachedChecksum.equals(currentChecksum);
            if (changed) {
                // Update checksum if changed
                resourceChecksumCache.put(resourceId, currentChecksum);
                logger.debug("Resource changed: {}", resourceId);
            }
            
            return changed;
        } catch (IOException e) {
            logger.warn("Error checking if resource has changed: {}", resourceId, e);
            return true;
        }
    }
    
    @Override
    public void evictResource(String resourceId) {
        if (!droolsProperties.isCacheEnabled()) {
            return;
        }
        
        logger.debug("Evicting resource from cache: {}", resourceId);
        resourceChecksumCache.remove(resourceId);
        
        // Evict related KieBuilder, KieContainer, and KieBase entries
        Set<String> keysToRemove = new HashSet<>();
        
        for (String key : kieBuilderCache.keySet()) {
            if (key.contains(resourceId)) {
                keysToRemove.add(key);
            }
        }
        
        for (String key : keysToRemove) {
            kieBuilderCache.remove(key);
            kieContainerCache.remove(key);
            kieBaseCache.remove(key);
        }
    }
    
    @Override
    public void evictAllResources() {
        if (!droolsProperties.isCacheEnabled()) {
            return;
        }
        
        logger.debug("Evicting all resources from cache");
        resourceChecksumCache.clear();
        kieBuilderCache.clear();
        kieContainerCache.clear();
        kieBaseCache.clear();
    }
    
    @Override
    public long getCacheHitCount() {
        return cacheHits.get();
    }
    
    @Override
    public long getCacheMissCount() {
        return cacheMisses.get();
    }
    
    @Override
    public int getCacheSize() {
        return kieBuilderCache.size() + kieContainerCache.size() + kieBaseCache.size();
    }
    
    @Override
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("enabled", droolsProperties.isCacheEnabled());
        statistics.put("hits", cacheHits.get());
        statistics.put("misses", cacheMisses.get());
        statistics.put("hitRatio", calculateHitRatio());
        statistics.put("kieBuilderCacheSize", kieBuilderCache.size());
        statistics.put("kieContainerCacheSize", kieContainerCache.size());
        statistics.put("kieBaseCacheSize", kieBaseCache.size());
        statistics.put("resourceChecksumCacheSize", resourceChecksumCache.size());
        
        return statistics;
    }
    
    @Override
    public Set<String> getCachedResourceIds() {
        return new HashSet<>(resourceChecksumCache.keySet());
    }
    
    /**
     * Calculates a checksum for a resource.
     * This is used to determine if a resource has changed.
     *
     * @param resource The resource
     * @return A checksum for the resource
     * @throws IOException if there is an error reading the resource
     */
    private String calculateResourceChecksum(Resource resource) throws IOException {
        // For simplicity, use a hash of the resource content as the checksum
        // In a production environment, you might want to use a more robust checksum algorithm
        try (InputStream is = resource.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            logger.warn("MD5 algorithm not available, using fallback checksum method");
            // Fallback to a simple hash of the resource path
            return String.valueOf(resource.toString().hashCode());
        }
    }
    
    /**
     * Calculates the cache hit ratio.
     *
     * @return The cache hit ratio as a percentage
     */
    private double calculateHitRatio() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) hits / total * 100.0;
    }
    
    /**
     * Class to hold a cached KieBuilder and its compilation results.
     */
    private static class CachedKieBuilder {
        private final KieBuilder kieBuilder;
        private final Results results;
        
        public CachedKieBuilder(KieBuilder kieBuilder, Results results) {
            this.kieBuilder = kieBuilder;
            this.results = results;
        }
        
        public KieBuilder getKieBuilder() {
            return kieBuilder;
        }
        
        public Results getResults() {
            return results;
        }
    }
}