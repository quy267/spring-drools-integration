package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.service.impl.RuleCompilationCacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RuleCompilationCacheServiceImplTest {

    @Mock
    private DroolsProperties droolsProperties;

    @Mock
    private KieFileSystem kieFileSystem;

    @Mock
    private KieBuilder kieBuilder;

    @Mock
    private Results results;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private KieBase kieBase;

    @Mock
    private Resource resource;

    private RuleCompilationCacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        when(droolsProperties.isCacheEnabled()).thenReturn(true);
        cacheService = new RuleCompilationCacheServiceImpl(droolsProperties);
    }

    @Test
    void testGetCachedKieBuilderWhenNotInCache() {
        // Act
        KieBuilder result = cacheService.getCachedKieBuilder(kieFileSystem, "testKey");

        // Assert
        assertNull(result);
        assertEquals(0, cacheService.getCacheHitCount());
        assertEquals(1, cacheService.getCacheMissCount());
    }

    @Test
    void testCacheAndGetKieBuilder() {
        // Arrange
        String cacheKey = "testKey";

        // Act
        cacheService.cacheKieBuilder(kieFileSystem, cacheKey, kieBuilder, results);
        KieBuilder result = cacheService.getCachedKieBuilder(kieFileSystem, cacheKey);

        // Assert
        assertNotNull(result);
        assertEquals(kieBuilder, result);
        assertEquals(1, cacheService.getCacheHitCount());
        assertEquals(0, cacheService.getCacheMissCount());
    }

    @Test
    void testGetCachedKieContainerWhenNotInCache() {
        // Act
        KieContainer result = cacheService.getCachedKieContainer(kieBuilder, "testKey");

        // Assert
        assertNull(result);
        assertEquals(0, cacheService.getCacheHitCount());
        assertEquals(1, cacheService.getCacheMissCount());
    }

    @Test
    void testCacheAndGetKieContainer() {
        // Arrange
        String cacheKey = "testKey";

        // Act
        cacheService.cacheKieContainer(kieBuilder, cacheKey, kieContainer);
        KieContainer result = cacheService.getCachedKieContainer(kieBuilder, cacheKey);

        // Assert
        assertNotNull(result);
        assertEquals(kieContainer, result);
        assertEquals(1, cacheService.getCacheHitCount());
        assertEquals(0, cacheService.getCacheMissCount());
    }

    @Test
    void testGetCachedKieBaseWhenNotInCache() {
        // Act
        KieBase result = cacheService.getCachedKieBase(kieContainer, "testBase", "testKey");

        // Assert
        assertNull(result);
        assertEquals(0, cacheService.getCacheHitCount());
        assertEquals(1, cacheService.getCacheMissCount());
    }

    @Test
    void testCacheAndGetKieBase() {
        // Arrange
        String kieBaseName = "testBase";
        String cacheKey = "testKey";

        // Act
        cacheService.cacheKieBase(kieContainer, kieBaseName, cacheKey, kieBase);
        KieBase result = cacheService.getCachedKieBase(kieContainer, kieBaseName, cacheKey);

        // Assert
        assertNotNull(result);
        assertEquals(kieBase, result);
        assertEquals(1, cacheService.getCacheHitCount());
        assertEquals(0, cacheService.getCacheMissCount());
    }

    @Test
    void testHasResourceChangedWhenResourceNotCached() throws IOException {
        // Arrange
        String resourceId = "testResource";
        
        // Mock resource content
        byte[] content = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Act
        boolean result = cacheService.hasResourceChanged(resource, resourceId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testHasResourceChangedWhenResourceUnchanged() throws IOException {
        // Arrange
        String resourceId = "testResource";
        
        // Mock resource content
        byte[] content = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        
        // Cache the resource first
        cacheService.hasResourceChanged(resource, resourceId);
        
        // Reset the mock to return the same content again
        inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Act
        boolean result = cacheService.hasResourceChanged(resource, resourceId);

        // Assert
        assertFalse(result);
    }

    @Test
    void testHasResourceChangedWhenResourceChanged() throws IOException {
        // Arrange
        String resourceId = "testResource";
        
        // Mock initial resource content
        byte[] initialContent = "initial content".getBytes();
        InputStream initialInputStream = new ByteArrayInputStream(initialContent);
        when(resource.getInputStream()).thenReturn(initialInputStream);
        
        // Cache the resource first
        cacheService.hasResourceChanged(resource, resourceId);
        
        // Mock changed resource content
        byte[] changedContent = "changed content".getBytes();
        InputStream changedInputStream = new ByteArrayInputStream(changedContent);
        when(resource.getInputStream()).thenReturn(changedInputStream);

        // Act
        boolean result = cacheService.hasResourceChanged(resource, resourceId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testEvictResource() throws IOException {
        // Arrange
        String resourceId = "testResource";
        
        // Mock resource content and cache it
        byte[] content = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        cacheService.hasResourceChanged(resource, resourceId);
        
        // Act
        cacheService.evictResource(resourceId);
        
        // Reset the mock to return the same content again
        inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        
        // Assert - resource should be treated as changed after eviction
        boolean result = cacheService.hasResourceChanged(resource, resourceId);
        assertTrue(result);
    }

    @Test
    void testEvictAllResources() throws IOException {
        // Arrange
        String resourceId1 = "testResource1";
        String resourceId2 = "testResource2";
        
        // Mock resource content and cache it
        byte[] content = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        cacheService.hasResourceChanged(resource, resourceId1);
        
        inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        cacheService.hasResourceChanged(resource, resourceId2);
        
        // Act
        cacheService.evictAllResources();
        
        // Assert
        assertEquals(0, cacheService.getCacheSize());
        assertTrue(cacheService.getCachedResourceIds().isEmpty());
    }

    @Test
    void testGetCacheStatistics() {
        // Arrange
        cacheService.getCachedKieBuilder(kieFileSystem, "missKey");
        
        String cacheKey = "hitKey";
        cacheService.cacheKieBuilder(kieFileSystem, cacheKey, kieBuilder, results);
        cacheService.getCachedKieBuilder(kieFileSystem, cacheKey);
        
        // Act
        Map<String, Object> stats = cacheService.getCacheStatistics();
        
        // Assert
        assertNotNull(stats);
        assertEquals(1L, stats.get("hitCount"));
        assertEquals(1L, stats.get("missCount"));
        assertEquals(1, stats.get("cacheSize"));
        assertEquals(0.5, stats.get("hitRatio"));
    }

    @Test
    void testGetCachedResourceIds() throws IOException {
        // Arrange
        String resourceId1 = "testResource1";
        String resourceId2 = "testResource2";
        
        // Mock resource content and cache it
        byte[] content = "test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        cacheService.hasResourceChanged(resource, resourceId1);
        
        inputStream = new ByteArrayInputStream(content);
        when(resource.getInputStream()).thenReturn(inputStream);
        cacheService.hasResourceChanged(resource, resourceId2);
        
        // Act
        Set<String> resourceIds = cacheService.getCachedResourceIds();
        
        // Assert
        assertNotNull(resourceIds);
        assertEquals(2, resourceIds.size());
        assertTrue(resourceIds.contains(resourceId1));
        assertTrue(resourceIds.contains(resourceId2));
    }

    @Test
    void testCacheDisabled() {
        // Arrange
        when(droolsProperties.isCacheEnabled()).thenReturn(false);
        cacheService = new RuleCompilationCacheServiceImpl(droolsProperties);
        
        // Act & Assert
        assertNull(cacheService.getCachedKieBuilder(kieFileSystem, "testKey"));
        
        cacheService.cacheKieBuilder(kieFileSystem, "testKey", kieBuilder, results);
        assertNull(cacheService.getCachedKieBuilder(kieFileSystem, "testKey"));
        
        assertNull(cacheService.getCachedKieContainer(kieBuilder, "testKey"));
        
        cacheService.cacheKieContainer(kieBuilder, "testKey", kieContainer);
        assertNull(cacheService.getCachedKieContainer(kieBuilder, "testKey"));
        
        assertNull(cacheService.getCachedKieBase(kieContainer, "testBase", "testKey"));
        
        cacheService.cacheKieBase(kieContainer, "testBase", "testKey", kieBase);
        assertNull(cacheService.getCachedKieBase(kieContainer, "testBase", "testKey"));
    }

    @Test
    void testResourceIOException() throws IOException {
        // Arrange
        String resourceId = "testResource";
        when(resource.getInputStream()).thenThrow(new IOException("Test IO exception"));
        
        // Act & Assert
        assertTrue(cacheService.hasResourceChanged(resource, resourceId));
    }
}