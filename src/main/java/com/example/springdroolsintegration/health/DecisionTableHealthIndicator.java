package com.example.springdroolsintegration.health;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.util.ExcelValidationUtil;
import com.example.springdroolsintegration.util.LoggingUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom health indicator for decision tables.
 * This health indicator checks the status of decision tables
 * and reports health information to Spring Boot Actuator.
 */
@Component
public class DecisionTableHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DecisionTableHealthIndicator.class);
    
    private final DroolsProperties droolsProperties;
    private final ResourceLoader resourceLoader;
    
    /**
     * Constructor for DecisionTableHealthIndicator.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param droolsProperties Configuration properties for Drools
     * @param resourceLoader Spring ResourceLoader for loading decision tables
     */
    public DecisionTableHealthIndicator(DroolsProperties droolsProperties, 
                                       ResourceLoader resourceLoader) {
        this.droolsProperties = droolsProperties;
        this.resourceLoader = resourceLoader;
        
        LoggingUtils.logInfo(logger, "DecisionTableHealthIndicator initialized");
    }
    
    /**
     * Checks the health of decision tables.
     * This method verifies that decision tables are accessible and valid.
     *
     * @return Health information for decision tables
     */
    @Override
    public Health health() {
        try {
            // Check decision tables from classpath
            List<String> validationErrors = new ArrayList<>();
            Map<String, Object> details = new HashMap<>();
            
            // Check decision tables from classpath
            if (droolsProperties.getDecisionTablePath() != null && 
                !droolsProperties.getDecisionTablePath().isEmpty()) {
                
                checkClasspathDecisionTables(validationErrors, details);
            }
            
            // Check decision tables from external path
            if (droolsProperties.getExternalRulePath() != null && 
                !droolsProperties.getExternalRulePath().isEmpty()) {
                
                checkExternalDecisionTables(validationErrors, details);
            }
            
            // Check specific decision table files
            if (!droolsProperties.getDecisionTableFiles().isEmpty()) {
                checkSpecificDecisionTables(validationErrors, details);
            }
            
            // If there are validation errors, report DOWN status
            if (!validationErrors.isEmpty()) {
                LoggingUtils.logError(logger, "Decision table validation errors: {}", validationErrors);
                return Health.down()
                        .withDetails(details)
                        .build();
            }
            
            // If no errors, report UP status
            return Health.up()
                    .withDetails(details)
                    .build();
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error checking decision table health", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getName())
                    .build();
        }
    }
    
    /**
     * Checks decision tables from the classpath.
     *
     * @param validationErrors List to add validation errors to
     * @param details Map to add health details to
     * @throws IOException if there is an error accessing decision tables
     */
    private void checkClasspathDecisionTables(List<String> validationErrors, Map<String, Object> details) 
            throws IOException {
        
        String decisionTablePath = droolsProperties.getDecisionTablePath();
        LoggingUtils.logInfo(logger, "Checking decision tables from classpath: {}", decisionTablePath);
        
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        
        // Check .xls files
        String xlsPattern = decisionTablePath.startsWith("classpath:") ? 
                decisionTablePath + "**/*.xls" : "classpath:" + decisionTablePath + "**/*.xls";
        Resource[] xlsResources = resolver.getResources(xlsPattern);
        
        // Check .xlsx files
        String xlsxPattern = decisionTablePath.startsWith("classpath:") ? 
                decisionTablePath + "**/*.xlsx" : "classpath:" + decisionTablePath + "**/*.xlsx";
        Resource[] xlsxResources = resolver.getResources(xlsxPattern);
        
        int validCount = 0;
        int invalidCount = 0;
        
        // Validate .xls files
        for (Resource resource : xlsResources) {
            if (validateDecisionTable(resource, validationErrors)) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        // Validate .xlsx files
        for (Resource resource : xlsxResources) {
            if (validateDecisionTable(resource, validationErrors)) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        details.put("classpathDecisionTables", Map.of(
                "path", decisionTablePath,
                "validCount", validCount,
                "invalidCount", invalidCount,
                "totalCount", xlsResources.length + xlsxResources.length
        ));
    }
    
    /**
     * Checks decision tables from an external path.
     *
     * @param validationErrors List to add validation errors to
     * @param details Map to add health details to
     * @throws IOException if there is an error accessing decision tables
     */
    private void checkExternalDecisionTables(List<String> validationErrors, Map<String, Object> details) 
            throws IOException {
        
        String externalPath = droolsProperties.getExternalRulePath();
        LoggingUtils.logInfo(logger, "Checking decision tables from external path: {}", externalPath);
        
        Path path = Paths.get(externalPath);
        if (!Files.exists(path)) {
            validationErrors.add("External rule path does not exist: " + externalPath);
            details.put("externalDecisionTables", Map.of(
                    "path", externalPath,
                    "error", "Path does not exist"
            ));
            return;
        }
        
        if (!Files.isDirectory(path)) {
            validationErrors.add("External rule path is not a directory: " + externalPath);
            details.put("externalDecisionTables", Map.of(
                    "path", externalPath,
                    "error", "Path is not a directory"
            ));
            return;
        }
        
        // Find all .xls and .xlsx files
        List<Path> decisionTableFiles = new ArrayList<>();
        Files.walk(path)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String fileName = p.toString();
                    return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
                })
                .forEach(decisionTableFiles::add);
        
        int validCount = 0;
        int invalidCount = 0;
        
        // Validate each file
        for (Path file : decisionTableFiles) {
            if (validateDecisionTable(file, validationErrors)) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        details.put("externalDecisionTables", Map.of(
                "path", externalPath,
                "validCount", validCount,
                "invalidCount", invalidCount,
                "totalCount", decisionTableFiles.size()
        ));
    }
    
    /**
     * Checks specific decision table files.
     *
     * @param validationErrors List to add validation errors to
     * @param details Map to add health details to
     * @throws IOException if there is an error accessing decision tables
     */
    private void checkSpecificDecisionTables(List<String> validationErrors, Map<String, Object> details) 
            throws IOException {
        
        List<String> decisionTableFiles = droolsProperties.getDecisionTableFiles();
        LoggingUtils.logInfo(logger, "Checking specific decision tables: {}", decisionTableFiles);
        
        int validCount = 0;
        int invalidCount = 0;
        
        for (String fileName : decisionTableFiles) {
            Resource resource = resourceLoader.getResource("classpath:" + fileName);
            if (!resource.exists()) {
                validationErrors.add("Decision table file not found: " + fileName);
                invalidCount++;
                continue;
            }
            
            if (validateDecisionTable(resource, validationErrors)) {
                validCount++;
            } else {
                invalidCount++;
            }
        }
        
        details.put("specificDecisionTables", Map.of(
                "validCount", validCount,
                "invalidCount", invalidCount,
                "totalCount", decisionTableFiles.size()
        ));
    }
    
    /**
     * Validates a decision table resource.
     *
     * @param resource The resource to validate
     * @param validationErrors List to add validation errors to
     * @return true if the decision table is valid, false otherwise
     */
    private boolean validateDecisionTable(Resource resource, List<String> validationErrors) {
        try {
            LoggingUtils.logDebug(logger, "Validating decision table: {}", resource.getFilename());
            
            if (!resource.exists()) {
                validationErrors.add("Resource does not exist: " + resource.getFilename());
                return false;
            }
            
            if (!resource.isReadable()) {
                validationErrors.add("Resource is not readable: " + resource.getFilename());
                return false;
            }
            
            // Check if the file is a valid Excel file
            try (InputStream is = resource.getInputStream();
                 Workbook workbook = WorkbookFactory.create(is)) {
                
                // Check if the workbook has at least one sheet
                if (workbook.getNumberOfSheets() == 0) {
                    validationErrors.add("Excel file does not contain any sheets: " + resource.getFilename());
                    return false;
                }
                
                // Additional validation could be performed here
                
                return true;
            } catch (Exception e) {
                validationErrors.add("Invalid Excel file: " + resource.getFilename() + " - " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            validationErrors.add("Error validating decision table: " + resource.getFilename() + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates a decision table file.
     *
     * @param file The file to validate
     * @param validationErrors List to add validation errors to
     * @return true if the decision table is valid, false otherwise
     */
    private boolean validateDecisionTable(Path file, List<String> validationErrors) {
        try {
            LoggingUtils.logDebug(logger, "Validating decision table: {}", file.getFileName());
            
            // Check if the file exists
            if (!Files.exists(file)) {
                validationErrors.add("File does not exist: " + file);
                return false;
            }
            
            // Check if the file is readable
            if (!Files.isReadable(file)) {
                validationErrors.add("File is not readable: " + file);
                return false;
            }
            
            // Check if the file is a valid Excel file
            try (InputStream is = Files.newInputStream(file);
                 Workbook workbook = WorkbookFactory.create(is)) {
                
                // Check if the workbook has at least one sheet
                if (workbook.getNumberOfSheets() == 0) {
                    validationErrors.add("Excel file does not contain any sheets: " + file);
                    return false;
                }
                
                // Additional validation could be performed here
                
                return true;
            } catch (Exception e) {
                validationErrors.add("Invalid Excel file: " + file + " - " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            validationErrors.add("Error validating decision table: " + file + " - " + e.getMessage());
            return false;
        }
    }
}