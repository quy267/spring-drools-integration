package com.example.springdroolsintegration.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Interceptor for rate limiting API requests.
 * This class implements a simple rate limiting mechanism based on client IP address.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);
    
    // Cache to store rate limit data for each client
    private final Map<String, RateLimitData> rateLimitCache = new ConcurrentHashMap<>();
    
    @Value("${app.security.rate-limit.enabled:true}")
    private boolean enabled;
    
    @Value("${app.security.rate-limit.max-requests:100}")
    private int maxRequests;
    
    @Value("${app.security.rate-limit.time-window-seconds:60}")
    private int timeWindowSeconds;
    
    @Value("${app.security.rate-limit.include-paths:/api/v1/rules/**,/api/v1/discounts/**,/api/v1/loans/**,/api/v1/recommendations/**}")
    private String includePaths;
    
    /**
     * Intercepts requests and applies rate limiting.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param handler The handler for the request
     * @return true if the request is allowed, false otherwise
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip rate limiting if disabled
        if (!enabled) {
            return true;
        }
        
        // Skip rate limiting for non-API paths
        String requestPath = request.getRequestURI();
        if (!isRateLimitedPath(requestPath)) {
            return true;
        }
        
        // Get client identifier (IP address or other identifier)
        String clientId = getClientIdentifier(request);
        
        // Check rate limit
        if (isRateLimitExceeded(clientId)) {
            logger.warn("Rate limit exceeded for client: {}, path: {}", clientId, requestPath);
            
            // Set response status and headers
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader("X-Rate-Limit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-Rate-Limit-Window", timeWindowSeconds + "s");
            response.setHeader("Retry-After", String.valueOf(getRetryAfterSeconds(clientId)));
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Checks if the rate limit is exceeded for a client.
     * 
     * @param clientId The client identifier
     * @return true if the rate limit is exceeded, false otherwise
     */
    private boolean isRateLimitExceeded(String clientId) {
        long currentTime = System.currentTimeMillis();
        
        // Get or create rate limit data for the client
        RateLimitData data = rateLimitCache.computeIfAbsent(clientId, k -> new RateLimitData());
        
        // Reset count if time window has passed
        if (currentTime - data.getWindowStartTime() > TimeUnit.SECONDS.toMillis(timeWindowSeconds)) {
            data.resetWindow(currentTime);
        }
        
        // Increment request count
        data.incrementCount();
        
        // Check if rate limit is exceeded
        return data.getRequestCount() > maxRequests;
    }
    
    /**
     * Gets the number of seconds to wait before retrying.
     * 
     * @param clientId The client identifier
     * @return The number of seconds to wait
     */
    private int getRetryAfterSeconds(String clientId) {
        RateLimitData data = rateLimitCache.get(clientId);
        if (data == null) {
            return timeWindowSeconds;
        }
        
        long elapsedMillis = System.currentTimeMillis() - data.getWindowStartTime();
        long remainingMillis = TimeUnit.SECONDS.toMillis(timeWindowSeconds) - elapsedMillis;
        
        return (int) Math.ceil(remainingMillis / 1000.0);
    }
    
    /**
     * Gets the client identifier from the request.
     * This method uses the client IP address as the identifier.
     * 
     * @param request The HTTP request
     * @return The client identifier
     */
    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get the real client IP if behind a proxy
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        
        // If X-Forwarded-For contains multiple IPs, use the first one (client IP)
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        
        return clientIp;
    }
    
    /**
     * Checks if a path should be rate limited.
     * 
     * @param path The request path
     * @return true if the path should be rate limited, false otherwise
     */
    private boolean isRateLimitedPath(String path) {
        if (path == null) {
            return false;
        }
        
        String[] paths = includePaths.split(",");
        for (String includePath : paths) {
            String trimmedPath = includePath.trim();
            if (trimmedPath.endsWith("/**")) {
                // Handle wildcard paths
                String prefix = trimmedPath.substring(0, trimmedPath.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else {
                // Handle exact paths
                if (path.equals(trimmedPath)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Inner class to store rate limit data for a client.
     */
    private static class RateLimitData {
        private long windowStartTime;
        private int requestCount;
        
        public RateLimitData() {
            this.windowStartTime = System.currentTimeMillis();
            this.requestCount = 0;
        }
        
        public void resetWindow(long currentTime) {
            this.windowStartTime = currentTime;
            this.requestCount = 0;
        }
        
        public void incrementCount() {
            this.requestCount++;
        }
        
        public long getWindowStartTime() {
            return windowStartTime;
        }
        
        public int getRequestCount() {
            return requestCount;
        }
    }
}