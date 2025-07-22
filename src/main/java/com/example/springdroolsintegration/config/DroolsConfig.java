package com.example.springdroolsintegration.config;

import org.drools.decisiontable.DecisionTableProviderImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration class for Drools rule engine.
 * This class configures KieContainer, KieSession, and other Drools components.
 */
@Configuration
public class DroolsConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DroolsConfig.class);
    
    private final DroolsProperties droolsProperties;
    private final ResourceLoader resourceLoader;
    private final KieServices kieServices;
    
    /**
     * Constructor for DroolsConfig.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param droolsProperties Configuration properties for Drools
     * @param resourceLoader Spring ResourceLoader for loading rule files
     */
    public DroolsConfig(DroolsProperties droolsProperties, ResourceLoader resourceLoader) {
        this.droolsProperties = droolsProperties;
        this.resourceLoader = resourceLoader;
        this.kieServices = KieServices.Factory.get();
        
        logger.info("Initializing Drools configuration with properties: rulePath={}, decisionTablePath={}",
                droolsProperties.getRulePath(), droolsProperties.getDecisionTablePath());
    }
    
    /**
     * Creates a KieFileSystem bean that loads rule files from the configured paths.
     * This includes both .drl files and decision tables (.xls, .xlsx).
     *
     * @return KieFileSystem with loaded rule resources
     * @throws IOException if there is an error loading resources
     */
    @Bean
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        
        // Load DRL files from rulePath
        loadDrlFiles(kieFileSystem);
        
        // Load decision tables from decisionTablePath
        loadDecisionTables(kieFileSystem);
        
        // Load specific rule files if configured
        if (!droolsProperties.getRuleFiles().isEmpty()) {
            for (String ruleFile : droolsProperties.getRuleFiles()) {
                logger.debug("Loading specific rule file: {}", ruleFile);
                Resource resource = ResourceFactory.newClassPathResource(ruleFile);
                kieFileSystem.write(resource);
            }
        }
        
        // Load specific decision table files if configured
        if (!droolsProperties.getDecisionTableFiles().isEmpty()) {
            for (String tableFile : droolsProperties.getDecisionTableFiles()) {
                logger.debug("Loading specific decision table file: {}", tableFile);
                Resource resource = ResourceFactory.newClassPathResource(tableFile);
                kieFileSystem.write(resource);
            }
        }
        
        // Load external rule files if configured
        if (droolsProperties.getExternalRulePath() != null && !droolsProperties.getExternalRulePath().isEmpty()) {
            logger.info("Loading rules from external path: {}", droolsProperties.getExternalRulePath());
            loadExternalRuleFiles(kieFileSystem);
        }
        
        return kieFileSystem;
    }
    
    /**
     * Creates a KieBuilder bean that compiles the rules in the KieFileSystem.
     *
     * @param kieFileSystem The KieFileSystem containing rule resources
     * @return KieBuilder with compiled rules
     */
    @Bean
    public KieBuilder kieBuilder(KieFileSystem kieFileSystem) {
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        
        // Check for errors in rule compilation
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            logger.error("Rule compilation errors: {}", kieBuilder.getResults().getMessages());
            throw new RuntimeException("Rule compilation error: " + kieBuilder.getResults().getMessages());
        }
        
        logger.info("Rules compiled successfully");
        return kieBuilder;
    }
    
    /**
     * Creates a KieContainer bean that holds the compiled rules.
     *
     * @param kieBuilder The KieBuilder with compiled rules
     * @return KieContainer with the compiled rules
     */
    @Bean
    public KieContainer kieContainer(KieBuilder kieBuilder) {
        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        
        logger.info("KieContainer created with default release ID: {}", kieRepository.getDefaultReleaseId());
        return kieContainer;
    }
    
    /**
     * Creates a KieBase bean from the KieContainer.
     * This method configures the KieBase with the specified rule packages.
     *
     * @param kieContainer The KieContainer with compiled rules
     * @return KieBase for rule execution
     */
    @Bean
    public KieBase kieBase(KieContainer kieContainer) {
        // If no rule packages are specified, use the default KieBase
        if (droolsProperties.getRulePackages() == null || droolsProperties.getRulePackages().isEmpty()) {
            logger.info("Using default KieBase: {}", droolsProperties.getKieBaseName());
            return kieContainer.getKieBase(droolsProperties.getKieBaseName());
        }
        
        // Create a KieBase configuration
        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        
        // Set event processing mode (STREAM or CLOUD)
        kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
        
        // Create a KieBase with the specified packages
        KieBase kieBase = kieContainer.newKieBase(droolsProperties.getKieBaseName(), kieBaseConfiguration);
        
        // Log the packages included in the KieBase
        logger.info("Created KieBase with packages: {}", droolsProperties.getRulePackages());
        
        return kieBase;
    }
    
    /**
     * Loads DRL files from the configured rule path.
     *
     * @param kieFileSystem The KieFileSystem to load the rules into
     * @throws IOException if there is an error loading resources
     */
    private void loadDrlFiles(KieFileSystem kieFileSystem) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        
        // Parse file extensions
        List<String> extensions = Arrays.asList(droolsProperties.getFileExtensions().split(","));
        
        // Filter for .drl files
        for (String extension : extensions) {
            if (extension.trim().equals(".drl")) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + 
                        droolsProperties.getRulePath().replaceFirst("classpath:", "") + "**/*" + extension.trim();
                
                logger.debug("Searching for DRL files with pattern: {}", pattern);
                org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources(pattern);
                
                for (org.springframework.core.io.Resource resource : resources) {
                    logger.info("Loading DRL file: {}", resource.getFilename());
                    kieFileSystem.write(ResourceFactory.newClassPathResource(resource.getFilename()));
                }
            }
        }
    }
    
    /**
     * Loads decision table files from the configured decision table path.
     *
     * @param kieFileSystem The KieFileSystem to load the decision tables into
     * @throws IOException if there is an error loading resources
     */
    private void loadDecisionTables(KieFileSystem kieFileSystem) throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        
        // Parse file extensions
        List<String> extensions = Arrays.asList(droolsProperties.getFileExtensions().split(","));
        
        // Filter for Excel files (.xls, .xlsx)
        for (String extension : extensions) {
            if (extension.trim().equals(".xls") || extension.trim().equals(".xlsx")) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + 
                        droolsProperties.getDecisionTablePath().replaceFirst("classpath:", "") + "**/*" + extension.trim();
                
                logger.debug("Searching for decision table files with pattern: {}", pattern);
                org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources(pattern);
                
                for (org.springframework.core.io.Resource resource : resources) {
                    logger.info("Loading decision table: {}", resource.getFilename());
                    DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
                    
                    // Convert decision table to DRL
                    Resource droolsResource = ResourceFactory.newClassPathResource(
                            droolsProperties.getDecisionTablePath().replaceFirst("classpath:", "") + resource.getFilename());
                    String drl = decisionTableProvider.loadFromResource(droolsResource, null);
                    
                    // Write the generated DRL to KieFileSystem
                    String drlPath = "src/main/resources/generated-rules/" + resource.getFilename() + ".drl";
                    logger.debug("Generated DRL from decision table: {}", drlPath);
                    kieFileSystem.write(drlPath, drl);
                }
            }
        }
    }
    
    /**
     * Loads rule files from an external directory.
     * This method scans the configured external rule path for rule files with the supported extensions.
     *
     * @param kieFileSystem The KieFileSystem to load the rules into
     */
    private void loadExternalRuleFiles(KieFileSystem kieFileSystem) {
        try {
            Path externalPath = Paths.get(droolsProperties.getExternalRulePath());
            
            if (!Files.exists(externalPath)) {
                logger.warn("External rule path does not exist: {}", externalPath);
                return;
            }
            
            if (!Files.isDirectory(externalPath)) {
                logger.warn("External rule path is not a directory: {}", externalPath);
                return;
            }
            
            logger.info("Scanning external rule path: {}", externalPath);
            
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
                        String drl = new String(Files.readAllBytes(filePath));
                        String drlPath = "src/main/resources/external-rules/" + fileName;
                        kieFileSystem.write(drlPath, drl);
                    } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                        // Load decision table
                        DecisionTableProviderImpl decisionTableProvider = new DecisionTableProviderImpl();
                        Resource droolsResource = ResourceFactory.newFileResource(filePath.toFile());
                        String drl = decisionTableProvider.loadFromResource(droolsResource, null);
                        
                        // Write the generated DRL to KieFileSystem
                        String drlPath = "src/main/resources/external-rules/" + fileName + ".drl";
                        logger.debug("Generated DRL from external decision table: {}", drlPath);
                        kieFileSystem.write(drlPath, drl);
                    }
                }
            }
            
            logger.info("Finished loading external rule files");
        } catch (IOException e) {
            logger.error("Error loading external rule files", e);
            throw new RuntimeException("Error loading external rule files", e);
        }
    }
}