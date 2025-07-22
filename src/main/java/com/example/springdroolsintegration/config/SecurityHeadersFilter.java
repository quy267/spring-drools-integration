package com.example.springdroolsintegration.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to add security headers to all responses.
 * This filter adds headers like Content-Security-Policy, X-Content-Type-Options, X-Frame-Options, etc.
 * to improve the security of the application.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityHeadersFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);

    @Value("${app.security.headers.content-security-policy:default-src 'self'; script-src 'self'; object-src 'none'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; font-src 'self'; frame-ancestors 'none'; base-uri 'self'}")
    private String contentSecurityPolicy;

    @Value("${app.security.headers.x-content-type-options:nosniff}")
    private String xContentTypeOptions;

    @Value("${app.security.headers.x-frame-options:DENY}")
    private String xFrameOptions;

    @Value("${app.security.headers.x-xss-protection:1; mode=block}")
    private String xXssProtection;

    @Value("${app.security.headers.strict-transport-security:max-age=31536000; includeSubDomains}")
    private String strictTransportSecurity;

    @Value("${app.security.headers.referrer-policy:strict-origin-when-cross-origin}")
    private String referrerPolicy;

    @Value("${app.security.headers.cache-control:no-store, no-cache, must-revalidate, max-age=0}")
    private String cacheControl;

    @Value("${app.security.headers.permissions-policy:accelerometer=(), camera=(), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), payment=(), usb=()}")
    private String permissionsPolicy;

    @Value("${app.security.headers.enabled:true}")
    private boolean enabled;

    /**
     * Adds security headers to the response.
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filter chain
     * @throws IOException If an I/O error occurs
     * @throws ServletException If a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Skip adding headers if disabled
        if (!enabled) {
            chain.doFilter(request, response);
            return;
        }

        if (response instanceof HttpServletResponse httpResponse) {
            // Add security headers
            httpResponse.setHeader("Content-Security-Policy", contentSecurityPolicy);
            httpResponse.setHeader("X-Content-Type-Options", xContentTypeOptions);
            httpResponse.setHeader("X-Frame-Options", xFrameOptions);
            httpResponse.setHeader("X-XSS-Protection", xXssProtection);
            httpResponse.setHeader("Strict-Transport-Security", strictTransportSecurity);
            httpResponse.setHeader("Referrer-Policy", referrerPolicy);
            httpResponse.setHeader("Cache-Control", cacheControl);
            httpResponse.setHeader("Permissions-Policy", permissionsPolicy);
            
            // Prevent caching of sensitive information
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");
            
            // Remove potentially dangerous headers
            httpResponse.setHeader("Server", "");
            httpResponse.setHeader("X-Powered-By", "");
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Logs the security headers configuration on initialization.
     */
    @Override
    public void init(jakarta.servlet.FilterConfig filterConfig) throws ServletException {
        if (enabled) {
            logger.info("Security headers filter initialized with: CSP={}, X-Frame-Options={}, HSTS={}",
                    contentSecurityPolicy, xFrameOptions, strictTransportSecurity);
        } else {
            logger.warn("Security headers filter is disabled");
        }
    }
}