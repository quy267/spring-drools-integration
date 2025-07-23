package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.config.RuleHotReloadService;
import com.example.springdroolsintegration.service.impl.RuleManagementServiceImpl;
import com.example.springdroolsintegration.util.FileValidator;
import com.example.springdroolsintegration.util.SecureFileStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

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
    private FileValidator fileValidator;

    @Mock
    private SecureFileStorage secureFileStorage;

    @Mock
    private KieServices kieServices;

    @Mock
    private KieFileSystem kieFileSystem;

    @Mock
    private KieBuilder kieBuilder;

    @Mock
    private Results results;

    @Mock
    private KieRepository kieRepository;

    @Mock
    private ReleaseId releaseId;

    @TempDir
    Path tempDir;

    private RuleManagementServiceImpl createService() {
        RuleManagementServiceImpl service = new RuleManagementServiceImpl(
                droolsProperties,
                kieContainer,
                kieBase,
                ruleHotReloadService,
                ruleAuditService,
                fileValidator,
                secureFileStorage
        );
        
        // Set up the KieServices field using reflection
        ReflectionTestUtils.setField(service, "kieServices", kieServices);
        return service;
    }

    private void setupBasicMocks() {
        lenient().when(droolsProperties.getRulePath()).thenReturn("classpath:rules/");
        lenient().when(droolsProperties.getDecisionTablePath()).thenReturn("classpath:rules/decision-tables/");
        lenient().when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");
        lenient().when(droolsProperties.getRuleFiles()).thenReturn(Arrays.asList("rule1.drl", "rule2.drl"));
        lenient().when(droolsProperties.getDecisionTableFiles()).thenReturn(Arrays.asList("table1.xls", "table2.xlsx"));
    }

    /**
     * Test that the reloadRules method successfully reloads rules.
     */
    @Test
    @DisplayName("Test successful rule reload")
    public void testSuccessfulRuleReload() throws IOException {
        // Setup
        RuleManagementServiceImpl service = createService();
        setupBasicMocks();
        
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieServices.getRepository()).thenReturn(kieRepository);
        when(kieRepository.getDefaultReleaseId()).thenReturn(releaseId);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(false);
        when(droolsProperties.isHotReload()).thenReturn(true);

        // Execute
        Map<String, Object> result = service.reloadRules();

        // Verify
        assertTrue((Boolean) result.get("success"), "Reload should be successful");
        assertEquals("Rules successfully reloaded", result.get("message"), "Message should indicate success");
        verify(kieContainer).updateToVersion(any());
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(true), anyString());
    }

    /**
     * Test that the reloadRules method handles errors correctly.
     */
    @Test
    @DisplayName("Test rule reload with errors")
    public void testRuleReloadWithErrors() throws IOException {
        // Setup
        RuleManagementServiceImpl service = createService();
        setupBasicMocks();
        
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(true);
        
        Message mockMessage = mock(Message.class);
        when(mockMessage.getText()).thenReturn("Test error message");
        when(results.getMessages(Message.Level.ERROR)).thenReturn(Arrays.asList(mockMessage));

        // Execute
        Map<String, Object> result = service.reloadRules();

        // Verify
        assertFalse((Boolean) result.get("success"), "Reload should not be successful");
        assertEquals("Rule reload failed", result.get("message"), "Message should indicate failure");
        assertNotNull(result.get("errors"), "Errors should be included in the result");
        verify(kieContainer, never()).updateToVersion(any());
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(false), anyString());
    }

    /**
     * Test that the reloadRules method handles exceptions correctly.
     */
    @Test
    @DisplayName("Test rule reload with exception")
    public void testRuleReloadWithException() throws IOException {
        // Setup
        RuleManagementServiceImpl service = createService();
        setupBasicMocks();
        
        when(kieServices.newKieFileSystem()).thenThrow(new RuntimeException("Test exception"));

        // Execute
        Map<String, Object> result = service.reloadRules();

        // Verify
        assertFalse((Boolean) result.get("success"), "Reload should not be successful");
        assertEquals("Error reloading rules: Test exception", result.get("message"), "Message should indicate exception");
        verify(ruleAuditService).logReloadEvent(eq("system"), eq(false), anyString());
    }

    /**
     * Test that the reloadRules method notifies the hot reload service if hot reload is enabled.
     */
    @Test
    @DisplayName("Test rule reload with hot reload enabled")
    public void testRuleReloadWithHotReloadEnabled() throws IOException {
        // Setup
        RuleManagementServiceImpl service = createService();
        setupBasicMocks();
        
        when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
        when(kieServices.newKieBuilder(kieFileSystem)).thenReturn(kieBuilder);
        when(kieServices.getRepository()).thenReturn(kieRepository);
        when(kieRepository.getDefaultReleaseId()).thenReturn(releaseId);
        when(kieBuilder.getResults()).thenReturn(results);
        when(results.hasMessages(Message.Level.ERROR)).thenReturn(false);
        when(droolsProperties.isHotReload()).thenReturn(true);

        // Execute
        Map<String, Object> result = service.reloadRules();

        // Verify
        assertTrue((Boolean) result.get("success"), "Reload should be successful");
        verify(kieContainer).updateToVersion(any());
        verify(droolsProperties).isHotReload();
    }
}