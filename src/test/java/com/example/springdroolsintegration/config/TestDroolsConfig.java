package com.example.springdroolsintegration.config;

import com.example.springdroolsintegration.service.RuleCompilationCacheService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for Drools components.
 * This configuration provides mock or simplified implementations of Drools beans
 * to prevent ApplicationContext loading failures during testing.
 */
@TestConfiguration
@Profile("test")
public class TestDroolsConfig {

    /**
     * Provides a mock KieContainer for testing.
     * This prevents the complex Drools initialization that can cause ApplicationContext failures.
     */
    @Bean
    @Primary
    public KieContainer kieContainer() {
        KieContainer mockContainer = Mockito.mock(KieContainer.class);
        
        // Create a simple KieBase mock
        KieBase mockKieBase = Mockito.mock(KieBase.class);
        Mockito.when(mockContainer.getKieBase()).thenReturn(mockKieBase);
        Mockito.when(mockContainer.getKieBase(Mockito.anyString())).thenReturn(mockKieBase);
        
        // Create a simple KieSession mock
        KieSession mockSession = Mockito.mock(KieSession.class);
        Mockito.when(mockContainer.newKieSession()).thenReturn(mockSession);
        Mockito.when(mockContainer.newKieSession(Mockito.anyString())).thenReturn(mockSession);
        
        return mockContainer;
    }

    /**
     * Provides a mock KieBase for testing.
     */
    @Bean
    @Primary
    public KieBase kieBase() {
        return Mockito.mock(KieBase.class);
    }

    /**
     * Provides a mock KieSession factory for testing.
     */
    @Bean
    @Primary
    public KieSession kieSession() {
        return Mockito.mock(KieSession.class);
    }

    /**
     * Provides a mock RuleCompilationCacheService for testing.
     * This prevents cache-related NullPointerExceptions during testing.
     */
    @Bean
    @Primary
    public RuleCompilationCacheService ruleCompilationCacheService() {
        RuleCompilationCacheService mockService = Mockito.mock(RuleCompilationCacheService.class);
        
        // Configure mock behavior to prevent NPEs
        Mockito.when(mockService.getCachedKieBuilder(Mockito.any(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockService.getCachedKieContainer(Mockito.any(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockService.getCachedKieBase(Mockito.any(), Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(mockService.hasResourceChanged(Mockito.any(), Mockito.anyString())).thenReturn(false);
        
        // Mock cache statistics
        Mockito.when(mockService.getCacheHitCount()).thenReturn(0L);
        Mockito.when(mockService.getCacheMissCount()).thenReturn(0L);
        Mockito.when(mockService.getCacheSize()).thenReturn(0);
        
        return mockService;
    }

    /**
     * Provides a simple KieServices instance for testing.
     */
    @Bean
    @Primary
    public KieServices kieServices() {
        return KieServices.Factory.get();
    }

    /**
     * Provides a mock KieFileSystem for testing.
     */
    @Bean
    @Primary
    public KieFileSystem kieFileSystem() {
        return Mockito.mock(KieFileSystem.class);
    }

    /**
     * Provides a mock KieBuilder for testing.
     */
    @Bean
    @Primary
    public KieBuilder kieBuilder() {
        return Mockito.mock(KieBuilder.class);
    }
}