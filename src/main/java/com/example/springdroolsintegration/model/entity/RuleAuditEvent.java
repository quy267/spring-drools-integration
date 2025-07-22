package com.example.springdroolsintegration.model.entity;

import java.time.LocalDateTime;

/**
 * Entity class for rule audit events.
 * This class represents an audit log entry for rule management operations.
 */
public class RuleAuditEvent {
    
    /**
     * Enum for rule audit event types.
     */
    public enum EventType {
        UPLOAD,
        VALIDATE,
        RELOAD,
        BACKUP,
        ROLLBACK
    }
    
    private Long id;
    private EventType eventType;
    private String fileName;
    private String filePath;
    private String version;
    private String username;
    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    
    /**
     * Default constructor.
     */
    public RuleAuditEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with event type.
     *
     * @param eventType The type of event
     */
    public RuleAuditEvent(EventType eventType) {
        this();
        this.eventType = eventType;
    }
    
    /**
     * Constructor with event type and success flag.
     *
     * @param eventType The type of event
     * @param success Whether the event was successful
     */
    public RuleAuditEvent(EventType eventType, boolean success) {
        this(eventType);
        this.success = success;
    }
    
    /**
     * Constructor with all fields except id.
     *
     * @param eventType The type of event
     * @param fileName The name of the file
     * @param filePath The path to the file
     * @param version The version of the rule
     * @param username The username of the user who performed the action
     * @param success Whether the event was successful
     * @param message Additional message or details
     */
    public RuleAuditEvent(EventType eventType, String fileName, String filePath, String version,
                         String username, boolean success, String message) {
        this(eventType, success);
        this.fileName = fileName;
        this.filePath = filePath;
        this.version = version;
        this.username = username;
        this.message = message;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "RuleAuditEvent{" +
                "id=" + id +
                ", eventType=" + eventType +
                ", fileName='" + fileName + '\'' +
                ", version='" + version + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}