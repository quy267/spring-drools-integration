package com.example.springdroolsintegration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Drools rule engine.
 * This class maps the app.drools.* properties from application.properties.
 */
@Component
@ConfigurationProperties(prefix = "app.drools")
@Validated
public class DroolsProperties {
    
    // Rule file locations
    
    /**
     * Base path to rule files (.drl)
     */
    @NotBlank(message = "Rule path must not be blank")
    private String rulePath;
    
    /**
     * Base path to decision table files (.xls, .xlsx)
     */
    @NotBlank(message = "Decision table path must not be blank")
    private String decisionTablePath;
    
    /**
     * Supported file extensions for rules
     */
    @NotBlank(message = "File extensions must not be blank")
    @Pattern(regexp = "^(\\.[a-zA-Z0-9]+,?)+$", message = "File extensions must be comma-separated list starting with dots (e.g., .drl,.xls)")
    private String fileExtensions;
    
    /**
     * List of specific rule file paths to load
     */
    @NotNull(message = "Rule files list must not be null")
    private List<String> ruleFiles = new ArrayList<>();
    
    /**
     * List of specific decision table file paths to load
     */
    @NotNull(message = "Decision table files list must not be null")
    private List<String> decisionTableFiles = new ArrayList<>();
    
    /**
     * List of rule packages to include in the KieBase
     */
    @NotNull(message = "Rule packages list must not be null")
    private List<String> rulePackages = new ArrayList<>();
    
    /**
     * External directory path for rule files (outside classpath)
     */
    private String externalRulePath;
    
    /**
     * Whether to scan subdirectories for rule files
     */
    private boolean scanSubdirectories = true;
    
    // Hot reload configuration
    
    /**
     * Whether to enable hot-reload of rules
     */
    private boolean hotReload;
    
    /**
     * Interval for checking rule file changes (in milliseconds)
     */
    @Min(value = 1000, message = "Hot reload interval must be at least 1000 ms")
    private long hotReloadInterval;
    
    // KieSession configuration
    
    /**
     * Maximum number of KieSessions to keep in the pool
     */
    @Min(value = 1, message = "Session pool size must be at least 1")
    private int sessionPoolSize = 10;
    
    /**
     * Timeout for KieSession in milliseconds
     */
    @Min(value = 1000, message = "Session timeout must be at least 1000 ms")
    private long sessionTimeout = 300000;
    
    /**
     * Maximum number of rules per session
     */
    @Min(value = 1, message = "Max rules per session must be at least 1")
    private int maxRulesPerSession = 1000;
    
    /**
     * Whether to use stateless sessions
     */
    private boolean statelessSession = false;
    
    /**
     * KieBase name
     */
    @NotBlank(message = "KieBase name must not be blank")
    private String kieBaseName = "defaultKieBase";
    
    /**
     * KieSession name
     */
    @NotBlank(message = "KieSession name must not be blank")
    private String kieSessionName = "defaultKieSession";
    
    // Performance tuning parameters
    
    /**
     * Whether to enable rule execution metrics collection
     */
    private boolean metricsEnabled = true;
    
    /**
     * Whether to enable rule execution tracing
     */
    private boolean traceEnabled = false;
    
    /**
     * Maximum number of threads for parallel rule execution
     */
    @Min(value = 1, message = "Max execution threads must be at least 1")
    private int maxExecutionThreads = Runtime.getRuntime().availableProcessors();
    
    /**
     * Rule execution timeout in milliseconds
     */
    @Min(value = 100, message = "Rule execution timeout must be at least 100 ms")
    private long ruleExecutionTimeout = 10000;
    
    /**
     * Whether to cache compiled rules
     */
    private boolean cacheEnabled = true;
    
    // Default constructor
    public DroolsProperties() {
    }
    
    // Getters and setters
    
    public String getRulePath() {
        return rulePath;
    }
    
    public void setRulePath(String rulePath) {
        this.rulePath = rulePath;
    }
    
    public String getDecisionTablePath() {
        return decisionTablePath;
    }
    
    public void setDecisionTablePath(String decisionTablePath) {
        this.decisionTablePath = decisionTablePath;
    }
    
    public String getFileExtensions() {
        return fileExtensions;
    }
    
    public void setFileExtensions(String fileExtensions) {
        this.fileExtensions = fileExtensions;
    }
    
    public boolean isHotReload() {
        return hotReload;
    }
    
    public void setHotReload(boolean hotReload) {
        this.hotReload = hotReload;
    }
    
    public long getHotReloadInterval() {
        return hotReloadInterval;
    }
    
    public void setHotReloadInterval(long hotReloadInterval) {
        this.hotReloadInterval = hotReloadInterval;
    }
    
    public List<String> getRuleFiles() {
        return ruleFiles;
    }
    
    public void setRuleFiles(List<String> ruleFiles) {
        this.ruleFiles = ruleFiles;
    }
    
    public List<String> getDecisionTableFiles() {
        return decisionTableFiles;
    }
    
    public void setDecisionTableFiles(List<String> decisionTableFiles) {
        this.decisionTableFiles = decisionTableFiles;
    }
    
    public List<String> getRulePackages() {
        return rulePackages;
    }
    
    public void setRulePackages(List<String> rulePackages) {
        this.rulePackages = rulePackages;
    }
    
    public String getExternalRulePath() {
        return externalRulePath;
    }
    
    public void setExternalRulePath(String externalRulePath) {
        this.externalRulePath = externalRulePath;
    }
    
    public boolean isScanSubdirectories() {
        return scanSubdirectories;
    }
    
    public void setScanSubdirectories(boolean scanSubdirectories) {
        this.scanSubdirectories = scanSubdirectories;
    }
    
    public int getSessionPoolSize() {
        return sessionPoolSize;
    }
    
    public void setSessionPoolSize(int sessionPoolSize) {
        this.sessionPoolSize = sessionPoolSize;
    }
    
    public long getSessionTimeout() {
        return sessionTimeout;
    }
    
    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public int getMaxRulesPerSession() {
        return maxRulesPerSession;
    }
    
    public void setMaxRulesPerSession(int maxRulesPerSession) {
        this.maxRulesPerSession = maxRulesPerSession;
    }
    
    public boolean isStatelessSession() {
        return statelessSession;
    }
    
    public void setStatelessSession(boolean statelessSession) {
        this.statelessSession = statelessSession;
    }
    
    public String getKieBaseName() {
        return kieBaseName;
    }
    
    public void setKieBaseName(String kieBaseName) {
        this.kieBaseName = kieBaseName;
    }
    
    public String getKieSessionName() {
        return kieSessionName;
    }
    
    public void setKieSessionName(String kieSessionName) {
        this.kieSessionName = kieSessionName;
    }
    
    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }
    
    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }
    
    public boolean isTraceEnabled() {
        return traceEnabled;
    }
    
    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }
    
    public int getMaxExecutionThreads() {
        return maxExecutionThreads;
    }
    
    public void setMaxExecutionThreads(int maxExecutionThreads) {
        this.maxExecutionThreads = maxExecutionThreads;
    }
    
    public long getRuleExecutionTimeout() {
        return ruleExecutionTimeout;
    }
    
    public void setRuleExecutionTimeout(long ruleExecutionTimeout) {
        this.ruleExecutionTimeout = ruleExecutionTimeout;
    }
    
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }
    
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}