package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.entity.RuleAuditEvent;
import com.example.springdroolsintegration.service.impl.RuleAuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RuleAuditServiceImpl.
 */
public class RuleAuditServiceImplTest {

    private RuleAuditServiceImpl ruleAuditService;

    @BeforeEach
    void setUp() {
        ruleAuditService = new RuleAuditServiceImpl();
    }

    @Test
    void testLogEvent() {
        // Arrange
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.UPLOAD,
                "test.xlsx",
                "/path/to/test.xlsx",
                "1.0",
                "testuser",
                true,
                "Test message"
        );

        // Act
        RuleAuditEvent loggedEvent = ruleAuditService.logEvent(event);

        // Assert
        assertNotNull(loggedEvent);
        assertNotNull(loggedEvent.getId());
        assertEquals(1L, loggedEvent.getId());
        assertEquals(RuleAuditEvent.EventType.UPLOAD, loggedEvent.getEventType());
        assertEquals("test.xlsx", loggedEvent.getFileName());
        assertEquals("/path/to/test.xlsx", loggedEvent.getFilePath());
        assertEquals("1.0", loggedEvent.getVersion());
        assertEquals("testuser", loggedEvent.getUsername());
        assertTrue(loggedEvent.isSuccess());
        assertEquals("Test message", loggedEvent.getMessage());
        assertNotNull(loggedEvent.getTimestamp());

        // Verify the event was added to the list
        List<RuleAuditEvent> allEvents = ruleAuditService.getAllEvents();
        assertEquals(1, allEvents.size());
        assertEquals(loggedEvent, allEvents.get(0));
    }

    @Test
    void testLogEventWithExistingId() {
        // Arrange
        RuleAuditEvent event = new RuleAuditEvent(
                RuleAuditEvent.EventType.UPLOAD,
                "test.xlsx",
                "/path/to/test.xlsx",
                "1.0",
                "testuser",
                true,
                "Test message"
        );
        event.setId(100L);

        // Act
        RuleAuditEvent loggedEvent = ruleAuditService.logEvent(event);

        // Assert
        assertNotNull(loggedEvent);
        assertEquals(100L, loggedEvent.getId());
    }

    @Test
    void testLogUploadEvent() {
        // Act
        RuleAuditEvent event = ruleAuditService.logUploadEvent(
                "test.xlsx",
                "/path/to/test.xlsx",
                "1.0",
                "testuser",
                true,
                "Upload successful"
        );

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(RuleAuditEvent.EventType.UPLOAD, event.getEventType());
        assertEquals("test.xlsx", event.getFileName());
        assertEquals("/path/to/test.xlsx", event.getFilePath());
        assertEquals("1.0", event.getVersion());
        assertEquals("testuser", event.getUsername());
        assertTrue(event.isSuccess());
        assertEquals("Upload successful", event.getMessage());
    }

    @Test
    void testLogValidationEvent() {
        // Act
        RuleAuditEvent event = ruleAuditService.logValidationEvent(
                "test.xlsx",
                "testuser",
                true,
                "Validation successful"
        );

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(RuleAuditEvent.EventType.VALIDATE, event.getEventType());
        assertEquals("test.xlsx", event.getFileName());
        assertNull(event.getFilePath());
        assertNull(event.getVersion());
        assertEquals("testuser", event.getUsername());
        assertTrue(event.isSuccess());
        assertEquals("Validation successful", event.getMessage());
    }

    @Test
    void testLogReloadEvent() {
        // Act
        RuleAuditEvent event = ruleAuditService.logReloadEvent(
                "testuser",
                true,
                "Reload successful"
        );

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(RuleAuditEvent.EventType.RELOAD, event.getEventType());
        assertNull(event.getFileName());
        assertNull(event.getFilePath());
        assertNull(event.getVersion());
        assertEquals("testuser", event.getUsername());
        assertTrue(event.isSuccess());
        assertEquals("Reload successful", event.getMessage());
    }

    @Test
    void testLogBackupEvent() {
        // Act
        RuleAuditEvent event = ruleAuditService.logBackupEvent(
                "/path/to/backup",
                "testuser",
                true,
                "Backup successful"
        );

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(RuleAuditEvent.EventType.BACKUP, event.getEventType());
        assertNull(event.getFileName());
        assertEquals("/path/to/backup", event.getFilePath());
        assertNull(event.getVersion());
        assertEquals("testuser", event.getUsername());
        assertTrue(event.isSuccess());
        assertEquals("Backup successful", event.getMessage());
    }

    @Test
    void testLogRollbackEvent() {
        // Act
        RuleAuditEvent event = ruleAuditService.logRollbackEvent(
                "1.0",
                "testuser",
                true,
                "Rollback successful"
        );

        // Assert
        assertNotNull(event);
        assertNotNull(event.getId());
        assertEquals(RuleAuditEvent.EventType.ROLLBACK, event.getEventType());
        assertNull(event.getFileName());
        assertNull(event.getFilePath());
        assertEquals("1.0", event.getVersion());
        assertEquals("testuser", event.getUsername());
        assertTrue(event.isSuccess());
        assertEquals("Rollback successful", event.getMessage());
    }

    @Test
    void testGetAllEvents() {
        // Arrange
        ruleAuditService.logUploadEvent("file1.xlsx", "/path/to/file1.xlsx", "1.0", "user1", true, "Upload 1");
        ruleAuditService.logUploadEvent("file2.xlsx", "/path/to/file2.xlsx", "1.0", "user1", true, "Upload 2");
        ruleAuditService.logValidationEvent("file1.xlsx", "user1", true, "Validation 1");

        // Act
        List<RuleAuditEvent> allEvents = ruleAuditService.getAllEvents();

        // Assert
        assertEquals(3, allEvents.size());
    }

    @Test
    void testGetEventsByType() {
        // Arrange
        ruleAuditService.logUploadEvent("file1.xlsx", "/path/to/file1.xlsx", "1.0", "user1", true, "Upload 1");
        ruleAuditService.logUploadEvent("file2.xlsx", "/path/to/file2.xlsx", "1.0", "user1", true, "Upload 2");
        ruleAuditService.logValidationEvent("file1.xlsx", "user1", true, "Validation 1");
        ruleAuditService.logReloadEvent("user1", true, "Reload 1");

        // Act
        List<RuleAuditEvent> uploadEvents = ruleAuditService.getEventsByType(RuleAuditEvent.EventType.UPLOAD);
        List<RuleAuditEvent> validateEvents = ruleAuditService.getEventsByType(RuleAuditEvent.EventType.VALIDATE);
        List<RuleAuditEvent> reloadEvents = ruleAuditService.getEventsByType(RuleAuditEvent.EventType.RELOAD);
        List<RuleAuditEvent> backupEvents = ruleAuditService.getEventsByType(RuleAuditEvent.EventType.BACKUP);

        // Assert
        assertEquals(2, uploadEvents.size());
        assertEquals(1, validateEvents.size());
        assertEquals(1, reloadEvents.size());
        assertEquals(0, backupEvents.size());
    }

    @Test
    void testGetEventsByFileName() {
        // Arrange
        ruleAuditService.logUploadEvent("file1.xlsx", "/path/to/file1.xlsx", "1.0", "user1", true, "Upload 1");
        ruleAuditService.logUploadEvent("file2.xlsx", "/path/to/file2.xlsx", "1.0", "user1", true, "Upload 2");
        ruleAuditService.logValidationEvent("file1.xlsx", "user1", true, "Validation 1");
        ruleAuditService.logReloadEvent("user1", true, "Reload 1");

        // Act
        List<RuleAuditEvent> file1Events = ruleAuditService.getEventsByFileName("file1.xlsx");
        List<RuleAuditEvent> file2Events = ruleAuditService.getEventsByFileName("file2.xlsx");
        List<RuleAuditEvent> nonExistentFileEvents = ruleAuditService.getEventsByFileName("nonexistent.xlsx");

        // Assert
        assertEquals(2, file1Events.size());
        assertEquals(1, file2Events.size());
        assertEquals(0, nonExistentFileEvents.size());
    }

    @Test
    void testThreadSafety() {
        // This is a basic test to ensure that multiple threads can log events concurrently
        // In a real-world scenario, you might want to use more sophisticated concurrency testing tools

        // Arrange
        final int threadCount = 10;
        final int eventsPerThread = 100;
        Thread[] threads = new Thread[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < eventsPerThread; j++) {
                    ruleAuditService.logUploadEvent(
                            "file" + threadId + "_" + j + ".xlsx",
                            "/path/to/file" + threadId + "_" + j + ".xlsx",
                            "1.0",
                            "user" + threadId,
                            true,
                            "Upload from thread " + threadId
                    );
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread interrupted: " + e.getMessage());
            }
        }

        // Assert
        List<RuleAuditEvent> allEvents = ruleAuditService.getAllEvents();
        assertEquals(threadCount * eventsPerThread, allEvents.size());
    }
}