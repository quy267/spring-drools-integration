package com.example.springdroolsintegration.config;

import com.example.springdroolsintegration.util.LoggingUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add correlation IDs to all incoming requests.
 * This filter intercepts all HTTP requests, adds a correlation ID to the MDC,
 * and ensures it's available throughout the request processing chain.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String correlationId = getCorrelationId(request);
        
        try {
            // Set the correlation ID in MDC
            LoggingUtils.setCorrelationId(correlationId);
            
            // Add the correlation ID to the response headers
            response.addHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Log the request
            logRequest(request, correlationId);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Log the response
            logResponse(response, correlationId);
            
            // Clear the correlation ID from MDC
            LoggingUtils.clearCorrelationId();
        }
    }
    
    /**
     * Gets the correlation ID from the request headers or generates a new one.
     *
     * @param request The HTTP request
     * @return The correlation ID
     */
    private String getCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        return correlationId;
    }
    
    /**
     * Logs the request details.
     *
     * @param request The HTTP request
     * @param correlationId The correlation ID
     */
    private void logRequest(HttpServletRequest request, String correlationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[correlationId={}] Request: {} {} (from {})", 
                    correlationId,
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getRemoteAddr());
        }
    }
    
    /**
     * Logs the response details.
     *
     * @param response The HTTP response
     * @param correlationId The correlation ID
     */
    private void logResponse(HttpServletResponse response, String correlationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[correlationId={}] Response: status={}", 
                    correlationId,
                    response.getStatus());
        }
    }
}