package com.example.springdroolsintegration.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Service interface for rule management operations.
 * This service handles rule upload, validation, status, and reload operations.
 */
public interface RuleManagementService {

    /**
     * Uploads a rule file (DRL or Excel decision table) to the system.
     *
     * @param file The rule file to upload
     * @param version Optional version identifier for the rule
     * @return A map containing the upload result information
     * @throws IOException if there is an error reading or processing the file
     */
    Map<String, Object> uploadRuleFile(MultipartFile file, String version) throws IOException;

    /**
     * Validates a rule file without adding it to the rule engine.
     *
     * @param file The rule file to validate
     * @return A map containing the validation result information
     * @throws IOException if there is an error reading or processing the file
     */
    Map<String, Object> validateRuleFile(MultipartFile file) throws IOException;

    /**
     * Gets the current status of the rule engine and loaded rules.
     *
     * @return A map containing rule engine status information
     */
    Map<String, Object> getRuleStatus();

    /**
     * Reloads all rules from their sources.
     *
     * @return A map containing the reload result information
     * @throws IOException if there is an error reading or processing rule files
     */
    Map<String, Object> reloadRules() throws IOException;
}