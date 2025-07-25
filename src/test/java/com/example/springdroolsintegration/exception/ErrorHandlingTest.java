package com.example.springdroolsintegration.exception;

import com.example.springdroolsintegration.service.RuleExecutionService;
import com.example.springdroolsintegration.util.ErrorRateMonitor;
import com.example.springdroolsintegration.util.RetryUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for error handling.
 * This class tests the custom exceptions, the GlobalExceptionHandler, and the error handling mechanisms.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ErrorRateMonitor errorRateMonitor;
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @MockBean
    private RuleExecutionService ruleExecutionService;
    
    /**
     * Test configuration for error handling tests.
     */
    @TestConfiguration
    static class TestConfig {
        
        /**
         * Creates a SimpleMeterRegistry for testing.
         *
         * @return A SimpleMeterRegistry
         */
        @Bean
        @Primary
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
    
    @BeforeEach
    public void setUp() {
        // Reset error counters before each test
        if (errorRateMonitor != null) {
            errorRateMonitor.resetErrorCounter("testOperation");
        }
    }
    
    @Test
    @DisplayName("Test RuleExecutionException creation and properties")
    public void testRuleExecutionException() {
        // Create a RuleExecutionException
        String ruleName = "testRule";
        String message = "Test rule execution error";
        RuleExecutionException.ErrorType errorType = RuleExecutionException.ErrorType.RULE_NOT_FOUND;
        String correlationId = "test-correlation-id";
        
        RuleExecutionException exception = new RuleExecutionException(
                message, ruleName, errorType, correlationId);
        
        // Verify properties
        assertEquals(message, exception.getMessage());
        assertEquals(ruleName, exception.getRuleName());
        assertEquals(errorType, exception.getErrorType());
        assertEquals(correlationId, exception.getCorrelationId());
        assertNull(exception.getRulePackage());
    }
    
    @Test
    @DisplayName("Test DecisionTableValidationException creation and properties")
    public void testDecisionTableValidationException() {
        // Create a DecisionTableValidationException
        String fileName = "test.xlsx";
        String sheetName = "Sheet1";
        String message = "Test decision table validation error";
        DecisionTableValidationException.ValidationErrorType errorType = 
                DecisionTableValidationException.ValidationErrorType.MISSING_HEADERS;
        
        DecisionTableValidationException exception = new DecisionTableValidationException(
                message, fileName, sheetName, errorType);
        
        // Verify properties
        assertEquals(message, exception.getMessage());
        assertEquals(fileName, exception.getFilename());
        assertEquals(sheetName, exception.getSheetName());
        assertEquals(errorType, exception.getErrorType());
    }
    
    @Test
    @DisplayName("Test RuleConfigurationException creation and properties")
    public void testRuleConfigurationException() {
        // Create a RuleConfigurationException
        String configKey = "app.drools.rulePath";
        String configValue = "invalid/path";
        String message = "Test rule configuration error";
        RuleConfigurationException.ErrorType errorType = 
                RuleConfigurationException.ErrorType.INVALID_RULE_PATH;
        
        RuleConfigurationException exception = new RuleConfigurationException(
                message, configKey, configValue, errorType);
        
        // Verify properties
        assertEquals(message, exception.getMessage());
        assertEquals(configKey, exception.getConfigKey());
        assertEquals(configValue, exception.getConfigValue());
        assertEquals(errorType, exception.getErrorType());
        assertNull(exception.getCorrelationId());
    }
    
    @Test
    @DisplayName("Test GlobalExceptionHandler with RuleExecutionException")
    public void testGlobalExceptionHandlerWithRuleExecutionException() throws Exception {
        // Mock the RuleExecutionService to throw a RuleExecutionException
        // Mock both overloaded versions of executeRules method
        when(ruleExecutionService.executeRules(any())).thenThrow(
                new RuleExecutionException("Test rule execution error", "testRule", 
                        RuleExecutionException.ErrorType.RUNTIME_ERROR, "test-correlation-id"));
        when(ruleExecutionService.executeRules(any(), any(String.class))).thenThrow(
                new RuleExecutionException("Test rule execution error", "testRule", 
                        RuleExecutionException.ErrorType.RUNTIME_ERROR, "test-correlation-id"));
        
        // Create a valid request body
        String validRequestBody = "{\"fact\": {\"name\": \"test\", \"value\": 123}}";
        
        try {
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rules/execute")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRequestBody)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            
            System.out.println("[DEBUG_LOG] Response status: " + result.getResponse().getStatus());
            System.out.println("[DEBUG_LOG] Response body: " + result.getResponse().getContentAsString());
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] Exception during MockMvc call: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Now run the assertions - add them back one by one
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/rules/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequestBody)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Rule Execution Error"));
    }
    
    @Test
    @DisplayName("Test GlobalExceptionHandler with MaxUploadSizeExceededException")
    public void testGlobalExceptionHandlerWithMaxUploadSizeExceededException() throws Exception {
        // Since MockMvc doesn't properly enforce multipart size limits in tests,
        // we'll test the GlobalExceptionHandler directly by creating a mock request
        // and manually triggering the exception handler
        
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(1024L);
        
        // Create a mock WebRequest
        org.springframework.web.context.request.WebRequest mockRequest = 
            org.mockito.Mockito.mock(org.springframework.web.context.request.WebRequest.class);
        
        // Get the GlobalExceptionHandler bean from the application context
        com.example.springdroolsintegration.exception.GlobalExceptionHandler globalExceptionHandler = 
            new com.example.springdroolsintegration.exception.GlobalExceptionHandler();
        
        // Call the exception handler directly
        org.springframework.http.ResponseEntity<org.springframework.http.ProblemDetail> response = 
            globalExceptionHandler.handleMaxUploadSizeExceededException(exception, mockRequest);
        
        // Verify the response
        assertEquals(413, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("File Upload Error", response.getBody().getTitle());
        assertEquals(413, response.getBody().getStatus());
        
        // Verify properties
        assertNotNull(response.getBody().getProperties());
        assertEquals("MAX_UPLOAD_SIZE_EXCEEDED", response.getBody().getProperties().get("errorType"));
        assertNotNull(response.getBody().getProperties().get("correlationId"));
        assertNotNull(response.getBody().getProperties().get("timestamp"));
    }
    
    @Test
    @DisplayName("Test RetryUtils with successful operation")
    public void testRetryUtilsWithSuccessfulOperation() throws Exception {
        // Create a callable that succeeds
        Callable<String> successfulOperation = () -> "success";
        
        // Execute with retry
        String result = RetryUtils.retry(successfulOperation);
        
        // Verify result
        assertEquals("success", result);
    }
    
    @Test
    @DisplayName("Test RetryUtils with failing operation")
    public void testRetryUtilsWithFailingOperation() {
        // Create a callable that always fails
        Callable<String> failingOperation = () -> {
            throw new RuntimeException("Test failure");
        };
        
        // Execute with retry and verify exception
        Exception exception = assertThrows(RuntimeException.class, () -> {
            RetryUtils.retry(failingOperation, 3);
        });
        
        assertEquals("Test failure", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test RetryUtils with eventually successful operation")
    public void testRetryUtilsWithEventuallySuccessfulOperation() throws Exception {
        // Create a counter to track attempts
        AtomicInteger attempts = new AtomicInteger(0);
        
        // Create a callable that fails twice then succeeds
        Callable<String> eventuallySuccessfulOperation = () -> {
            int attempt = attempts.incrementAndGet();
            if (attempt < 3) {
                throw new RuntimeException("Attempt " + attempt + " failed");
            }
            return "success after " + attempt + " attempts";
        };
        
        // Execute with retry
        String result = RetryUtils.retry(eventuallySuccessfulOperation, 3);
        
        // Verify result
        assertEquals("success after 3 attempts", result);
        assertEquals(3, attempts.get());
    }
    
    @Test
    @DisplayName("Test RetryUtils with retryable exceptions")
    public void testRetryUtilsWithRetryableExceptions() throws Exception {
        // Create a counter to track attempts
        AtomicInteger attempts = new AtomicInteger(0);
        
        // Create a callable that throws different exceptions
        Callable<String> operation = () -> {
            int attempt = attempts.incrementAndGet();
            if (attempt == 1) {
                throw new IllegalArgumentException("Retryable exception");
            } else if (attempt == 2) {
                throw new IllegalStateException("Non-retryable exception");
            }
            return "success";
        };
        
        // Create a list of retryable exceptions
        List<Class<? extends Exception>> retryableExceptions = 
                RetryUtils.retryableExceptions(IllegalArgumentException.class);
        
        // Execute with retry and verify exception
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            RetryUtils.retry(operation, 3, 100L, 1.0, retryableExceptions);
        });
        
        assertEquals("Non-retryable exception", exception.getMessage());
        assertEquals(2, attempts.get());
    }
    
    @Test
    @DisplayName("Test RetryUtils with result validation")
    public void testRetryUtilsWithResultValidation() throws Exception {
        // Create a counter to track attempts
        AtomicInteger attempts = new AtomicInteger(0);
        
        // Create a callable that returns different results
        Callable<Integer> operation = () -> attempts.incrementAndGet();
        
        // Create a predicate to validate the result
        Predicate<Integer> resultValidator = result -> result > 2;
        
        // Execute with retry until valid
        Integer result = RetryUtils.retryUntilValid(operation, 5, 100L, 1.0, resultValidator);
        
        // Verify result
        assertEquals(3, result);
        assertEquals(3, attempts.get());
    }
    
    @Test
    @DisplayName("Test ErrorRateMonitor with successful operations")
    public void testErrorRateMonitorWithSuccessfulOperations() {
        // Record successful operations
        for (int i = 0; i < 10; i++) {
            errorRateMonitor.recordSuccess("testOperation");
        }
        
        // Verify error rate
        double errorRate = errorRateMonitor.getErrorRate("testOperation");
        assertEquals(0.0, errorRate);
        
        // Verify error threshold
        assertFalse(errorRateMonitor.isErrorRateExceeded("testOperation"));
    }
    
    @Test
    @DisplayName("Test ErrorRateMonitor with failed operations")
    public void testErrorRateMonitorWithFailedOperations() {
        // Record successful and failed operations
        for (int i = 0; i < 8; i++) {
            errorRateMonitor.recordSuccess("testOperation");
        }
        for (int i = 0; i < 2; i++) {
            errorRateMonitor.recordError("testOperation", new RuntimeException("Test error"));
        }
        
        // Verify error rate
        double errorRate = errorRateMonitor.getErrorRate("testOperation");
        assertEquals(20.0, errorRate, 0.1);
        
        // Verify error threshold
        assertTrue(errorRateMonitor.isErrorRateExceeded("testOperation", 15.0));
        assertFalse(errorRateMonitor.isErrorRateExceeded("testOperation", 25.0));
    }
    
    @Test
    @DisplayName("Test ErrorRateMonitor with timed operations")
    public void testErrorRateMonitorWithTimedOperations() {
        // Record timed operations
        errorRateMonitor.recordOperation("testOperation", 100, true, null);
        errorRateMonitor.recordOperation("testOperation", 200, false, new RuntimeException("Test error"));
        
        // Verify error rate
        double errorRate = errorRateMonitor.getErrorRate("testOperation");
        assertEquals(50.0, errorRate, 0.1);
        
        // Verify metrics
        assertNotNull(meterRegistry.find("app.operations.duration").timer());
        assertNotNull(meterRegistry.find("app.operations.success").counter());
        assertNotNull(meterRegistry.find("app.operations.error").counter());
    }
}