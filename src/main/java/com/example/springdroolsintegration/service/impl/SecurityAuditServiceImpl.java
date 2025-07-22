package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.service.SecurityAuditService;
import com.example.springdroolsintegration.util.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the SecurityAuditService interface.
 * This class handles the logging of security events to the application log.
 */
@Service
public class SecurityAuditServiceImpl implements SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger("security-audit");
    private static final String AUDIT_MARKER = "[SECURITY_AUDIT]";

    @Value("${app.security.audit.enabled:true}")
    private boolean auditEnabled;

    @Value("${app.security.audit.include-timestamp:true}")
    private boolean includeTimestamp;

    @Value("${app.security.audit.include-correlation-id:true}")
    private boolean includeCorrelationId;

    /**
     * Logs an authentication event.
     *
     * @param username The username
     * @param success Whether the authentication was successful
     * @param details Additional details about the authentication event
     */
    @Override
    public void logAuthenticationEvent(String username, boolean success, String details) {
        if (!auditEnabled) return;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("eventType", "AUTHENTICATION");
        metadata.put("success", success);

        String message = String.format("%s Authentication attempt by user '%s': %s - %s",
                AUDIT_MARKER, username, success ? "SUCCESS" : "FAILURE", details);

        logEvent(message, metadata);
    }

    /**
     * Logs an access event.
     *
     * @param username The username
     * @param resource The resource being accessed
     * @param action The action being performed
     * @param success Whether the access was successful
     * @param details Additional details about the access event
     */
    @Override
    public void logAccessEvent(String username, String resource, String action, boolean success, String details) {
        if (!auditEnabled) return;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("eventType", "ACCESS");
        metadata.put("resource", resource);
        metadata.put("action", action);
        metadata.put("success", success);

        String message = String.format("%s Access attempt by user '%s' to resource '%s' for action '%s': %s - %s",
                AUDIT_MARKER, username, resource, action, success ? "SUCCESS" : "FAILURE", details);

        logEvent(message, metadata);
    }

    /**
     * Logs a file operation event.
     *
     * @param username The username
     * @param filename The filename
     * @param operation The operation being performed
     * @param success Whether the operation was successful
     * @param details Additional details about the file operation event
     */
    @Override
    public void logFileEvent(String username, String filename, String operation, boolean success, String details) {
        if (!auditEnabled) return;

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("eventType", "FILE_OPERATION");
        metadata.put("filename", filename);
        metadata.put("operation", operation);
        metadata.put("success", success);

        String message = String.format("%s File operation '%s' by user '%s' on file '%s': %s - %s",
                AUDIT_MARKER, operation, username, filename, success ? "SUCCESS" : "FAILURE", details);

        logEvent(message, metadata);
    }

    /**
     * Logs a security violation event.
     *
     * @param username The username
     * @param violationType The type of violation
     * @param details Additional details about the violation event
     * @param metadata Additional metadata about the violation event
     */
    @Override
    public void logSecurityViolation(String username, String violationType, String details, Map<String, Object> metadata) {
        if (!auditEnabled) return;

        if (metadata == null) {
            metadata = new HashMap<>();
        }
        
        metadata.put("eventType", "SECURITY_VIOLATION");
        metadata.put("violationType", violationType);

        String message = String.format("%s Security violation of type '%s' by user '%s': %s",
                AUDIT_MARKER, violationType, username, details);

        // Security violations are always logged at WARN level
        logEvent(message, metadata, true);
    }

    /**
     * Logs a general security event.
     *
     * @param eventType The type of event
     * @param username The username
     * @param details Additional details about the event
     * @param metadata Additional metadata about the event
     */
    @Override
    public void logSecurityEvent(String eventType, String username, String details, Map<String, Object> metadata) {
        if (!auditEnabled) return;

        if (metadata == null) {
            metadata = new HashMap<>();
        }
        
        metadata.put("eventType", eventType);

        String message = String.format("%s Security event of type '%s' by user '%s': %s",
                AUDIT_MARKER, eventType, username, details);

        logEvent(message, metadata);
    }

    /**
     * Logs an event with the given message and metadata.
     *
     * @param message The log message
     * @param metadata The metadata to include in the log
     */
    private void logEvent(String message, Map<String, Object> metadata) {
        logEvent(message, metadata, false);
    }

    /**
     * Logs an event with the given message and metadata.
     *
     * @param message The log message
     * @param metadata The metadata to include in the log
     * @param isViolation Whether this is a security violation (logged at WARN level)
     */
    private void logEvent(String message, Map<String, Object> metadata, boolean isViolation) {
        // Add timestamp if configured
        if (includeTimestamp) {
            metadata.put("timestamp", Instant.now().toString());
        }

        // Add correlation ID if configured
        if (includeCorrelationId) {
            String correlationId = LoggingUtils.getOrCreateCorrelationId();
            metadata.put("correlationId", correlationId);
        }

        // Add client IP if available
        String clientIp = LoggingUtils.getClientIp();
        if (clientIp != null) {
            metadata.put("clientIp", clientIp);
        }

        // Log at appropriate level
        if (isViolation) {
            logger.warn("{} {}", message, metadata);
        } else {
            logger.info("{} {}", message, metadata);
        }
    }
}