package com.example.springdroolsintegration.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for the RuleHotReloadService.
 * These tests verify that the rule hot-reloading functionality works correctly.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "app.drools.rule-path=classpath:rules/",
        "app.drools.decision-table-path=classpath:rules/decision-tables/",
        "app.drools.file-extensions=.drl,.xls,.xlsx",
        "app.drools.hot-reload=true",
        "app.drools.hot-reload-interval=1000"
})
@ActiveProfiles("test")
public class RuleHotReloadServiceTest {

    @MockBean
    private KieContainer kieContainer;

    @MockBean
    private ResourceLoader resourceLoader;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DroolsProperties droolsProperties;

    @TempDir
    Path tempDir;

    /**
     * Test that the RuleHotReloadService is properly configured.
     */
    @Test
    @DisplayName("Test RuleHotReloadService configuration")
    public void testRuleHotReloadServiceConfiguration() {
        // Create a new RuleHotReloadService with mocked dependencies
        RuleHotReloadService service = new RuleHotReloadService(
                droolsProperties,
                kieContainer,
                resourceLoader,
                eventPublisher
        );

        // Verify that the service is not null
        assertNotNull(service, "RuleHotReloadService should not be null");

        // Verify that the hot-reload properties are correctly configured
        assertTrue(droolsProperties.isHotReload(), "Hot reload should be enabled");
        assertEquals(1000, droolsProperties.getHotReloadInterval(), "Hot reload interval should be 1000ms");
    }

    /**
     * Test that the RuleHotReloadService properly handles file changes.
     */
    @Test
    @DisplayName("Test rule file change detection")
    public void testRuleFileChangeDetection() throws Exception {
        // Override the external rule path to use the temp directory
        when(droolsProperties.getExternalRulePath()).thenReturn(tempDir.toString());
        when(droolsProperties.isScanSubdirectories()).thenReturn(true);
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");

        // Create a new RuleHotReloadService with mocked dependencies
        RuleHotReloadService service = new RuleHotReloadService(
                droolsProperties,
                kieContainer,
                resourceLoader,
                eventPublisher
        );

        // Create a test rule file
        File ruleFile = tempDir.resolve("test-rule.drl").toFile();
        Files.write(ruleFile.toPath(), "package com.example.rules;\n\nrule \"Test Rule\"\nwhen\nthen\nend".getBytes());

        // Wait for the file watcher to detect the change
        TimeUnit.SECONDS.sleep(2);

        // Verify that the KieContainer was updated
        verify(kieContainer, timeout(5000).atLeastOnce()).updateToVersion(any());

        // Verify that an event was published
        verify(eventPublisher, timeout(5000).atLeastOnce()).publishEvent(any(RuleHotReloadService.RuleReloadEvent.class));
    }

    /**
     * Test that the RuleHotReloadService properly handles multiple file changes.
     */
    @Test
    @DisplayName("Test multiple rule file changes")
    public void testMultipleRuleFileChanges() throws Exception {
        // Override the external rule path to use the temp directory
        when(droolsProperties.getExternalRulePath()).thenReturn(tempDir.toString());
        when(droolsProperties.isScanSubdirectories()).thenReturn(true);
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");

        // Create a new RuleHotReloadService with mocked dependencies
        RuleHotReloadService service = new RuleHotReloadService(
                droolsProperties,
                kieContainer,
                resourceLoader,
                eventPublisher
        );

        // Create multiple test rule files
        File ruleFile1 = tempDir.resolve("test-rule1.drl").toFile();
        Files.write(ruleFile1.toPath(), "package com.example.rules;\n\nrule \"Test Rule 1\"\nwhen\nthen\nend".getBytes());

        // Wait for the file watcher to detect the first change
        TimeUnit.SECONDS.sleep(2);

        // Create a second rule file
        File ruleFile2 = tempDir.resolve("test-rule2.drl").toFile();
        Files.write(ruleFile2.toPath(), "package com.example.rules;\n\nrule \"Test Rule 2\"\nwhen\nthen\nend".getBytes());

        // Wait for the file watcher to detect the second change
        TimeUnit.SECONDS.sleep(2);

        // Verify that the KieContainer was updated at least twice
        verify(kieContainer, timeout(5000).atLeast(2)).updateToVersion(any());

        // Verify that events were published
        verify(eventPublisher, timeout(5000).atLeast(2)).publishEvent(any(RuleHotReloadService.RuleReloadEvent.class));
    }

    /**
     * Test that the RuleHotReloadService properly handles file deletions.
     */
    @Test
    @DisplayName("Test rule file deletion")
    public void testRuleFileDeletion() throws Exception {
        // Override the external rule path to use the temp directory
        when(droolsProperties.getExternalRulePath()).thenReturn(tempDir.toString());
        when(droolsProperties.isScanSubdirectories()).thenReturn(true);
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");

        // Create a new RuleHotReloadService with mocked dependencies
        RuleHotReloadService service = new RuleHotReloadService(
                droolsProperties,
                kieContainer,
                resourceLoader,
                eventPublisher
        );

        // Create a test rule file
        File ruleFile = tempDir.resolve("test-rule-delete.drl").toFile();
        Files.write(ruleFile.toPath(), "package com.example.rules;\n\nrule \"Test Rule Delete\"\nwhen\nthen\nend".getBytes());

        // Wait for the file watcher to detect the creation
        TimeUnit.SECONDS.sleep(2);

        // Delete the rule file
        Files.delete(ruleFile.toPath());

        // Wait for the file watcher to detect the deletion
        TimeUnit.SECONDS.sleep(2);

        // Verify that the KieContainer was updated at least twice (once for creation, once for deletion)
        verify(kieContainer, timeout(5000).atLeast(2)).updateToVersion(any());

        // Verify that events were published
        verify(eventPublisher, timeout(5000).atLeast(2)).publishEvent(any(RuleHotReloadService.RuleReloadEvent.class));
    }

    /**
     * Test that the RuleHotReloadService properly handles concurrent file changes.
     */
    @Test
    @DisplayName("Test concurrent rule file changes")
    public void testConcurrentRuleFileChanges() throws Exception {
        // Override the external rule path to use the temp directory
        when(droolsProperties.getExternalRulePath()).thenReturn(tempDir.toString());
        when(droolsProperties.isScanSubdirectories()).thenReturn(true);
        when(droolsProperties.getFileExtensions()).thenReturn(".drl,.xls,.xlsx");

        // Create a new RuleHotReloadService with mocked dependencies
        RuleHotReloadService service = new RuleHotReloadService(
                droolsProperties,
                kieContainer,
                resourceLoader,
                eventPublisher
        );

        // Create multiple test rule files concurrently
        for (int i = 0; i < 5; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    File ruleFile = tempDir.resolve("concurrent-rule" + index + ".drl").toFile();
                    Files.write(ruleFile.toPath(), 
                            ("package com.example.rules;\n\nrule \"Concurrent Rule " + index + "\"\nwhen\nthen\nend").getBytes());
                } catch (IOException e) {
                    fail("Failed to create rule file: " + e.getMessage());
                }
            }).start();
        }

        // Wait for the file watcher to detect all changes
        TimeUnit.SECONDS.sleep(5);

        // Verify that the KieContainer was updated at least once
        verify(kieContainer, timeout(5000).atLeastOnce()).updateToVersion(any());

        // Verify that at least one event was published
        verify(eventPublisher, timeout(5000).atLeastOnce()).publishEvent(any(RuleHotReloadService.RuleReloadEvent.class));
    }
}