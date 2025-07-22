package com.example.springdroolsintegration.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration class for Spring Cache.
 * This class configures the cache manager and cache settings for the application.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache names used in the application.
     */
    public static final String CUSTOMER_DISCOUNT_CACHE = "customerDiscountCache";
    public static final String PRODUCT_RECOMMENDATION_CACHE = "productRecommendationCache";
    public static final String LOAN_APPROVAL_CACHE = "loanApprovalCache";
    public static final String RULE_EXECUTION_CACHE = "ruleExecutionCache";

    /**
     * Creates a CacheManager bean using ConcurrentMapCacheManager.
     * This is a simple cache implementation that stores entries in a ConcurrentHashMap.
     *
     * @return The configured CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Set the caches that will be managed by this cache manager
        cacheManager.setCacheNames(Arrays.asList(
                CUSTOMER_DISCOUNT_CACHE,
                PRODUCT_RECOMMENDATION_CACHE,
                LOAN_APPROVAL_CACHE,
                RULE_EXECUTION_CACHE
        ));
        
        return cacheManager;
    }
}