package com.example.springdroolsintegration.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service for hot-reloading rule files in development mode.
 * This service monitors rule files for changes and reloads them automatically.
 * It is only enabled when app.drools.hot-reload=true in the configuration.
 */
@Service
@ConditionalOnProperty(name = "app.drools.hot-reload", havingValue = "true")
public class RuleHotReloadService implements InitializingBean, DisposableBean {
    
    private static final Logger logger = LoggerFactory.getLogger(RuleHotReloadService.class);
    
    private final DroolsProperties droolsProperties;
    private final KieContainer kieContainer;
    private final KieServices kieServices;
    private final ResourceLoader resourceLoader;
    private final ApplicationEventPublisher eventPublisher;
    
    private FileAlterationMonitor monitor;
    private final AtomicBoolean reloadInProgress = new AtomicBoolean(false);
    
    /**
     * Constructor for RuleHotReloadService.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param droolsProperties Configuration properties for Drools
     * @param kieContainer KieContainer for rule execution
     * @param resourceLoader Spring ResourceLoader for loading rule files
     * @param eventPublisher Spring ApplicationEventPublisher for publishing events
     */
    public RuleHotReloadService(DroolsProperties droolsProperties, 
                               KieContainer kieContainer,
                               ResourceLoader resourceLoader,
                               ApplicationEventPublisher eventPublisher) {
        this.droolsProperties = droolsProperties;
        this.kieContainer = kieContainer;
        this.kieServices = KieServices.Factory.get();
        this.resourceLoader = resourceLoader;
        this.eventPublisher = eventPublisher;
        
        logger.info("Rule hot-reloading enabled with interval: {} ms", droolsProperties.getHotReloadInterval());
    }
    
    /**
     * Initializes the file monitoring service after the bean is created.
     * This method sets up file watchers for rule directories.
     *
     * @throws Exception if there is an error initializing the service
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        setupFileMonitoring();
    }
    
    /**
     * Cleans up resources when the bean is destroyed.
     * This method stops the file monitoring service.
     *
     * @throws Exception if there is an error cleaning up resources
     */
    @Override
    public void destroy() throws Exception {
        if (monitor != null) {
            logger.info("Stopping rule file monitoring");
            monitor.stop();
        }
    }
    
    /**
     * Sets up file monitoring for rule directories.
     * This method creates file watchers for both classpath and external rule directories.
     *
     * @throws Exception if there is an error setting up file monitoring
     */
    private void setupFileMonitoring() throws Exception {
        // Create a monitor with the configured interval
        monitor = new FileAlterationMonitor(droolsProperties.getHotReloadInterval());
        
        // Monitor classpath rule directory if possible
        try {
            setupClasspathMonitoring();
        } catch (IOException e) {
            logger.warn("Could not set up monitoring for classpath rules: {}", e.getMessage());
        }
        
        // Monitor external rule directory if configured
        if (droolsProperties.getExternalRulePath() != null && !droolsProperties.getExternalRulePath().isEmpty()) {
            setupExternalDirectoryMonitoring();
        }
        
        // Start the monitor if observers were added
        if (monitor.getObservers().iterator().hasNext()) {
            logger.info("Starting rule file monitoring");
            monitor.start();
        } else {
            logger.warn("No rule directories could be monitored for hot-reloading");
        }
    }
    
    /**
     * Sets up monitoring for classpath rule directories.
     * This method attempts to find the physical location of classpath resources.
     *
     * @throws IOException if there is an error accessing classpath resources
     */
    private void setupClasspathMonitoring() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        // Try to find the physical location of the rule path
        String rulePath = droolsProperties.getRulePath().replaceFirst("classpath:", "");
        Resource[] resources = resolver.getResources("classpath:" + rulePath);
        
        if (resources.length > 0) {
            for (Resource resource : resources) {
                if (resource.isFile()) {
                    File directory = resource.getFile();
                    if (directory.isDirectory()) {
                        addDirectoryMonitor(directory);
                    }
                }
            }
        }
        
        // Try to find the physical location of the decision table path
        String decisionTablePath = droolsProperties.getDecisionTablePath().replaceFirst("classpath:", "");
        resources = resolver.getResources("classpath:" + decisionTablePath);
        
        if (resources.length > 0) {
            for (Resource resource : resources) {
                if (resource.isFile()) {
                    File directory = resource.getFile();
                    if (directory.isDirectory()) {
                        addDirectoryMonitor(directory);
                    }
                }
            }
        }
    }
    
    /**
     * Sets up monitoring for an external rule directory.
     * This method creates a file watcher for the configured external rule path.
     */
    private void setupExternalDirectoryMonitoring() {
        Path externalPath = Paths.get(droolsProperties.getExternalRulePath());
        
        if (Files.exists(externalPath) && Files.isDirectory(externalPath)) {
            addDirectoryMonitor(externalPath.toFile());
        } else {
            logger.warn("External rule path does not exist or is not a directory: {}", externalPath);
        }
    }
    
    /**
     * Adds a directory monitor for the specified directory.
     * This method creates a file observer that watches for changes in rule files.
     *
     * @param directory The directory to monitor
     */
    private void addDirectoryMonitor(File directory) {
        logger.info("Setting up rule file monitoring for directory: {}", directory.getAbsolutePath());
        
        // Parse file extensions
        List<String> extensions = Arrays.asList(droolsProperties.getFileExtensions().split(","));
        
        // Create a file observer for the directory
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        
        // Add a listener for file changes
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver observer) {
                // Not used
            }
            
            @Override
            public void onDirectoryCreate(File directory) {
                // Not used
            }
            
            @Override
            public void onDirectoryChange(File directory) {
                // Not used
            }
            
            @Override
            public void onDirectoryDelete(File directory) {
                // Not used
            }
            
            @Override
            public void onFileCreate(File file) {
                handleFileChange(file);
            }
            
            @Override
            public void onFileChange(File file) {
                handleFileChange(file);
            }
            
            @Override
            public void onFileDelete(File file) {
                // Optionally handle file deletion
            }
            
            @Override
            public void onStop(FileAlterationObserver observer) {
                // Not used
            }
            
            /**
             * Handles a file change event.
             * This method checks if the changed file is a rule file and triggers a reload if necessary.
             *
             * @param file The changed file
             */
            private void handleFileChange(File file) {
                String fileName = file.getName();
                
                // Check if the file has a supported extension
                boolean hasSupportedExtension = extensions.stream()
                        .anyMatch(ext -> fileName.endsWith(ext.trim()));
                
                if (hasSupportedExtension) {
                    logger.info("Detected change in rule file: {}", fileName);
                    reloadRules();
                }
            }
        });
        
        // Add the observer to the monitor
        monitor.addObserver(observer);
    }
    
    /**
     * Reloads all rules when a change is detected.
     * This method rebuilds the KieContainer with the updated rules.
     */
    private void reloadRules() {
        // Use atomic boolean to prevent concurrent reloads
        if (reloadInProgress.compareAndSet(false, true)) {
            try {
                logger.info("Reloading rules due to file changes");
                
                // Create a new KieFileSystem
                KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
                
                // Load all rule files into the KieFileSystem
                // This is a simplified version - in a real implementation, you would reuse the loading logic from DroolsConfig
                try {
                    loadRuleFiles(kieFileSystem);
                } catch (IOException e) {
                    logger.error("Error loading rule files during hot-reload", e);
                    return;
                }
                
                // Build the new rules
                KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
                kieBuilder.buildAll();
                
                // Check for errors
                Results results = kieBuilder.getResults();
                if (results.hasMessages(Message.Level.ERROR)) {
                    logger.error("Rule compilation errors during hot-reload: {}", results.getMessages());
                    return;
                }
                
                // Update the KieContainer
                kieContainer.updateToVersion(kieServices.getRepository().getDefaultReleaseId());
                
                logger.info("Rules successfully reloaded");
                
                // Publish an event to notify other components
                eventPublisher.publishEvent(new RuleReloadEvent(this));
            } finally {
                reloadInProgress.set(false);
            }
        } else {
            logger.debug("Rule reload already in progress, skipping");
        }
    }
    
    /**
     * Loads rule files into the KieFileSystem.
     * This method loads rule files from classpath and external directories.
     *
     * @param kieFileSystem The KieFileSystem to load rules into
     * @throws IOException if there is an error loading rule files
     */
    private void loadRuleFiles(KieFileSystem kieFileSystem) throws IOException {
        logger.info("Loading rule files during hot-reload");
        
        // Use PathMatchingResourcePatternResolver to find rule files
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        // Load rule files from classpath
        try {
            // Load DRL files from rulePath
            String rulePath = droolsProperties.getRulePath().replaceFirst("classpath:", "");
            String pattern = "classpath:" + rulePath + "**/*.drl";
            logger.debug("Searching for DRL files with pattern: {}", pattern);
            
            Resource[] resources = resolver.getResources(pattern);
            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    logger.info("Loading DRL file: {}", resource.getFilename());
                    // Add the resource to KieFileSystem
                    kieFileSystem.write("src/main/resources/" + rulePath + resource.getFilename(), 
                            resource.getInputStream());
                }
            }
            
            // Load decision table files from decisionTablePath
            String decisionTablePath = droolsProperties.getDecisionTablePath().replaceFirst("classpath:", "");
            
            // Load .xls files
            pattern = "classpath:" + decisionTablePath + "**/*.xls";
            resources = resolver.getResources(pattern);
            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    logger.info("Loading XLS decision table: {}", resource.getFilename());
                    // Add the resource to KieFileSystem
                    kieFileSystem.write("src/main/resources/" + decisionTablePath + resource.getFilename(), 
                            resource.getInputStream());
                }
            }
            
            // Load .xlsx files
            pattern = "classpath:" + decisionTablePath + "**/*.xlsx";
            resources = resolver.getResources(pattern);
            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    logger.info("Loading XLSX decision table: {}", resource.getFilename());
                    // Add the resource to KieFileSystem
                    kieFileSystem.write("src/main/resources/" + decisionTablePath + resource.getFilename(), 
                            resource.getInputStream());
                }
            }
        } catch (IOException e) {
            logger.error("Error loading classpath rule files", e);
            throw e;
        }
        
        // Load external rule files if configured
        if (droolsProperties.getExternalRulePath() != null && !droolsProperties.getExternalRulePath().isEmpty()) {
            try {
                Path externalPath = Paths.get(droolsProperties.getExternalRulePath());
                
                if (Files.exists(externalPath) && Files.isDirectory(externalPath)) {
                    logger.info("Loading rules from external path: {}", externalPath);
                    
                    // Walk the directory tree if subdirectories should be scanned
                    Files.walk(externalPath)
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            String fileName = filePath.getFileName().toString();
                            
                            // Check if the file has a supported extension
                            if (fileName.endsWith(".drl") || fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                                try {
                                    logger.info("Loading external rule file: {}", fileName);
                                    
                                    // Add the file to KieFileSystem
                                    kieFileSystem.write("src/main/resources/external-rules/" + fileName, 
                                            Files.readAllBytes(filePath));
                                } catch (IOException e) {
                                    logger.error("Error loading external rule file: {}", filePath, e);
                                }
                            }
                        });
                } else {
                    logger.warn("External rule path does not exist or is not a directory: {}", externalPath);
                }
            } catch (IOException e) {
                logger.error("Error loading external rule files", e);
                throw e;
            }
        }
        
        logger.info("Finished loading rule files");
    }
    
    /**
     * Event class for rule reload notifications.
     * This event is published when rules are successfully reloaded.
     */
    public static class RuleReloadEvent {
        private final Object source;
        
        public RuleReloadEvent(Object source) {
            this.source = source;
        }
        
        public Object getSource() {
            return source;
        }
    }
}