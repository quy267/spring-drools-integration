package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DiscountRule entity for the discount rules use case.
 * This entity represents a discount rule in the system and contains all relevant
 * information needed for calculating discounts.
 */
public class DiscountRule {
    
    /**
     * Unique identifier for the discount rule
     */
    private Long id;
    
    /**
     * Name of the discount rule
     */
    @NotBlank(message = "Rule name is required")
    @Size(min = 3, max = 100, message = "Rule name must be between 3 and 100 characters")
    private String name;
    
    /**
     * Description of the discount rule
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    /**
     * The discount percentage to apply
     */
    @Min(value = 0, message = "Discount percentage must be non-negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private double discountPercentage;
    
    /**
     * Priority of the rule (higher values take precedence)
     */
    private int priority;
    
    /**
     * Whether the rule is currently active
     */
    private boolean active;
    
    /**
     * Start date for the rule validity period
     */
    private LocalDate startDate;
    
    /**
     * End date for the rule validity period
     */
    private LocalDate endDate;
    
    /**
     * Date and time when the rule was created
     */
    private LocalDateTime createdAt;
    
    /**
     * Date and time when the rule was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Minimum customer age for the rule to apply (null means no minimum)
     */
    private Integer customerAgeMin;
    
    /**
     * Maximum customer age for the rule to apply (null means no maximum)
     */
    private Integer customerAgeMax;
    
    /**
     * Required customer loyalty tier for the rule to apply (null means any tier)
     */
    private String loyaltyTier;
    
    /**
     * Minimum order amount for the rule to apply (null means no minimum)
     */
    private Double minOrderAmount;
    
    /**
     * Minimum order volume (quantity) for the rule to apply (null means no minimum)
     */
    private Integer minOrderVolume;
    
    /**
     * Product category to which the rule applies (null means all categories)
     */
    private String productCategory;
    
    /**
     * Whether the rule can be combined with other rules
     */
    private boolean combinable;
    
    /**
     * Default constructor
     */
    public DiscountRule() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.priority = 1;
        this.combinable = false;
    }
    
    /**
     * Constructor with essential fields
     *
     * @param name The name of the rule
     * @param description The description of the rule
     * @param discountPercentage The discount percentage to apply
     */
    public DiscountRule(String name, String description, double discountPercentage) {
        this();
        this.name = name;
        this.description = description;
        this.discountPercentage = discountPercentage;
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The rule ID
     * @param name The name of the rule
     * @param description The description of the rule
     * @param discountPercentage The discount percentage to apply
     * @param priority The priority of the rule
     * @param active Whether the rule is active
     * @param startDate The start date of the rule validity period
     * @param endDate The end date of the rule validity period
     * @param customerAgeMin The minimum customer age
     * @param customerAgeMax The maximum customer age
     * @param loyaltyTier The required loyalty tier
     * @param minOrderAmount The minimum order amount
     * @param minOrderVolume The minimum order volume
     * @param productCategory The product category
     * @param combinable Whether the rule can be combined with other rules
     */
    public DiscountRule(Long id, String name, String description, double discountPercentage,
                       int priority, boolean active, LocalDate startDate, LocalDate endDate,
                       Integer customerAgeMin, Integer customerAgeMax, String loyaltyTier,
                       Double minOrderAmount, Integer minOrderVolume, String productCategory,
                       boolean combinable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.priority = priority;
        this.active = active;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.customerAgeMin = customerAgeMin;
        this.customerAgeMax = customerAgeMax;
        this.loyaltyTier = loyaltyTier;
        this.minOrderAmount = minOrderAmount;
        this.minOrderVolume = minOrderVolume;
        this.productCategory = productCategory;
        this.combinable = combinable;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Integer getCustomerAgeMin() {
        return customerAgeMin;
    }
    
    public void setCustomerAgeMin(Integer customerAgeMin) {
        this.customerAgeMin = customerAgeMin;
    }
    
    public Integer getCustomerAgeMax() {
        return customerAgeMax;
    }
    
    public void setCustomerAgeMax(Integer customerAgeMax) {
        this.customerAgeMax = customerAgeMax;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    
    public Double getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }
    
    public Integer getMinOrderVolume() {
        return minOrderVolume;
    }
    
    public void setMinOrderVolume(Integer minOrderVolume) {
        this.minOrderVolume = minOrderVolume;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public boolean isCombinable() {
        return combinable;
    }
    
    public void setCombinable(boolean combinable) {
        this.combinable = combinable;
    }
    
    /**
     * Checks if the rule is currently valid based on its start and end dates
     * 
     * @return true if the rule is valid, false otherwise
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        
        // Check if the rule is active
        if (!active) {
            return false;
        }
        
        // Check start date if set
        if (startDate != null && today.isBefore(startDate)) {
            return false;
        }
        
        // Check end date if set
        if (endDate != null && today.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Evaluates if the rule applies to a given customer
     * 
     * @param customer The customer to evaluate
     * @return true if the rule applies to the customer, false otherwise
     */
    public boolean appliesTo(Customer customer) {
        if (customer == null) {
            return false;
        }
        
        // Check customer age if min age is set
        if (customerAgeMin != null && customer.getAge() < customerAgeMin) {
            return false;
        }
        
        // Check customer age if max age is set
        if (customerAgeMax != null && customer.getAge() > customerAgeMax) {
            return false;
        }
        
        // Check loyalty tier if set
        if (loyaltyTier != null && !loyaltyTier.equals(customer.getLoyaltyTier())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Evaluates if the rule applies to a given order
     * 
     * @param order The order to evaluate
     * @return true if the rule applies to the order, false otherwise
     */
    public boolean appliesTo(Order order) {
        if (order == null) {
            return false;
        }
        
        // Check order amount if min amount is set
        if (minOrderAmount != null && order.getAmount() < minOrderAmount) {
            return false;
        }
        
        // Check order volume if min volume is set
        if (minOrderVolume != null && order.getVolume() < minOrderVolume) {
            return false;
        }
        
        // Check product category if set
        if (productCategory != null && order.getItems() != null && !order.getItems().isEmpty()) {
            // Check if any item in the order matches the product category
            boolean hasMatchingCategory = order.getItems().stream()
                    .anyMatch(item -> productCategory.equals(item.getCategory()));
            
            if (!hasMatchingCategory) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Evaluates if the rule applies to a given customer and order
     * 
     * @param customer The customer to evaluate
     * @param order The order to evaluate
     * @return true if the rule applies to both the customer and order, false otherwise
     */
    public boolean appliesTo(Customer customer, Order order) {
        return appliesTo(customer) && appliesTo(order);
    }
    
    @Override
    public String toString() {
        return "DiscountRule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", priority=" + priority +
                ", active=" + active +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountRule that = (DiscountRule) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}