package com.example.springdroolsintegration.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for customer discount rule execution response.
 * This class returns the results of discount rule evaluation, including
 * applied discounts and final amounts.
 */
@Schema(description = "Response model for customer discount rule evaluation results")
public class CustomerDiscountResponse {
    
    /**
     * The customer's ID
     */
    @Schema(description = "Customer ID", example = "1001")
    private Long customerId;
    
    /**
     * The customer's name
     */
    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;
    
    /**
     * The customer's loyalty tier
     */
    @Schema(description = "Customer loyalty tier", example = "GOLD")
    private String loyaltyTier;
    
    /**
     * The original order amount before discounts
     */
    @Schema(description = "Original order amount before discounts", example = "125.50")
    private double originalAmount;
    
    /**
     * The total discount percentage applied
     */
    @Schema(description = "Total discount percentage applied", example = "15.0")
    private double discountPercentage;
    
    /**
     * The discount amount in currency units
     */
    @Schema(description = "Discount amount in currency units", example = "18.83")
    private double discountAmount;
    
    /**
     * The final amount after applying discounts
     */
    @Schema(description = "Final amount after applying discounts", example = "106.67")
    private double finalAmount;
    
    /**
     * The names of the rules that were applied
     */
    @Schema(description = "Names of the rules that were applied", example = "Gold Tier Discount")
    private String appliedRules;
    
    /**
     * List of individual discounts that were applied
     */
    @Schema(description = "List of individual discounts that were applied")
    private List<DiscountDetail> discounts = new ArrayList<>();
    
    /**
     * The order ID if available
     */
    @Schema(description = "Order ID if available", example = "ORD-12345")
    private String orderId;
    
    /**
     * The timestamp when the discount was calculated
     */
    @Schema(description = "Timestamp when the discount was calculated")
    private LocalDateTime timestamp;
    
    /**
     * Any additional information or notes about the discount calculation
     */
    @Schema(description = "Additional information or notes about the discount calculation")
    private String notes;
    
    /**
     * Default constructor
     */
    public CustomerDiscountResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param originalAmount The original order amount
     * @param discountPercentage The discount percentage applied
     * @param finalAmount The final amount after discounts
     * @param appliedRules The names of the applied rules
     */
    public CustomerDiscountResponse(Long customerId, String customerName, double originalAmount,
                                   double discountPercentage, double finalAmount, String appliedRules) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.originalAmount = originalAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = originalAmount - finalAmount;
        this.finalAmount = finalAmount;
        this.appliedRules = appliedRules;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Full constructor with all fields
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param loyaltyTier The customer's loyalty tier
     * @param originalAmount The original order amount
     * @param discountPercentage The discount percentage applied
     * @param discountAmount The discount amount in currency units
     * @param finalAmount The final amount after discounts
     * @param appliedRules The names of the applied rules
     * @param discounts List of individual discounts
     * @param orderId The order ID
     * @param timestamp The timestamp of calculation
     * @param notes Additional notes
     */
    public CustomerDiscountResponse(Long customerId, String customerName, String loyaltyTier,
                                   double originalAmount, double discountPercentage, double discountAmount,
                                   double finalAmount, String appliedRules, List<DiscountDetail> discounts,
                                   String orderId, LocalDateTime timestamp, String notes) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.loyaltyTier = loyaltyTier;
        this.originalAmount = originalAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.appliedRules = appliedRules;
        this.discounts = discounts != null ? discounts : new ArrayList<>();
        this.orderId = orderId;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.notes = notes;
    }
    
    // Getters and setters
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    
    public double getOriginalAmount() {
        return originalAmount;
    }
    
    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
        // Recalculate discount amount
        this.discountAmount = originalAmount - finalAmount;
    }
    
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
        // Recalculate final amount and discount amount
        this.finalAmount = originalAmount * (1 - discountPercentage / 100.0);
        this.discountAmount = originalAmount - finalAmount;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        // Recalculate final amount
        this.finalAmount = originalAmount - discountAmount;
        // Recalculate discount percentage
        if (originalAmount > 0) {
            this.discountPercentage = (discountAmount / originalAmount) * 100.0;
        }
    }
    
    public double getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
        // Recalculate discount amount
        this.discountAmount = originalAmount - finalAmount;
        // Recalculate discount percentage
        if (originalAmount > 0) {
            this.discountPercentage = (discountAmount / originalAmount) * 100.0;
        }
    }
    
    public String getAppliedRules() {
        return appliedRules;
    }
    
    public void setAppliedRules(String appliedRules) {
        this.appliedRules = appliedRules;
    }
    
    public List<DiscountDetail> getDiscounts() {
        return discounts;
    }
    
    public void setDiscounts(List<DiscountDetail> discounts) {
        this.discounts = discounts != null ? discounts : new ArrayList<>();
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Adds a discount detail to the response
     * 
     * @param discount The discount detail to add
     */
    public void addDiscount(DiscountDetail discount) {
        if (this.discounts == null) {
            this.discounts = new ArrayList<>();
        }
        this.discounts.add(discount);
    }
    
    /**
     * Recalculates the total discount based on individual discounts
     */
    public void recalculateFromDiscounts() {
        if (discounts == null || discounts.isEmpty()) {
            this.discountPercentage = 0;
            this.discountAmount = 0;
            this.finalAmount = originalAmount;
            return;
        }
        
        // Calculate total discount amount
        this.discountAmount = discounts.stream()
                .mapToDouble(DiscountDetail::getDiscountAmount)
                .sum();
        
        // Calculate final amount
        this.finalAmount = originalAmount - discountAmount;
        
        // Calculate discount percentage
        if (originalAmount > 0) {
            this.discountPercentage = (discountAmount / originalAmount) * 100.0;
        }
        
        // Build applied rules string
        this.appliedRules = String.join(", ", discounts.stream()
                .map(DiscountDetail::getRuleName)
                .toArray(String[]::new));
    }
    
    @Override
    public String toString() {
        return "CustomerDiscountResponse{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", originalAmount=" + originalAmount +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", appliedRules='" + appliedRules + '\'' +
                ", discounts.size=" + (discounts != null ? discounts.size() : 0) +
                ", timestamp=" + timestamp +
                '}';
    }
    
    /**
     * Inner class representing details of an individual discount
     */
    @Schema(description = "Details of an individual discount")
    public static class DiscountDetail {
        
        /**
         * The ID of the rule that was applied
         */
        @Schema(description = "ID of the rule that was applied", example = "1")
        private Long ruleId;
        
        /**
         * The name of the rule that was applied
         */
        @Schema(description = "Name of the rule that was applied", example = "Gold Tier Discount")
        private String ruleName;
        
        /**
         * The discount percentage applied by this rule
         */
        @Schema(description = "Discount percentage applied by this rule", example = "15.0")
        private double discountPercentage;
        
        /**
         * The discount amount in currency units
         */
        @Schema(description = "Discount amount in currency units", example = "18.83")
        private double discountAmount;
        
        /**
         * The priority of the rule
         */
        @Schema(description = "Priority of the rule", example = "10")
        private int priority;
        
        /**
         * Default constructor
         */
        public DiscountDetail() {
        }
        
        /**
         * Constructor with all fields
         *
         * @param ruleId The rule ID
         * @param ruleName The rule name
         * @param discountPercentage The discount percentage
         * @param discountAmount The discount amount
         * @param priority The rule priority
         */
        public DiscountDetail(Long ruleId, String ruleName, double discountPercentage, 
                             double discountAmount, int priority) {
            this.ruleId = ruleId;
            this.ruleName = ruleName;
            this.discountPercentage = discountPercentage;
            this.discountAmount = discountAmount;
            this.priority = priority;
        }
        
        // Getters and setters
        
        public Long getRuleId() {
            return ruleId;
        }
        
        public void setRuleId(Long ruleId) {
            this.ruleId = ruleId;
        }
        
        public String getRuleName() {
            return ruleName;
        }
        
        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }
        
        public double getDiscountPercentage() {
            return discountPercentage;
        }
        
        public void setDiscountPercentage(double discountPercentage) {
            this.discountPercentage = discountPercentage;
        }
        
        public double getDiscountAmount() {
            return discountAmount;
        }
        
        public void setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public void setPriority(int priority) {
            this.priority = priority;
        }
    }
}