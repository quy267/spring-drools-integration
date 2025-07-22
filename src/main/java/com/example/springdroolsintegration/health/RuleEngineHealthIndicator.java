package com.example.springdroolsintegration.health;

import com.example.springdroolsintegration.service.RuleExecutionService;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Custom health indicator for the rule engine.
 * This health indicator checks the status of the rule engine and provides health information.
 */
@Component
public class RuleEngineHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineHealthIndicator.class);
    
    private final KieBase kieBase;
    private final RuleExecutionService ruleExecutionService;
    
    /**
     * Constructor for RuleEngineHealthIndicator.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieBase The KieBase for the rule engine
     * @param ruleExecutionService The rule execution service
     */
    public RuleEngineHealthIndicator(KieBase kieBase, RuleExecutionService ruleExecutionService) {
        this.kieBase = kieBase;
        this.ruleExecutionService = ruleExecutionService;
        logger.info("RuleEngineHealthIndicator initialized");
    }
    
    @Override
    public Health health() {
        try {
            // Check if KieBase is available
            if (kieBase == null) {
                logger.warn("KieBase is null");
                return Health.down()
                        .withDetail("error", "KieBase is not available")
                        .build();
            }
            
            // Check if we can create a KieSession
            KieSession kieSession = null;
            try {
                kieSession = ruleExecutionService.createSession();
                if (kieSession == null) {
                    logger.warn("Failed to create KieSession");
                    return Health.down()
                            .withDetail("error", "Failed to create KieSession")
                            .build();
                }
            } finally {
                if (kieSession != null) {
                    ruleExecutionService.disposeSession(kieSession);
                }
            }
            
            // Get rule execution statistics
            Map<String, Object> statistics = ruleExecutionService.getExecutionStatistics();
            
            // Build health information
            Health.Builder builder = Health.up()
                    .withDetail("kieBase", kieBase.getClass().getSimpleName())
                    .withDetail("packages", kieBase.getKiePackages().size())
                    .withDetail("totalExecutions", statistics.get("totalExecutions"))
                    .withDetail("totalBatchExecutions", statistics.get("totalBatchExecutions"))
                    .withDetail("sessionsCreated", statistics.get("sessionsCreated"))
                    .withDetail("sessionsDisposed", statistics.get("sessionsDisposed"));
            
            // Add session pool statistics if available
            if (statistics.containsKey("sessionPool")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> poolStats = (Map<String, Object>) statistics.get("sessionPool");
                builder.withDetail("sessionPoolSize", poolStats.get("poolSize"))
                       .withDetail("totalSessionsCreated", poolStats.get("totalSessionsCreated"))
                       .withDetail("totalSessionsBorrowed", poolStats.get("totalSessionsBorrowed"))
                       .withDetail("totalSessionsReturned", poolStats.get("totalSessionsReturned"));
            }
            
            return builder.build();
        } catch (Exception e) {
            logger.error("Error checking rule engine health", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}