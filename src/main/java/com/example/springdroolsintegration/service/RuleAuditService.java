package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.entity.RuleAuditEvent;

import java.util.List;

/**
 * Service interface for rule audit logging.
 * This service handles the creation and retrieval of rule audit events.
 */
public interface RuleAuditService {

    /**
     * Logs a rule audit event.
     *
     * @param event The rule audit event to log
     * @return The logged rule audit event
     */
    RuleAuditEvent logEvent(RuleAuditEvent event);

    /**
     * Creates and logs a rule upload event.
     *
     * @param fileName The name of the uploaded file
     * @param filePath The path to the uploaded file
     * @param version The version of the rule
     * @param username The username of the user who performed the upload
     * @param success Whether the upload was successful
     * @param message Additional message or details
     * @return The logged rule audit event
     */
    RuleAuditEvent logUploadEvent(String fileName, String filePath, String version,
                                 String username, boolean success, String message);

    /**
     * Creates and logs a rule validation event.
     *
     * @param fileName The name of the validated file
     * @param username The username of the user who performed the validation
     * @param success Whether the validation was successful
     * @param message Additional message or details
     * @return The logged rule audit event
     */
    RuleAuditEvent logValidationEvent(String fileName, String username, boolean success, String message);

    /**
     * Creates and logs a rule reload event.
     *
     * @param username The username of the user who performed the reload
     * @param success Whether the reload was successful
     * @param message Additional message or details
     * @return The logged rule audit event
     */
    RuleAuditEvent logReloadEvent(String username, boolean success, String message);

    /**
     * Creates and logs a rule backup event.
     *
     * @param backupPath The path to the backup
     * @param username The username of the user who performed the backup
     * @param success Whether the backup was successful
     * @param message Additional message or details
     * @return The logged rule audit event
     */
    RuleAuditEvent logBackupEvent(String backupPath, String username, boolean success, String message);

    /**
     * Creates and logs a rule rollback event.
     *
     * @param version The version to roll back to
     * @param username The username of the user who performed the rollback
     * @param success Whether the rollback was successful
     * @param message Additional message or details
     * @return The logged rule audit event
     */
    RuleAuditEvent logRollbackEvent(String version, String username, boolean success, String message);

    /**
     * Gets all rule audit events.
     *
     * @return A list of all rule audit events
     */
    List<RuleAuditEvent> getAllEvents();

    /**
     * Gets rule audit events by event type.
     *
     * @param eventType The event type to filter by
     * @return A list of rule audit events of the specified type
     */
    List<RuleAuditEvent> getEventsByType(RuleAuditEvent.EventType eventType);

    /**
     * Gets rule audit events for a specific file.
     *
     * @param fileName The file name to filter by
     * @return A list of rule audit events for the specified file
     */
    List<RuleAuditEvent> getEventsByFileName(String fileName);
}