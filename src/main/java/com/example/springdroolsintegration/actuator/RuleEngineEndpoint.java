package com.example.springdroolsintegration.actuator;

import com.example.springdroolsintegration.service.KieSessionPoolService;
import com.example.springdroolsintegration.service.RuleExecutionHistoryService;
import com.example.springdroolsintegration.service.RuleExecutionService;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom actuator endpoint for rule engine metrics and information.
 * This endpoint exposes detailed information about the rule engine.
 */
@Component
@Endpoint(id = "rules")
public class RuleEngineEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(RuleEngineEndpoint.class);
    
    private final KieBase kieBase;
    private final RuleExecutionService ruleExecutionService;
    private final KieSessionPoolService sessionPoolService;
    private final RuleExecutionHistoryService ruleExecutionHistoryService;
    
    /**
     * Constructor for RuleEngineEndpoint.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param kieBase The KieBase for the rule engine
     * @param ruleExecutionService The rule execution service
     * @param sessionPoolService The KieSession pool service
     * @param ruleExecutionHistoryService The rule execution history service
     */
    public RuleEngineEndpoint(
            KieBase kieBase,
            RuleExecutionService ruleExecutionService,
            KieSessionPoolService sessionPoolService,
            RuleExecutionHistoryService ruleExecutionHistoryService) {
        this.kieBase = kieBase;
        this.ruleExecutionService = ruleExecutionService;
        this.sessionPoolService = sessionPoolService;
        this.ruleExecutionHistoryService = ruleExecutionHistoryService;
        logger.info("RuleEngineEndpoint initialized");
    }
    
    /**
     * Gets comprehensive information about the rule engine.
     *
     * @return Map of rule engine information
     */
    @ReadOperation
    public Map<String, Object> info() {
        logger.debug("Retrieving rule engine information");
        
        Map<String, Object> info = new HashMap<>();
        
        // Add KieBase information
        Map<String, Object> kieBaseInfo = new HashMap<>();
        kieBaseInfo.put("type", kieBase.getClass().getSimpleName());
        kieBaseInfo.put("packages", kieBase.getKiePackages().size());
        
        // Add package details
        Map<String, Integer> packageRuleCounts = new HashMap<>();
        kieBase.getKiePackages().forEach(pkg -> 
                packageRuleCounts.put(pkg.getName(), pkg.getRules().size()));
        kieBaseInfo.put("packageRuleCounts", packageRuleCounts);
        
        info.put("kieBase", kieBaseInfo);
        
        // Add execution statistics
        info.put("executionStats", ruleExecutionService.getExecutionStatistics());
        
        // Add session pool information
        Map<String, Object> sessionPoolInfo = new HashMap<>();
        sessionPoolInfo.put("poolSize", sessionPoolService.getPoolSize());
        sessionPoolInfo.put("totalSessionsCreated", sessionPoolService.getTotalSessionsCreated());
        sessionPoolInfo.put("totalSessionsBorrowed", sessionPoolService.getTotalSessionsBorrowed());
        sessionPoolInfo.put("totalSessionsReturned", sessionPoolService.getTotalSessionsReturned());
        info.put("sessionPool", sessionPoolInfo);
        
        // Add execution history statistics
        info.put("executionHistory", ruleExecutionHistoryService.getExecutionStatistics());
        
        // Add performance thresholds
        Map<String, Object> thresholds = new HashMap<>();
        thresholds.put("warningExecutionTimeMs", 100);
        thresholds.put("criticalExecutionTimeMs", 500);
        thresholds.put("maxPoolSize", 20);
        thresholds.put("minSuccessRate", 95.0);
        info.put("thresholds", thresholds);
        
        return info;
    }
    
    /**
     * Gets information about a specific aspect of the rule engine.
     *
     * @param aspect The aspect to get information about (e.g., "execution", "pool", "history")
     * @return Map of information about the specified aspect
     */
    @ReadOperation
    public Map<String, Object> infoForAspect(@Selector String aspect) {
        logger.debug("Retrieving rule engine information for aspect: {}", aspect);
        
        Map<String, Object> info = new HashMap<>();
        
        switch (aspect) {
            case "execution":
                info.put("executionStats", ruleExecutionService.getExecutionStatistics());
                break;
            case "pool":
                Map<String, Object> sessionPoolInfo = new HashMap<>();
                sessionPoolInfo.put("poolSize", sessionPoolService.getPoolSize());
                sessionPoolInfo.put("totalSessionsCreated", sessionPoolService.getTotalSessionsCreated());
                sessionPoolInfo.put("totalSessionsBorrowed", sessionPoolService.getTotalSessionsBorrowed());
                sessionPoolInfo.put("totalSessionsReturned", sessionPoolService.getTotalSessionsReturned());
                info.put("sessionPool", sessionPoolInfo);
                break;
            case "history":
                info.put("executionHistory", ruleExecutionHistoryService.getExecutionStatistics());
                break;
            case "packages":
                Map<String, Integer> packageRuleCounts = new HashMap<>();
                kieBase.getKiePackages().forEach(pkg -> 
                        packageRuleCounts.put(pkg.getName(), pkg.getRules().size()));
                info.put("packageRuleCounts", packageRuleCounts);
                break;
            case "thresholds":
                Map<String, Object> thresholds = new HashMap<>();
                thresholds.put("warningExecutionTimeMs", 100);
                thresholds.put("criticalExecutionTimeMs", 500);
                thresholds.put("maxPoolSize", 20);
                thresholds.put("minSuccessRate", 95.0);
                info.put("thresholds", thresholds);
                break;
            default:
                info.put("error", "Unknown aspect: " + aspect);
                info.put("availableAspects", 
                        List.of("execution", "pool", "history", "packages", "thresholds"));
                break;
        }
        
        return info;
    }
}