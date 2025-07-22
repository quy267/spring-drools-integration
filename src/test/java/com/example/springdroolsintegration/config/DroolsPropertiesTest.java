package com.example.springdroolsintegration.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DroolsProperties configuration validation.
 */
class DroolsPropertiesTest {

    private Validator validator;
    private DroolsProperties properties;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        properties = new DroolsProperties();
        
        // Set valid default values
        properties.setRulePath("classpath:rules/");
        properties.setDecisionTablePath("classpath:rules/decision-tables/");
        properties.setFileExtensions(".drl,.xls,.xlsx");
        properties.setKieBaseName("defaultKieBase");
        properties.setKieSessionName("defaultKieSession");
    }

    @Test
    void whenAllPropertiesValid_thenNoViolations() {
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertTrue(violations.isEmpty(), "No violations should be found for valid properties");
    }

    @Test
    void whenRulePathBlank_thenViolation() {
        properties.setRulePath("");
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Rule path must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenDecisionTablePathBlank_thenViolation() {
        properties.setDecisionTablePath("");
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Decision table path must not be blank", violations.iterator().next().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "drl,xls", "drl, xls", ".drl .xlsx"})
    void whenFileExtensionsInvalid_thenViolation(String extensions) {
        properties.setFileExtensions(extensions);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertFalse(violations.isEmpty(), "Should have violations for invalid file extensions");
    }

    @Test
    void whenFileExtensionsValid_thenNoViolation() {
        properties.setFileExtensions(".drl,.xls,.xlsx");
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertTrue(violations.isEmpty(), "No violations should be found for valid file extensions");
    }

    @Test
    void whenSessionPoolSizeTooSmall_thenViolation() {
        properties.setSessionPoolSize(0);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Session pool size must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void whenSessionTimeoutTooSmall_thenViolation() {
        properties.setSessionTimeout(999);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Session timeout must be at least 1000 ms", violations.iterator().next().getMessage());
    }

    @Test
    void whenMaxRulesPerSessionTooSmall_thenViolation() {
        properties.setMaxRulesPerSession(0);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Max rules per session must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void whenKieBaseNameBlank_thenViolation() {
        properties.setKieBaseName("");
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("KieBase name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenKieSessionNameBlank_thenViolation() {
        properties.setKieSessionName("");
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("KieSession name must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenMaxExecutionThreadsTooSmall_thenViolation() {
        properties.setMaxExecutionThreads(0);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Max execution threads must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void whenRuleExecutionTimeoutTooSmall_thenViolation() {
        properties.setRuleExecutionTimeout(99);
        Set<ConstraintViolation<DroolsProperties>> violations = validator.validate(properties);
        assertEquals(1, violations.size(), "Should have one violation");
        assertEquals("Rule execution timeout must be at least 100 ms", violations.iterator().next().getMessage());
    }
}