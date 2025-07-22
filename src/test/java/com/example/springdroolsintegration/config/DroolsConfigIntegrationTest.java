package com.example.springdroolsintegration.config;

import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Drools configuration.
 * These tests verify that the Drools rule engine is properly configured and can execute rules.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "app.drools.rule-path=classpath:rules/",
        "app.drools.decision-table-path=classpath:rules/decision-tables/",
        "app.drools.file-extensions=.drl,.xls,.xlsx",
        "app.drools.hot-reload=false"
})
@ActiveProfiles("test")
public class DroolsConfigIntegrationTest {

    @Autowired
    private KieContainer kieContainer;
    
    @Autowired
    private KieBase kieBase;
    
    @Autowired
    private DroolsProperties droolsProperties;
    
    /**
     * Test that the KieContainer is properly configured and can be autowired.
     */
    @Test
    public void testKieContainerConfiguration() {
        // Verify that KieContainer is not null
        assertNotNull(kieContainer, "KieContainer should not be null");
        
        // Verify that KieBase is not null
        assertNotNull(kieBase, "KieBase should not be null");
        
        // Verify that a KieSession can be created
        KieSession kieSession = kieContainer.newKieSession();
        assertNotNull(kieSession, "KieSession should not be null");
        
        // Clean up
        kieSession.dispose();
    }
    
    /**
     * Test that the DroolsProperties are properly configured.
     */
    @Test
    public void testDroolsProperties() {
        // Verify that properties are loaded correctly
        assertEquals("classpath:rules/", droolsProperties.getRulePath(), "Rule path should match");
        assertEquals("classpath:rules/decision-tables/", droolsProperties.getDecisionTablePath(), "Decision table path should match");
        assertEquals(".drl,.xls,.xlsx", droolsProperties.getFileExtensions(), "File extensions should match");
        assertFalse(droolsProperties.isHotReload(), "Hot reload should be disabled in test");
    }
    
    /**
     * Test that a KieSession can be created and used.
     * This is a basic test that verifies the KieSession can be created and disposed.
     */
    @Test
    public void testKieSessionCreation() {
        // Create a new KieSession
        KieSession kieSession = kieBase.newKieSession();
        
        // Verify that the KieSession is not null
        assertNotNull(kieSession, "KieSession should not be null");
        
        // Verify that the KieSession has the expected ID
        assertTrue(kieSession.getId() > 0, "KieSession ID should be positive");
        
        // Clean up
        kieSession.dispose();
    }
    
    /**
     * Test that the KieContainer can be used to create a KieSession with a specific name.
     */
    @Test
    public void testNamedKieSessionCreation() {
        // Create a new KieSession with the default name
        KieSession kieSession = kieContainer.newKieSession(droolsProperties.getKieSessionName());
        
        // Verify that the KieSession is not null
        assertNotNull(kieSession, "Named KieSession should not be null");
        
        // Clean up
        kieSession.dispose();
    }
    
    /**
     * Test that the KieBase contains the expected packages.
     * This test verifies that the KieBase is properly configured with the expected packages.
     */
    @Test
    public void testKieBasePackages() {
        // Get the collection of package names from the KieBase
        java.util.Collection<String> packageNames = kieBase.getKiePackages().stream()
                .map(pkg -> pkg.getName())
                .collect(java.util.stream.Collectors.toList());
        
        // Verify that the KieBase contains at least the default package
        assertTrue(packageNames.contains("defaultpkg"), "KieBase should contain the default package");
        
        // Log the packages for debugging
        System.out.println("KieBase packages: " + packageNames);
    }
    
    /**
     * Test that rule execution works correctly.
     * This is a placeholder test that would normally execute a simple rule.
     * In a real implementation, you would create a test rule and verify its execution.
     */
    @Test
    public void testRuleExecution() {
        // This is a placeholder for a real rule execution test
        // In a real test, you would:
        // 1. Create a fact object
        // 2. Insert it into a KieSession
        // 3. Fire the rules
        // 4. Verify the expected outcome
        
        // For now, we'll just verify that a KieSession can be created and disposed
        KieSession kieSession = kieBase.newKieSession();
        assertNotNull(kieSession, "KieSession should not be null");
        kieSession.dispose();
    }
}