package com.example.springdroolsintegration.service;

import java.util.Map;

/**
 * Service interface for security audit logging.
 * This interface defines methods for logging security-relevant events.
 */
public interface SecurityAuditService {

    /**
     * Logs an authentication event.
     *
     * @param username The username
     * @param success Whether the authentication was successful
     * @param details Additional details about the authentication event
     */
    void logAuthenticationEvent(String username, boolean success, String details);

    /**
     * Logs an access event.
     *
     * @param username The username
     * @param resource The resource being accessed
     * @param action The action being performed
     * @param success Whether the access was successful
     * @param details Additional details about the access event
     */
    void logAccessEvent(String username, String resource, String action, boolean success, String details);

    /**
     * Logs a file operation event.
     *
     * @param username The username
     * @param filename The filename
     * @param operation The operation being performed
     * @param success Whether the operation was successful
     * @param details Additional details about the file operation event
     */
    void logFileEvent(String username, String filename, String operation, boolean success, String details);

    /**
     * Logs a security violation event.
     *
     * @param username The username
     * @param violationType The type of violation
     * @param details Additional details about the violation event
     * @param metadata Additional metadata about the violation event
     */
    void logSecurityViolation(String username, String violationType, String details, Map<String, Object> metadata);

    /**
     * Logs a general security event.
     *
     * @param eventType The type of event
     * @param username The username
     * @param details Additional details about the event
     * @param metadata Additional metadata about the event
     */
    void logSecurityEvent(String eventType, String username, String details, Map<String, Object> metadata);
}