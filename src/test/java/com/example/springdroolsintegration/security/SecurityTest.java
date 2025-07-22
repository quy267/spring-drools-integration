package com.example.springdroolsintegration.security;

import com.example.springdroolsintegration.config.CorrelationIdFilter;
import com.example.springdroolsintegration.config.RateLimitInterceptor;
import com.example.springdroolsintegration.config.SecurityHeadersFilter;
import com.example.springdroolsintegration.config.WebMvcConfig;
import com.example.springdroolsintegration.controller.RuleExecutionController;
import com.example.springdroolsintegration.controller.RuleManagementController;
import com.example.springdroolsintegration.service.SecurityAuditService;
import com.example.springdroolsintegration.util.FileSanitizer;
import com.example.springdroolsintegration.util.FileValidator;
import com.example.springdroolsintegration.util.LoggingUtils;
import com.example.springdroolsintegration.util.SecureFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive security tests for the application.
 * This class tests all security features implemented in the application.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private SecurityAuditService securityAuditService;

    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private FileSanitizer fileSanitizer;

    @Autowired
    private CorrelationIdFilter correlationIdFilter;

    @Autowired
    private SecurityHeadersFilter securityHeadersFilter;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @BeforeEach
    public void setup() {
        // Reset any mocks
        reset(securityAuditService);
    }

    @Test
    @DisplayName("Test input validation for rule execution endpoint")
    public void testInputValidation() throws Exception {
        // Test with invalid input (null fact)
        String invalidRequest = "{ \"rulePackage\": \"com.example.rules\" }";
        
        mockMvc.perform(post("/api/v1/rules/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
        
        // Verify security audit logging
        verify(securityAuditService, times(1)).logSecurityViolation(
                eq("anonymous"), 
                eq("SUSPICIOUS_RESPONSE"), 
                contains("Suspicious HTTP status code"), 
                any(Map.class));
    }

    @Test
    @DisplayName("Test file upload security")
    public void testFileUploadSecurity() throws Exception {
        // Test with valid file
        MockMultipartFile validFile = new MockMultipartFile(
                "file", 
                "test.drl", 
                MediaType.TEXT_PLAIN_VALUE, 
                "package com.example.rules; rule \"Test\" when then end".getBytes(StandardCharsets.UTF_8));
        
        // Test with invalid file (wrong extension)
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", 
                "test.exe", 
                MediaType.APPLICATION_OCTET_STREAM_VALUE, 
                "malicious content".getBytes(StandardCharsets.UTF_8));
        
        // Test with malicious XML content
        MockMultipartFile maliciousFile = new MockMultipartFile(
                "file", 
                "malicious.drl", 
                MediaType.TEXT_PLAIN_VALUE, 
                "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><foo>&xxe;</foo>".getBytes(StandardCharsets.UTF_8));
        
        // Test file validation
        assertTrue(fileValidator.isValidRuleFile(validFile), "Valid file should pass validation");
        assertFalse(fileValidator.isValidRuleFile(invalidFile), "Invalid file should fail validation");
        
        // Test file sanitization
        assertTrue(fileSanitizer.isSafeFile(validFile), "Valid file should pass sanitization");
        assertFalse(fileSanitizer.isSafeFile(invalidFile), "Invalid file should fail sanitization");
        assertFalse(fileSanitizer.isSafeFile(maliciousFile), "Malicious file should fail sanitization");
        
        // Test path sanitization
        Path basePath = Path.of("/tmp");
        assertNotNull(fileSanitizer.sanitizePath(basePath, "valid/path"), "Valid path should be sanitized");
        assertNull(fileSanitizer.sanitizePath(basePath, "../../../etc/passwd"), "Path traversal should be blocked");
    }

    @Test
    @DisplayName("Test CORS configuration")
    public void testCorsConfiguration() throws Exception {
        // Test CORS preflight request
        mockMvc.perform(options("/api/v1/rules/execute")
                .header("Origin", "http://example.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
        
        // Test CORS actual request
        mockMvc.perform(get("/api/v1/rules/metadata")
                .header("Origin", "http://example.com"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Test rate limiting")
    public void testRateLimiting() throws Exception {
        // Mock HttpServletRequest and HttpServletResponse
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/rules/execute");
        request.setMethod("POST");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        // Test rate limiting by simulating multiple requests
        for (int i = 0; i < 101; i++) {
            rateLimitInterceptor.preHandle(request, response, null);
        }
        
        // The 101st request should be rate limited (assuming max-requests=100)
        assertEquals(429, response.getStatus(), "Request should be rate limited");
        assertNotNull(response.getHeader("Retry-After"), "Retry-After header should be set");
    }

    @Test
    @DisplayName("Test security headers")
    public void testSecurityHeaders() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/rules/metadata"))
                .andExpect(status().isOk())
                .andReturn();
        
        // Check security headers
        assertTrue(result.getResponse().containsHeader("Content-Security-Policy"), 
                "Content-Security-Policy header should be present");
        assertTrue(result.getResponse().containsHeader("X-Content-Type-Options"), 
                "X-Content-Type-Options header should be present");
        assertTrue(result.getResponse().containsHeader("X-Frame-Options"), 
                "X-Frame-Options header should be present");
        assertTrue(result.getResponse().containsHeader("X-XSS-Protection"), 
                "X-XSS-Protection header should be present");
        assertTrue(result.getResponse().containsHeader("Referrer-Policy"), 
                "Referrer-Policy header should be present");
        assertTrue(result.getResponse().containsHeader("Cache-Control"), 
                "Cache-Control header should be present");
    }

    @Test
    @DisplayName("Test correlation ID and request logging")
    public void testCorrelationIdAndRequestLogging() throws Exception {
        // Test correlation ID header
        MvcResult result = mockMvc.perform(get("/api/v1/rules/metadata")
                .header("X-Correlation-ID", "test-correlation-id"))
                .andExpect(status().isOk())
                .andReturn();
        
        assertEquals("test-correlation-id", result.getResponse().getHeader("X-Correlation-ID"), 
                "Correlation ID should be preserved");
        
        // Test correlation ID generation
        result = mockMvc.perform(get("/api/v1/rules/metadata"))
                .andExpect(status().isOk())
                .andReturn();
        
        assertNotNull(result.getResponse().getHeader("X-Correlation-ID"), 
                "Correlation ID should be generated");
        
        // Test client IP tracking
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "192.168.1.1");
        
        LoggingUtils.clearClientIp();
        correlationIdFilter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {
            assertEquals("192.168.1.1", LoggingUtils.getClientIp(), "Client IP should be tracked");
        });
    }

    @Test
    @DisplayName("Test security audit logging")
    public void testSecurityAuditLogging() throws Exception {
        // Test security violation logging
        mockMvc.perform(post("/api/v1/rules/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"invalid\": true }"))
                .andExpect(status().isBadRequest());
        
        // Verify security audit logging
        verify(securityAuditService, times(1)).logSecurityViolation(
                anyString(), 
                eq("SUSPICIOUS_RESPONSE"), 
                anyString(), 
                any(Map.class));
    }
}