package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.model.entity.RuleAuditEvent;
import com.example.springdroolsintegration.service.RuleAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of the RuleAuditService interface.
 * This service handles the creation and retrieval of rule audit events.
 * Note: This is an in-memory implementation for demonstration purposes.
 * In a production environment, events would be stored in a database.
 */
@Service
public class RuleAuditServiceImpl implements RuleAuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleAuditServiceImpl.class);
    
    // Thread-safe list to store audit events
    private final List<RuleAuditEvent> auditEvents = new CopyOnWriteArrayList<>();
    
    // ID generator for audit events
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public RuleAuditEvent logEvent(RuleAuditEvent event) {
        // Set ID if not already set
        if (event.getId() == null) {
            event.setId(idGenerator.getAndIncrement());
        }
        
        // Add event to list
        auditEvents.add(event);
        
        // Log event
        logger.info("Rule audit event logged: {}", event);
        
        return event;
    }
    
    @Override
    public RuleAuditEvent logUploadEvent(String fileName, String filePath, String version,
                                        String username, boolean success, String message) {
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.UPLOAD,
                fileName,
                filePath,
                version,
                username,
                success,
                message
        );
        
        return logEvent(event);
    }
    
    @Override
    public RuleAuditEvent logValidationEvent(String fileName, String username, boolean success, String message) {
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.VALIDATE,
                fileName,
                null,
                null,
                username,
                success,
                message
        );
        
        return logEvent(event);
    }
    
    @Override
    public RuleAuditEvent logReloadEvent(String username, boolean success, String message) {
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.RELOAD,
                null,
                null,
                null,
                username,
                success,
                message
        );
        
        return logEvent(event);
    }
    
    @Override
    public RuleAuditEvent logBackupEvent(String backupPath, String username, boolean success, String message) {
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.BACKUP,
                null,
                backupPath,
                null,
                username,
                success,
                message
        );
        
        return logEvent(event);
    }
    
    @Override
    public RuleAuditEvent logRollbackEvent(String version, String username, boolean success, String message) {
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.ROLLBACK,
                null,
                null,
                version,
                username,
                success,
                message
        );
        
        return logEvent(event);
    }
    
    @Override
    public List<RuleAuditEvent> getAllEvents() {
        // Return a copy of the list to prevent modification
        return new ArrayList<>(auditEvents);
    }
    
    @Override
    public List<RuleAuditEvent> getEventsByType(RuleAuditEvent.EventType eventType) {
        return auditEvents.stream()
                .filter(event -> event.getEventType() == eventType)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RuleAuditEvent> getEventsByFileName(String fileName) {
        return auditEvents.stream()
                .filter(event -> fileName.equals(event.getFileName()))
                .collect(Collectors.toList());
    }
}