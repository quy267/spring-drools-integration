package com.example.springdroolsintegration.health;

import com.example.springdroolsintegration.config.DroolsProperties;
import com.example.springdroolsintegration.util.LoggingUtils;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator for the Drools rule engine.
 * This health indicator checks the status of the Drools rule engine
 * and reports health information to Spring Boot Actuator.
 */
@Component
public class DroolsHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DroolsHealthIndicator.class);
    
    private final KieContainer kieContainer;
    private final KieBase kieBase;
    private final DroolsProperties droolsProperties;
    
    /**
     * Constructor for DroolsHealthIndicator.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieContainer The KieContainer for rule execution
     * @param kieBase The KieBase for rule execution
     * @param droolsProperties Configuration properties for Drools
     */
    public DroolsHealthIndicator(KieContainer kieContainer, 
                                KieBase kieBase,
                                DroolsProperties droolsProperties) {
        this.kieContainer = kieContainer;
        this.kieBase = kieBase;
        this.droolsProperties = droolsProperties;
        
        LoggingUtils.logInfo(logger, "DroolsHealthIndicator initialized");
    }
    
    /**
     * Checks the health of the Drools rule engine.
     * This method verifies that the KieContainer and KieBase are functioning properly
     * and that a KieSession can be created and disposed.
     *
     * @return Health information for the Drools rule engine
     */
    @Override
    public Health health() {
        try {
            // Check if KieContainer is available
            if (kieContainer == null) {
                LoggingUtils.logError(logger, "KieContainer is null");
                return Health.down()
                        .withDetail("error", "KieContainer is not available")
                        .build();
            }
            
            // Check if KieBase is available
            if (kieBase == null) {
                LoggingUtils.logError(logger, "KieBase is null");
                return Health.down()
                        .withDetail("error", "KieBase is not available")
                        .build();
            }
            
            // Try to create a KieSession
            KieSession kieSession = null;
            try {
                kieSession = kieBase.newKieSession();
                if (kieSession == null) {
                    LoggingUtils.logError(logger, "Failed to create KieSession");
                    return Health.down()
                            .withDetail("error", "Failed to create KieSession")
                            .build();
                }
                
                // Collect health details
                Map<String, Object> details = collectHealthDetails();
                
                return Health.up()
                        .withDetails(details)
                        .build();
            } finally {
                // Dispose the KieSession
                if (kieSession != null) {
                    kieSession.dispose();
                }
            }
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error checking Drools health", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getName())
                    .build();
        }
    }
    
    /**
     * Collects detailed health information about the Drools rule engine.
     *
     * @return A map of health details
     */
    private Map<String, Object> collectHealthDetails() {
        Map<String, Object> details = new HashMap<>();
        
        // Add KieBase information
        details.put("kieBaseName", droolsProperties.getKieBaseName());
        details.put("kieSessionName", droolsProperties.getKieSessionName());
        
        // Add rule package information
        details.put("rulePackages", droolsProperties.getRulePackages());
        
        // Add rule count
        long ruleCount = kieBase.getKiePackages().stream()
                .mapToLong(pkg -> pkg.getRules().size())
                .sum();
        details.put("ruleCount", ruleCount);
        
        // Add configuration information
        details.put("hotReloadEnabled", droolsProperties.isHotReload());
        details.put("metricsEnabled", droolsProperties.isMetricsEnabled());
        
        return details;
    }
}