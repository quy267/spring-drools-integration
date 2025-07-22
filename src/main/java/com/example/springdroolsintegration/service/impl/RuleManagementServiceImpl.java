package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.config.RuleHotReloadService;
import com.example.springdroolsintegration.service.RuleAuditService;
import com.example.springdroolsintegration.service.RuleManagementService;
import com.example.springdroolsintegration.util.ExcelValidationUtil;
import com.example.springdroolsintegration.util.FileValidator;
import com.example.springdroolsintegration.util.SecureFileStorage;
import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the RuleManagementService interface.
 * This service handles rule upload, validation, status, and reload operations.
 */
@Service
public class RuleManagementServiceImpl implements RuleManagementService {

    private static final Logger logger = LoggerFactory.getLogger(RuleManagementServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private final DroolsProperties droolsProperties;
    private final KieContainer kieContainer;
    private final KieServices kieServices;
    private final KieBase kieBase;
    private final RuleHotReloadService ruleHotReloadService;
    private final RuleAuditService ruleAuditService;
    private final FileValidator fileValidator;
    private final SecureFileStorage secureFileStorage;
    
    @Value("${app.rules.upload.dir:${java.io.tmpdir}/rules/uploads}")
    private String uploadDir;
    
    @Value("${app.rules.backup.dir:${java.io.tmpdir}/rules/backups}")
    private String backupDir;
    
    @Value("${app.rules.version.enabled:true}")
    private boolean versioningEnabled;
    
    /**
     * Constructor for RuleManagementServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param droolsProperties Configuration properties for Drools
     * @param kieContainer KieContainer for rule execution
     * @param kieBase KieBase for rule execution
     * @param ruleHotReloadService Service for hot-reloading rules
     * @param ruleAuditService Service for audit logging
     * @param fileValidator Validator for uploaded files
     * @param secureFileStorage Secure storage for uploaded files
     */
    public RuleManagementServiceImpl(DroolsProperties droolsProperties, 
                                    KieContainer kieContainer,
                                    KieBase kieBase,
                                    RuleHotReloadService ruleHotReloadService,
                                    RuleAuditService ruleAuditService,
                                    FileValidator fileValidator,
                                    SecureFileStorage secureFileStorage) {
        this.droolsProperties = droolsProperties;
        this.kieContainer = kieContainer;
        this.kieBase = kieBase;
        this.kieServices = KieServices.Factory.get();
        this.ruleHotReloadService = ruleHotReloadService;
        this.ruleAuditService = ruleAuditService;
        this.fileValidator = fileValidator;
        this.secureFileStorage = secureFileStorage;
        
        // Create upload and backup directories if they don't exist
        createDirectoryIfNotExists(uploadDir);
        createDirectoryIfNotExists(backupDir);
        
        logger.info("RuleManagementService initialized with upload dir: {}, backup dir: {}", uploadDir, backupDir);
    }
    
    @Override
    public Map<String, Object> uploadRuleFile(MultipartFile file, String version) throws IOException {
        logger.info("Uploading rule file: {}, version: {}", file.getOriginalFilename(), version);
        
        // Validate file using FileValidator
        String validationError = fileValidator.getRuleFileValidationError(file);
        if (validationError != null) {
            logger.warn("File validation failed: {}", validationError);
            Map<String, Object> result = new HashMap<>();
            result.put("valid", false);
            result.put("success", false);
            result.put("message", validationError);
            result.put("timestamp", Instant.now().toString());
            
            // Log failed validation
            ruleAuditService.logUploadEvent(
                    file.getOriginalFilename(),
                    null,
                    version,
                    "system", // In a real app, this would be the authenticated user
                    false,
                    "Upload failed due to validation: " + validationError
            );
            return result;
        }
        
        // Create version string
        String versionStr = versioningEnabled ? 
                (version != null && !version.isEmpty() ? version : generateVersionString()) : "";
        
        // Create backup of existing rules
        createBackup();
        
        // Save file securely using SecureFileStorage
        String fileName = file.getOriginalFilename();
        String subdirectory = versionStr.isEmpty() ? null : versionStr;
        
        Path savedPath;
        try {
            // Store file securely
            savedPath = secureFileStorage.storeFile(file, subdirectory);
        } catch (SecurityException e) {
            logger.error("Security error storing file: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Security error: " + e.getMessage());
            result.put("timestamp", Instant.now().toString());
            
            // Log failed upload
            ruleAuditService.logUploadEvent(
                    fileName,
                    null,
                    versionStr,
                    "system",
                    false,
                    "Upload failed due to security error: " + e.getMessage()
            );
            return result;
        }
        
        String savedFilePath = savedPath.toString();
        
        // Reload rules
        Map<String, Object> reloadResult = reloadRules();
        
        Map<String, Object> result = new HashMap<>();
        boolean success = reloadResult.get("success").equals(true);
        result.put("success", success);
        result.put("fileName", fileName);
        result.put("filePath", savedFilePath);
        result.put("version", versionStr);
        result.put("uploadTime", LocalDateTime.now().toString());
        result.put("message", "Rule file uploaded and " + 
                (success ? "loaded successfully" : "failed to load"));
        
        if (!success) {
            result.put("errors", reloadResult.get("errors"));
        }
        
        // Log upload event
        ruleAuditService.logUploadEvent(
                fileName,
                savedFilePath,
                versionStr,
                "system", // In a real app, this would be the authenticated user
                success,
                result.get("message").toString()
        );
        
        return result;
    }
    
    @Override
    public Map<String, Object> validateRuleFile(MultipartFile file) throws IOException {
        logger.info("Validating rule file: {}", file.getOriginalFilename());
        
        Map<String, Object> result = new HashMap<>();
        
        // Use FileValidator to validate the file
        String validationError = fileValidator.getRuleFileValidationError(file);
        if (validationError != null) {
            logger.warn("File validation failed: {}", validationError);
            result.put("valid", false);
            result.put("message", validationError);
            result.put("timestamp", Instant.now().toString());
            
            // Log validation event
            ruleAuditService.logValidationEvent(
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown",
                    "system", // In a real app, this would be the authenticated user
                    false,
                    "Validation failed: " + validationError
            );
            
            return result;
        }
        
        // Get file extension for additional validations
        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        
        // For Excel files, perform additional validation
        if (fileExtension.equals(".xls") || fileExtension.equals(".xlsx")) {
            List<String> excelValidationErrors = ExcelValidationUtil.validateExcelFile(file);
            if (!excelValidationErrors.isEmpty()) {
                result.put("valid", false);
                result.put("message", "Excel file validation failed");
                result.put("errors", excelValidationErrors);
                result.put("timestamp", Instant.now().toString());
                
                // Log validation event
                ruleAuditService.logValidationEvent(
                        fileName,
                        "system",
                        false,
                        "Excel validation failed: " + String.join("; ", excelValidationErrors)
                );
                
                return result;
            }
        }
        
        // Create temporary file
        File tempFile = File.createTempFile("rule-validation-", fileExtension);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        
        // Validate rule file with Drools
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        try {
            // Add file to KieFileSystem
            if (fileExtension.equals(".drl")) {
                // Add DRL file directly
                Resource resource = ResourceFactory.newFileResource(tempFile);
                kieFileSystem.write("src/main/resources/validation/rules.drl", resource);
            } else if (fileExtension.equals(".xls") || fileExtension.equals(".xlsx")) {
                // Convert decision table to DRL
                Resource resource = ResourceFactory.newFileResource(tempFile);
                DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
                String drl = decisionTableProvider.loadFromResource(resource, null);
                kieFileSystem.write("src/main/resources/validation/rules.drl", drl);
            }
            
            // Build rules
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            // Check for errors
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                List<String> errors = results.getMessages(Message.Level.ERROR).stream()
                        .map(Message::getText)
                        .collect(Collectors.toList());
                
                result.put("valid", false);
                result.put("message", "Rule compilation failed");
                result.put("errors", errors);
                
                // Log validation event
                ruleAuditService.logValidationEvent(
                        fileName,
                        "system",
                        false,
                        "Rule compilation failed: " + String.join("; ", errors)
                );
            } else {
                result.put("valid", true);
                result.put("message", "Rule file is valid");
                
                // Log validation event
                ruleAuditService.logValidationEvent(
                        fileName,
                        "system",
                        true,
                        "Rule file is valid"
                );
            }
        } catch (Exception e) {
            logger.error("Error validating rule file", e);
            result.put("valid", false);
            result.put("message", "Error validating rule file: " + e.getMessage());
            
            // Log validation event
            ruleAuditService.logValidationEvent(
                    fileName,
                    "system",
                    false,
                    "Error validating rule file: " + e.getMessage()
            );
        } finally {
            // Delete temporary file
            tempFile.delete();
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getRuleStatus() {
        logger.info("Getting rule status");
        
        Map<String, Object> result = new HashMap<>();
        
        // Get KieBase information
        result.put("kieBaseName", droolsProperties.getKieBaseName());
        result.put("kieSessionName", droolsProperties.getKieSessionName());
        result.put("hotReloadEnabled", droolsProperties.isHotReload());
        
        // Get rule paths
        result.put("rulePath", droolsProperties.getRulePath());
        result.put("decisionTablePath", droolsProperties.getDecisionTablePath());
        result.put("externalRulePath", droolsProperties.getExternalRulePath());
        
        // Get rule files
        List<String> ruleFiles = new ArrayList<>(droolsProperties.getRuleFiles());
        List<String> decisionTableFiles = new ArrayList<>(droolsProperties.getDecisionTableFiles());
        
        result.put("ruleFiles", ruleFiles);
        result.put("decisionTableFiles", decisionTableFiles);
        
        // Get rule packages
        result.put("rulePackages", droolsProperties.getRulePackages());
        
        // Get KieBase statistics
        result.put("ruleCount", kieBase.getKiePackages().stream()
                .mapToLong(pkg -> pkg.getRules().size())
                .sum());
        
        return result;
    }
    
    @Override
    public Map<String, Object> reloadRules() throws IOException {
        logger.info("Reloading rules");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Create a new KieFileSystem
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            
            // Load rule files into the KieFileSystem
            loadRuleFiles(kieFileSystem);
            
            // Build the new rules
            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
            kieBuilder.buildAll();
            
            // Check for errors
            Results results = kieBuilder.getResults();
            if (results.hasMessages(Message.Level.ERROR)) {
                List<String> errors = results.getMessages(Message.Level.ERROR).stream()
                        .map(Message::getText)
                        .collect(Collectors.toList());
                
                result.put("success", false);
                result.put("message", "Rule reload failed");
                result.put("errors", errors);
                
                // Log reload event
                ruleAuditService.logReloadEvent(
                        "system", // In a real app, this would be the authenticated user
                        false,
                        "Rule reload failed: " + String.join("; ", errors)
                );
            } else {
                // Update the KieContainer
                kieContainer.updateToVersion(kieServices.getRepository().getDefaultReleaseId());
                
                result.put("success", true);
                result.put("message", "Rules successfully reloaded");
                
                // Log reload event
                ruleAuditService.logReloadEvent(
                        "system", // In a real app, this would be the authenticated user
                        true,
                        "Rules successfully reloaded"
                );
                
                // Notify the hot reload service if it's enabled
                if (droolsProperties.isHotReload() && ruleHotReloadService != null) {
                    logger.info("Notifying hot reload service of manual rule reload");
                    // The hot reload service will be notified via the KieContainer update
                }
            }
        } catch (Exception e) {
            logger.error("Error reloading rules", e);
            result.put("success", false);
            result.put("message", "Error reloading rules: " + e.getMessage());
            
            // Log reload event
            ruleAuditService.logReloadEvent(
                    "system", // In a real app, this would be the authenticated user
                    false,
                    "Error reloading rules: " + e.getMessage()
            );
        }
        
        return result;
    }
    
    /**
     * Loads rule files into the KieFileSystem.
     * This method loads rule files from classpath and external directories.
     *
     * @param kieFileSystem The KieFileSystem to load rules into
     * @throws IOException if there is an error loading rule files
     */
    private void loadRuleFiles(KieFileSystem kieFileSystem) throws IOException {
        logger.info("Loading rule files for reload");
        
        // Load DRL files from rulePath
        String rulePath = droolsProperties.getRulePath();
        if (rulePath != null && !rulePath.isEmpty()) {
            logger.info("Loading DRL files from path: {}", rulePath);
            
            // Load DRL files from classpath
            if (rulePath.startsWith("classpath:")) {
                loadClasspathDrlFiles(kieFileSystem, rulePath);
            } else {
                // Load DRL files from file system
                loadFileSystemDrlFiles(kieFileSystem, rulePath);
            }
        }
        
        // Load decision tables from decisionTablePath
        String decisionTablePath = droolsProperties.getDecisionTablePath();
        if (decisionTablePath != null && !decisionTablePath.isEmpty()) {
            logger.info("Loading decision tables from path: {}", decisionTablePath);
            
            // Load decision tables from classpath
            if (decisionTablePath.startsWith("classpath:")) {
                loadClasspathDecisionTables(kieFileSystem, decisionTablePath);
            } else {
                // Load decision tables from file system
                loadFileSystemDecisionTables(kieFileSystem, decisionTablePath);
            }
        }
        
        // Load specific rule files if configured
        if (!droolsProperties.getRuleFiles().isEmpty()) {
            for (String ruleFile : droolsProperties.getRuleFiles()) {
                logger.info("Loading specific rule file: {}", ruleFile);
                Resource resource = ResourceFactory.newClassPathResource(ruleFile);
                kieFileSystem.write(resource);
            }
        }
        
        // Load specific decision table files if configured
        if (!droolsProperties.getDecisionTableFiles().isEmpty()) {
            for (String tableFile : droolsProperties.getDecisionTableFiles()) {
                logger.info("Loading specific decision table file: {}", tableFile);
                Resource resource = ResourceFactory.newClassPathResource(tableFile);
                kieFileSystem.write(resource);
            }
        }
        
        // Load external rule files if configured
        if (droolsProperties.getExternalRulePath() != null && !droolsProperties.getExternalRulePath().isEmpty()) {
            loadExternalRuleFiles(kieFileSystem);
        }
        
        logger.info("Finished loading rule files for reload");
    }
    
    /**
     * Loads DRL files from a classpath directory.
     *
     * @param kieFileSystem The KieFileSystem to load the rules into
     * @param rulePath The classpath directory to load DRL files from
     * @throws IOException if there is an error loading resources
     */
    private void loadClasspathDrlFiles(KieFileSystem kieFileSystem, String rulePath) throws IOException {
        // This is a simplified implementation
        // In a real implementation, you would use ClassPathResource or similar to load the files
        logger.info("Loading DRL files from classpath: {}", rulePath);
        
        // For now, we'll just rely on the specific rule files configured in droolsProperties
        // A more complete implementation would scan the classpath for .drl files
    }
    
    /**
     * Loads DRL files from a file system directory.
     *
     * @param kieFileSystem The KieFileSystem to load the rules into
     * @param rulePath The file system directory to load DRL files from
     * @throws IOException if there is an error loading resources
     */
    private void loadFileSystemDrlFiles(KieFileSystem kieFileSystem, String rulePath) throws IOException {
        Path path = Paths.get(rulePath);
        if (Files.exists(path) && Files.isDirectory(path)) {
            logger.info("Loading DRL files from file system: {}", path);
            
            // Find all .drl files in the directory
            List<Path> drlFiles = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".drl"))
                    .collect(Collectors.toList());
            
            for (Path drlFile : drlFiles) {
                String fileName = drlFile.getFileName().toString();
                logger.info("Loading DRL file: {}", fileName);
                
                // Read the file content
                byte[] content = Files.readAllBytes(drlFile);
                
                // Add the file to KieFileSystem
                String drlPath = "src/main/resources/rules/" + fileName;
                kieFileSystem.write(drlPath, content);
            }
        } else {
            logger.warn("Rule path does not exist or is not a directory: {}", path);
        }
    }
    
    /**
     * Loads decision tables from a classpath directory.
     *
     * @param kieFileSystem The KieFileSystem to load the decision tables into
     * @param decisionTablePath The classpath directory to load decision tables from
     * @throws IOException if there is an error loading resources
     */
    private void loadClasspathDecisionTables(KieFileSystem kieFileSystem, String decisionTablePath) throws IOException {
        // This is a simplified implementation
        // In a real implementation, you would use ClassPathResource or similar to load the files
        logger.info("Loading decision tables from classpath: {}", decisionTablePath);
        
        // For now, we'll just rely on the specific decision table files configured in droolsProperties
        // A more complete implementation would scan the classpath for .xls and .xlsx files
    }
    
    /**
     * Loads decision tables from a file system directory.
     *
     * @param kieFileSystem The KieFileSystem to load the decision tables into
     * @param decisionTablePath The file system directory to load decision tables from
     * @throws IOException if there is an error loading resources
     */
    private void loadFileSystemDecisionTables(KieFileSystem kieFileSystem, String decisionTablePath) throws IOException {
        Path path = Paths.get(decisionTablePath);
        if (Files.exists(path) && Files.isDirectory(path)) {
            logger.info("Loading decision tables from file system: {}", path);
            
            // Find all .xls and .xlsx files in the directory
            List<Path> tableFiles = Files.walk(path)
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String fileName = p.toString();
                        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
                    })
                    .collect(Collectors.toList());
            
            for (Path tableFile : tableFiles) {
                String fileName = tableFile.getFileName().toString();
                logger.info("Loading decision table: {}", fileName);
                
                // Convert decision table to DRL
                Resource resource = ResourceFactory.newFileResource(tableFile.toFile());
                DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
                String drl = decisionTableProvider.loadFromResource(resource, null);
                
                // Add the generated DRL to KieFileSystem
                String drlPath = "src/main/resources/generated-rules/" + fileName + ".drl";
                kieFileSystem.write(drlPath, drl);
            }
        } else {
            logger.warn("Decision table path does not exist or is not a directory: {}", path);
        }
    }
    
    /**
     * Loads rule files from an external directory.
     * This method scans the configured external rule path for rule files with the supported extensions.
     *
     * @param kieFileSystem The KieFileSystem to load the rules into
     * @throws IOException if there is an error loading resources
     */
    private void loadExternalRuleFiles(KieFileSystem kieFileSystem) throws IOException {
        Path externalPath = Paths.get(droolsProperties.getExternalRulePath());
        
        if (!Files.exists(externalPath)) {
            logger.warn("External rule path does not exist: {}", externalPath);
            return;
        }
        
        if (!Files.isDirectory(externalPath)) {
            logger.warn("External rule path is not a directory: {}", externalPath);
            return;
        }
        
        logger.info("Loading rules from external path: {}", externalPath);
        
        // Parse file extensions
        List<String> extensions = Arrays.asList(droolsProperties.getFileExtensions().split(","));
        
        // Walk the directory tree if subdirectories should be scanned
        List<Path> filesToProcess;
        if (droolsProperties.isScanSubdirectories()) {
            filesToProcess = Files.walk(externalPath)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } else {
            filesToProcess = Files.list(externalPath)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        
        // Process each file with supported extensions
        for (Path filePath : filesToProcess) {
            String fileName = filePath.getFileName().toString();
            
            // Check if the file has a supported extension
            boolean hasSupportedExtension = extensions.stream()
                    .anyMatch(ext -> fileName.endsWith(ext.trim()));
            
            if (hasSupportedExtension) {
                logger.info("Loading external rule file: {}", fileName);
                
                // Handle different file types
                if (fileName.endsWith(".drl")) {
                    // Load DRL file
                    byte[] content = Files.readAllBytes(filePath);
                    String drlPath = "src/main/resources/external-rules/" + fileName;
                    kieFileSystem.write(drlPath, content);
                } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    // Load decision table
                    Resource resource = ResourceFactory.newFileResource(filePath.toFile());
                    DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
                    String drl = decisionTableProvider.loadFromResource(resource, null);
                    
                    // Write the generated DRL to KieFileSystem
                    String drlPath = "src/main/resources/external-rules/" + fileName + ".drl";
                    kieFileSystem.write(drlPath, drl);
                }
            }
        }
        
        logger.info("Finished loading external rule files");
    }
    
    /**
     * Creates a backup of existing rules.
     * This method copies all rule files to a backup directory with a timestamp.
     */
    private void createBackup() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String backupPath = backupDir + File.separator + "backup_" + timestamp;
        
        try {
            // Create backup directory
            createDirectoryIfNotExists(backupPath);
        } catch (Exception e) {
            logger.error("Error creating backup directory", e);
            
            // Log backup failure
            ruleAuditService.logBackupEvent(
                    backupDir,
                    "system", // In a real app, this would be the authenticated user
                    false,
                    "Error creating backup directory: " + e.getMessage()
            );
            return;
        }
        
        boolean success = true;
        StringBuilder backupDetails = new StringBuilder();
        int fileCount = 0;
        
        // Backup external rules if configured
        if (droolsProperties.getExternalRulePath() != null && !droolsProperties.getExternalRulePath().isEmpty()) {
            Path externalPath = Paths.get(droolsProperties.getExternalRulePath());
            if (Files.exists(externalPath) && Files.isDirectory(externalPath)) {
                String externalBackupPath = backupPath + File.separator + "external";
                try {
                    createDirectoryIfNotExists(externalBackupPath);
                    
                    // Copy files
                    List<Path> externalFiles = Files.walk(externalPath)
                            .filter(Files::isRegularFile)
                            .collect(Collectors.toList());
                    
                    for (Path source : externalFiles) {
                        try {
                            Path relativePath = externalPath.relativize(source);
                            Path destination = Paths.get(externalBackupPath, relativePath.toString());
                            
                            // Create parent directories if they don't exist
                            if (destination.getParent() != null) {
                                Files.createDirectories(destination.getParent());
                            }
                            
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                            fileCount++;
                        } catch (IOException e) {
                            logger.error("Error backing up file: {}", source, e);
                            success = false;
                            backupDetails.append("Failed to backup: ").append(source).append("; ");
                        }
                    }
                    
                    backupDetails.append("External files: ").append(externalFiles.size()).append("; ");
                } catch (IOException e) {
                    logger.error("Error processing external rule path", e);
                    success = false;
                    backupDetails.append("Failed to process external path; ");
                }
            }
        }
        
        // Backup uploaded rules
        Path uploadPath = Paths.get(uploadDir);
        if (Files.exists(uploadPath) && Files.isDirectory(uploadPath)) {
            String uploadBackupPath = backupPath + File.separator + "uploads";
            try {
                createDirectoryIfNotExists(uploadBackupPath);
                
                // Copy files
                List<Path> uploadFiles = Files.walk(uploadPath)
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
                
                for (Path source : uploadFiles) {
                    try {
                        Path relativePath = uploadPath.relativize(source);
                        Path destination = Paths.get(uploadBackupPath, relativePath.toString());
                        
                        // Create parent directories if they don't exist
                        if (destination.getParent() != null) {
                            Files.createDirectories(destination.getParent());
                        }
                        
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        fileCount++;
                    } catch (IOException e) {
                        logger.error("Error backing up file: {}", source, e);
                        success = false;
                        backupDetails.append("Failed to backup: ").append(source).append("; ");
                    }
                }
                
                backupDetails.append("Upload files: ").append(uploadFiles.size());
            } catch (IOException e) {
                logger.error("Error processing upload path", e);
                success = false;
                backupDetails.append("Failed to process upload path; ");
            }
        }
        
        logger.info("Created rule backup at: {}", backupPath);
        
        // Log backup event
        String message = success ? 
                String.format("Backup created successfully at %s (%d files)", backupPath, fileCount) :
                String.format("Backup created with errors at %s (%s)", backupPath, backupDetails.toString());
        
        ruleAuditService.logBackupEvent(
                backupPath,
                "system", // In a real app, this would be the authenticated user
                success,
                message
        );
    }
    
    // The saveFile method has been replaced by SecureFileStorage.storeFile
    
    /**
     * Creates a directory if it doesn't exist.
     *
     * @param directory The directory to create
     */
    private void createDirectoryIfNotExists(String directory) {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                logger.info("Created directory: {}", directory);
            } catch (IOException e) {
                logger.error("Error creating directory: {}", directory, e);
            }
        }
    }
    
    /**
     * Generates a version string based on the current timestamp.
     *
     * @return A version string
     */
    private String generateVersionString() {
        return LocalDateTime.now().format(DATE_FORMATTER) + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Gets the file extension from a filename.
     *
     * @param filename The filename
     * @return The file extension (including the dot)
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * Gets the list of supported file extensions.
     *
     * @return A list of supported file extensions
     */
    private List<String> getSupportedExtensions() {
        return List.of(".drl", ".xls", ".xlsx");
    }
}