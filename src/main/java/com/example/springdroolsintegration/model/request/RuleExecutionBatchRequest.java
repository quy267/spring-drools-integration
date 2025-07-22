package com.example.springdroolsintegration.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request model for batch rule execution.
 * This class wraps a list of fact objects to be used for batch rule execution and adds validation.
 */
@Schema(description = "Request model for batch rule execution")
public class RuleExecutionBatchRequest {
    
    /**
     * The list of fact objects to execute rules on.
     * Each object in the list will have rules executed against it.
     */
    @Schema(description = "The list of fact objects to execute rules on", required = true)
    @NotNull(message = "Facts list cannot be null")
    @NotEmpty(message = "Facts list cannot be empty")
    @Size(max = 1000, message = "Facts list cannot contain more than 1000 items")
    private List<Object> facts;
    
    /**
     * The rule package to execute (optional).
     * If not provided, all applicable rules will be executed.
     */
    @Schema(description = "The rule package to execute (optional)", example = "com.example.rules.discount")
    private String rulePackage;
    
    /**
     * Default constructor
     */
    public RuleExecutionBatchRequest() {
    }
    
    /**
     * Constructor with facts list
     *
     * @param facts The list of fact objects to execute rules on
     */
    public RuleExecutionBatchRequest(List<Object> facts) {
        this.facts = facts;
    }
    
    /**
     * Constructor with facts list and rule package
     *
     * @param facts The list of fact objects to execute rules on
     * @param rulePackage The rule package to execute
     */
    public RuleExecutionBatchRequest(List<Object> facts, String rulePackage) {
        this.facts = facts;
        this.rulePackage = rulePackage;
    }
    
    /**
     * Gets the facts list
     *
     * @return The facts list
     */
    public List<Object> getFacts() {
        return facts;
    }
    
    /**
     * Sets the facts list
     *
     * @param facts The facts list
     */
    public void setFacts(List<Object> facts) {
        this.facts = facts;
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