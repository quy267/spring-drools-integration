package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.service.impl.SecurityAuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityAuditServiceImplTest {

    @Spy
    @InjectMocks
    private SecurityAuditServiceImpl securityAuditService;

    @Mock
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        // Set the logger field using reflection
        ReflectionTestUtils.setField(securityAuditService, "logger", mockLogger);
        
        // Enable audit logging
        ReflectionTestUtils.setField(securityAuditService, "auditEnabled", true);
        ReflectionTestUtils.setField(securityAuditService, "includeTimestamp", true);
        ReflectionTestUtils.setField(securityAuditService, "includeCorrelationId", true);
    }

    @Test
    void testLogAuthenticationEvent_Success() {
        // Arrange
        String username = "testUser";
        boolean success = true;
        String details = "Login successful";

        // Act
        securityAuditService.logAuthenticationEvent(username, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Authentication attempt by user 'testUser': SUCCESS"), any(Map.class));
    }

    @Test
    void testLogAuthenticationEvent_Failure() {
        // Arrange
        String username = "testUser";
        boolean success = false;
        String details = "Invalid password";

        // Act
        securityAuditService.logAuthenticationEvent(username, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Authentication attempt by user 'testUser': FAILURE"), any(Map.class));
    }

    @Test
    void testLogAccessEvent_Success() {
        // Arrange
        String username = "testUser";
        String resource = "/api/rules";
        String action = "READ";
        boolean success = true;
        String details = "Access granted";

        // Act
        securityAuditService.logAccessEvent(username, resource, action, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Access attempt by user 'testUser' to resource '/api/rules' for action 'READ': SUCCESS"), any(Map.class));
    }

    @Test
    void testLogAccessEvent_Failure() {
        // Arrange
        String username = "testUser";
        String resource = "/api/admin";
        String action = "WRITE";
        boolean success = false;
        String details = "Access denied";

        // Act
        securityAuditService.logAccessEvent(username, resource, action, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Access attempt by user 'testUser' to resource '/api/admin' for action 'WRITE': FAILURE"), any(Map.class));
    }

    @Test
    void testLogFileEvent_Success() {
        // Arrange
        String username = "testUser";
        String filename = "rules.xlsx";
        String operation = "UPLOAD";
        boolean success = true;
        String details = "File uploaded successfully";

        // Act
        securityAuditService.logFileEvent(username, filename, operation, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] File operation 'UPLOAD' by user 'testUser' on file 'rules.xlsx': SUCCESS"), any(Map.class));
    }

    @Test
    void testLogFileEvent_Failure() {
        // Arrange
        String username = "testUser";
        String filename = "rules.xlsx";
        String operation = "DELETE";
        boolean success = false;
        String details = "Permission denied";

        // Act
        securityAuditService.logFileEvent(username, filename, operation, success, details);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] File operation 'DELETE' by user 'testUser' on file 'rules.xlsx': FAILURE"), any(Map.class));
    }

    @Test
    void testLogSecurityViolation() {
        // Arrange
        String username = "testUser";
        String violationType = "BRUTE_FORCE";
        String details = "Multiple failed login attempts";
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("attemptCount", 5);

        // Act
        securityAuditService.logSecurityViolation(username, violationType, details, metadata);

        // Assert
        verify(mockLogger).warn(contains("[SECURITY_AUDIT] Security violation of type 'BRUTE_FORCE' by user 'testUser'"), any(Map.class));
    }

    @Test
    void testLogSecurityViolation_NullMetadata() {
        // Arrange
        String username = "testUser";
        String violationType = "BRUTE_FORCE";
        String details = "Multiple failed login attempts";

        // Act
        securityAuditService.logSecurityViolation(username, violationType, details, null);

        // Assert
        verify(mockLogger).warn(contains("[SECURITY_AUDIT] Security violation of type 'BRUTE_FORCE' by user 'testUser'"), any(Map.class));
    }

    @Test
    void testLogSecurityEvent() {
        // Arrange
        String eventType = "PASSWORD_CHANGE";
        String username = "testUser";
        String details = "Password changed successfully";
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("forced", false);

        // Act
        securityAuditService.logSecurityEvent(eventType, username, details, metadata);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Security event of type 'PASSWORD_CHANGE' by user 'testUser'"), any(Map.class));
    }

    @Test
    void testLogSecurityEvent_NullMetadata() {
        // Arrange
        String eventType = "PASSWORD_CHANGE";
        String username = "testUser";
        String details = "Password changed successfully";

        // Act
        securityAuditService.logSecurityEvent(eventType, username, details, null);

        // Assert
        verify(mockLogger).info(contains("[SECURITY_AUDIT] Security event of type 'PASSWORD_CHANGE' by user 'testUser'"), any(Map.class));
    }

    @Test
    void testAuditDisabled() {
        // Arrange
        ReflectionTestUtils.setField(securityAuditService, "auditEnabled", false);
        String username = "testUser";
        boolean success = true;
        String details = "Login successful";

        // Act
        securityAuditService.logAuthenticationEvent(username, success, details);
        securityAuditService.logAccessEvent(username, "/api/rules", "READ", success, details);
        securityAuditService.logFileEvent(username, "rules.xlsx", "UPLOAD", success, details);
        securityAuditService.logSecurityViolation(username, "BRUTE_FORCE", details, null);
        securityAuditService.logSecurityEvent("PASSWORD_CHANGE", username, details, null);

        // Assert - no logging should occur when audit is disabled
        verifyNoInteractions(mockLogger);
    }
}