package com.example.springdroolsintegration.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Spring Web MVC.
 * This class configures CORS for cross-origin requests and registers interceptors.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    private final RateLimitInterceptor rateLimitInterceptor;

    @Value("${app.security.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.security.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.security.cors.exposed-headers:Content-Disposition,X-Correlation-ID}")
    private String exposedHeaders;

    @Value("${app.security.cors.max-age:3600}")
    private long maxAge;
    
    /**
     * Constructor for WebMvcConfig.
     * 
     * @param rateLimitInterceptor The rate limit interceptor
     */
    public WebMvcConfig(RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    /**
     * Configures CORS for cross-origin requests.
     * This method sets up allowed origins, methods, headers, and other CORS settings.
     *
     * @param registry The CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Configuring CORS mappings");
        
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders(allowedHeaders.split(","))
                .exposedHeaders(exposedHeaders.split(","))
                .allowCredentials(true)
                .maxAge(maxAge);
        
        logger.info("CORS configuration: allowedOrigins={}, allowedMethods={}", 
                allowedOrigins, allowedMethods);
    }
    
    /**
     * Registers interceptors for request processing.
     * This method adds the rate limit interceptor to enforce API rate limits.
     *
     * @param registry The interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("Registering interceptors");
        
        // Register rate limit interceptor for API endpoints
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**");
        
        logger.info("Rate limit interceptor registered for API endpoints");
    }
}