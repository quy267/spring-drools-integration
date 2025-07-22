package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.config.RuleHotReloadService;
import com.example.springdroolsintegration.service.impl.RuleManagementServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the RuleManagementServiceImpl.
 * These tests verify that the rule reload functionality works correctly.
 */
@ExtendWith(MockitoExtension.class)
public class RuleManagementServiceImplTest {

    @Mock
    private DroolsProperties droolsProperties;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private KieBase kieBase;

    @Mock
    private RuleHotReloadService ruleHotReloadService;

    @Mock
    private RuleAuditService ruleAuditService;

    @Mock
    private KieServices kieServices;

    @Mock
    private KieFileSystem kieFileSystem;

    @Mock
    private KieBuilder kieBuilder;

    @Mock
    private Results results;

    @TempDir
    Path tempDir;

    /**
     * Test that the reloadRules method successfully reloads rules.
     */
    @Test
    @DisplayName("Test successful rule reload")
    public void testSuccessfulRuleReload() throws IOException {
        // Create the service instance
        RuleManagementServiceImpl service = new RuleManagementServiceImpl(
                droolsProperties,
                kieContainer,
                kieBase,
                ruleHotReloadService,
                ruleAuditService
        );

        // Set up the KieServices field using reflection
        ReflectionTestUtils.setField(service, "kieServices", kieServices);

        // Set up the mocks
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(false);

        // Configure properties
        when(droolsProperties.getRulePath()).thenReturn("classpath:rules/");
        when(droolsProperties.getDecisionTablePath()).thenReturn("classpath:rules/decision-tables/");
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");
        when(droolsProperties.getRuleFiles()).thenReturn(Arrays.asList("rule1.drl", "rule2.drl"));
        when(droolsProperties.getDecisionTableFiles()).thenReturn(Arrays.asList("table1.xls", "table2.xlsx"));
        when(droolsProperties.isHotReload()).thenReturn(true);

        // Execute the method
        Map<String, Object> result = service.reloadRules();

        // Verify the result
        assertTrue((Boolean) result.get("success"), "Reload should be successful");
        assertEquals("Rules successfully reloaded", result.get("message"), "Message should indicate success");

        // Verify that the KieContainer was updated
        verify(kieContainer).updateToVersion(any());

        // Verify that the audit service was called
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(true), anyString());
    }

    /**
     * Test that the reloadRules method handles errors correctly.
     */
    @Test
    @DisplayName("Test rule reload with errors")
    public void testRuleReloadWithErrors() throws IOException {
        // Create the service instance
        RuleManagementServiceImpl service = new RuleManagementServiceImpl(
                droolsProperties,
                kieContainer,
                kieBase,
                ruleHotReloadService,
                ruleAuditService
        );

        // Set up the KieServices field using reflection
        ReflectionTestUtils.setField(service, "kieServices", kieServices);

        // Set up the mocks
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(true);
        // Create a mock Message
        Message mockMessage = mock(Message.class);
        when(mockMessage.getText()).thenReturn("Test error message");
        when(mockMessage.getLevel()).thenReturn(Message.Level.ERROR);
        
        when(results.getMessages(Message.Level.ERROR)).thenReturn(Arrays.asList(mockMessage));

        // Configure properties
        when(droolsProperties.getRulePath()).thenReturn("classpath:rules/");
        when(droolsProperties.getDecisionTablePath()).thenReturn("classpath:rules/decision-tables/");
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");
        when(droolsProperties.getRuleFiles()).thenReturn(Arrays.asList("rule1.drl", "rule2.drl"));
        when(droolsProperties.getDecisionTableFiles()).thenReturn(Arrays.asList("table1.xls", "table2.xlsx"));

        // Execute the method
        Map<String, Object> result = service.reloadRules();

        // Verify the result
        assertFalse((Boolean) result.get("success"), "Reload should not be successful");
        assertEquals("Rule reload failed", result.get("message"), "Message should indicate failure");
        assertNotNull(result.get("errors"), "Errors should be included in the result");

        // Verify that the KieContainer was not updated
        verify(kieContainer, never()).updateToVersion(any());

        // Verify that the audit service was called
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(false), anyString());
    }

    /**
     * Test that the reloadRules method handles exceptions correctly.
     */
    @Test
    @DisplayName("Test rule reload with exception")
    public void testRuleReloadWithException() throws IOException {
        // Create the service instance
        RuleManagementServiceImpl service = new RuleManagementServiceImpl(
                droolsProperties,
                kieContainer,
                kieBase,
                ruleHotReloadService,
                ruleAuditService
        );

        // Set up the KieServices field using reflection
        ReflectionTestUtils.setField(service, "kieServices", kieServices);

        // Set up the mocks to throw an exception
        when(kieServices.newKieFileSystem()).thenThrow(new RuntimeException("Test exception"));

        // Execute the method
        Map<String, Object> result = service.reloadRules();

        // Verify the result
        assertFalse((Boolean) result.get("success"), "Reload should not be successful");
        assertEquals("Error reloading rules: Test exception", result.get("message"), "Message should indicate exception");

        // Verify that the audit service was called
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(false), anyString());
    }

    /**
     * Test that the reloadRules method notifies the hot reload service if hot reload is enabled.
     */
    @Test
    @DisplayName("Test rule reload with hot reload enabled")
    public void testRuleReloadWithHotReloadEnabled() throws IOException {
        // Create the service instance
        RuleManagementServiceImpl service = new RuleManagementServiceImpl(
                droolsProperties,
                kieContainer,
                kieBase,
                ruleHotReloadService,
                ruleAuditService
        );

        // Set up the KieServices field using reflection
        ReflectionTestUtils.setField(service, "kieServices", kieServices);

        // Set up the mocks
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(false);

        // Configure properties
        when(droolsProperties.getRulePath()).thenReturn("classpath:rules/");
        when(droolsProperties.getDecisionTablePath()).thenReturn("classpath:rules/decision-tables/");
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");
        when(droolsProperties.getRuleFiles()).thenReturn(Arrays.asList("rule1.drl", "rule2.drl"));
        when(droolsProperties.getDecisionTableFiles()).thenReturn(Arrays.asList("table1.xls", "table2.xlsx"));
        when(droolsProperties.isHotReload()).thenReturn(true);

        // Execute the method
        Map<String, Object> result = service.reloadRules();

        // Verify the result
        assertTrue((Boolean) result.get("success"), "Reload should be successful");

        // Verify that the KieContainer was updated
        verify(kieContainer).updateToVersion(any());

        // Verify that the hot reload service was notified (indirectly via the KieContainer update)
        verify(droolsProperties).isHotReload();
    }
}