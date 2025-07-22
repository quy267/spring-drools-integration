package com.example.springdroolsintegration.config;

import com.example.springdroolsintegration.service.SecurityAuditService;
import com.example.springdroolsintegration.util.LoggingUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Filter to add correlation IDs to all incoming requests and log security-relevant events.
 * This filter intercepts all HTTP requests, adds a correlation ID to the MDC,
 * tracks client IP addresses, and logs security-relevant events.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    
    private final SecurityAuditService securityAuditService;
    
    @Value("${app.security.audit.log-all-requests:false}")
    private boolean logAllRequests;
    
    @Value("${app.security.audit.suspicious-status-codes:400,401,403,405,429}")
    private String suspiciousStatusCodes;
    
    /**
     * Constructor for CorrelationIdFilter.
     * 
     * @param securityAuditService The security audit service
     */
    public CorrelationIdFilter(SecurityAuditService securityAuditService) {
        this.securityAuditService = securityAuditService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String correlationId = getCorrelationId(request);
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        try {
            // Set the correlation ID in MDC
            LoggingUtils.setCorrelationId(correlationId);
            
            // Set the client IP in thread local
            LoggingUtils.setClientIp(clientIp);
            
            // Add the correlation ID to the response headers
            response.addHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Log the request
            logRequest(request, correlationId, clientIp);
            
            // Log access event if configured to log all requests
            if (logAllRequests) {
                securityAuditService.logAccessEvent(
                        getUsernameFromRequest(request),
                        requestUri,
                        method,
                        true,
                        "Request received"
                );
            }
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Log the response
            logResponse(response, correlationId, requestUri, method, clientIp);
            
            // Log suspicious responses
            int status = response.getStatus();
            if (isSuspiciousStatusCode(status)) {
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("uri", requestUri);
                metadata.put("method", method);
                metadata.put("statusCode", status);
                metadata.put("clientIp", clientIp);
                
                securityAuditService.logSecurityViolation(
                        getUsernameFromRequest(request),
                        "SUSPICIOUS_RESPONSE",
                        "Suspicious HTTP status code: " + status,
                        metadata
                );
            }
            
            // Clear the correlation ID from MDC
            LoggingUtils.clearCorrelationId();
            
            // Clear the client IP from thread local
            LoggingUtils.clearClientIp();
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
     * Gets the client IP address from the request.
     *
     * @param request The HTTP request
     * @return The client IP address
     */
    private String getClientIp(HttpServletRequest request) {
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
     * Gets the username from the request.
     * This is a placeholder method that should be replaced with actual authentication integration.
     *
     * @param request The HTTP request
     * @return The username, or "anonymous" if not authenticated
     */
    private String getUsernameFromRequest(HttpServletRequest request) {
        // In a real application, this would get the username from the security context
        // For now, we'll just return "anonymous"
        return "anonymous";
    }
    
    /**
     * Checks if a status code is suspicious.
     *
     * @param statusCode The HTTP status code
     * @return true if the status code is suspicious, false otherwise
     */
    private boolean isSuspiciousStatusCode(int statusCode) {
        String[] codes = suspiciousStatusCodes.split(",");
        for (String code : codes) {
            if (Integer.parseInt(code.trim()) == statusCode) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Logs the request details.
     *
     * @param request The HTTP request
     * @param correlationId The correlation ID
     * @param clientIp The client IP address
     */
    private void logRequest(HttpServletRequest request, String correlationId, String clientIp) {
        if (logger.isDebugEnabled()) {
            logger.debug("[correlationId={}] Request: {} {} (from {})", 
                    correlationId,
                    request.getMethod(),
                    request.getRequestURI(),
                    clientIp);
        }
    }
    
    /**
     * Logs the response details.
     *
     * @param response The HTTP response
     * @param correlationId The correlation ID
     * @param requestUri The request URI
     * @param method The HTTP method
     * @param clientIp The client IP address
     */
    private void logResponse(HttpServletResponse response, String correlationId, 
                            String requestUri, String method, String clientIp) {
        int status = response.getStatus();
        
        if (logger.isDebugEnabled()) {
            logger.debug("[correlationId={}] Response: {} {} {} (from {}) - status={}", 
                    correlationId,
                    method,
                    requestUri,
                    clientIp,
                    status);
        }
    }
}