package com.example.springdroolsintegration.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request model for rule execution.
 * This class wraps the fact object to be used for rule execution and adds validation.
 */
@Schema(description = "Request model for rule execution")
public class RuleExecutionRequest {
    
    /**
     * The fact object to execute rules on.
     * This can be any object that the rules will operate on.
     */
    @Schema(description = "The fact object to execute rules on", required = true)
    @NotNull(message = "Fact object cannot be null")
    private Object fact;
    
    /**
     * The rule package to execute (optional).
     * If not provided, all applicable rules will be executed.
     */
    @Schema(description = "The rule package to execute (optional)", example = "com.example.rules.discount")
    private String rulePackage;
    
    /**
     * Default constructor
     */
    public RuleExecutionRequest() {
    }
    
    /**
     * Constructor with fact object
     *
     * @param fact The fact object to execute rules on
     */
    public RuleExecutionRequest(Object fact) {
        this.fact = fact;
    }
    
    /**
     * Constructor with fact object and rule package
     *
     * @param fact The fact object to execute rules on
     * @param rulePackage The rule package to execute
     */
    public RuleExecutionRequest(Object fact, String rulePackage) {
        this.fact = fact;
        this.rulePackage = rulePackage;
    }
    
    /**
     * Gets the fact object
     *
     * @return The fact object
     */
    public Object getFact() {
        return fact;
    }
    
    /**
     * Sets the fact object
     *
     * @param fact The fact object
     */
    public void setFact(Object fact) {
        this.fact = fact;
    }
    
    /**
     * Gets the rule package
     *
     * @return The rule package
     */
    public String getRulePackage() {
        return rulePackage;
    }
    
    /**
     * Sets the rule package
     *
     * @param rulePackage The rule package
     */
    public void setRulePackage(String rulePackage) {
        this.rulePackage = rulePackage;
    }
}