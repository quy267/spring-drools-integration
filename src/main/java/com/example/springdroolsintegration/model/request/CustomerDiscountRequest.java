package com.example.springdroolsintegration.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request model for customer discount rule execution.
 * This DTO captures all input data needed for evaluating discount rules for a customer order.
 */
@Schema(description = "Request model for customer discount rule evaluation")
public class CustomerDiscountRequest {
    
    /**
     * The customer's ID (optional if customer details are provided)
     */
    @Schema(description = "Customer ID (optional if customer details are provided)", example = "1001")
    private Long customerId;
    
    /**
     * The customer's name
     */
    @Schema(description = "Customer name", example = "John Doe")
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String customerName;
    
    /**
     * The customer's age in years
     */
    @Schema(description = "Customer age in years", example = "35")
    @Min(value = 0, message = "Age must be a positive number")
    private int customerAge;
    
    /**
     * The customer's loyalty tier (e.g., BRONZE, SILVER, GOLD, PLATINUM)
     */
    @Schema(description = "Customer loyalty tier", example = "GOLD")
    @NotNull(message = "Loyalty tier is required")
    private String loyaltyTier;
    
    /**
     * The customer's email address (optional)
     */
    @Schema(description = "Customer email address (optional)", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    private String customerEmail;
    
    /**
     * The total amount of the order
     */
    @Schema(description = "Total order amount", example = "125.50")
    @Min(value = 0, message = "Order amount must be non-negative")
    private double orderAmount;
    
    /**
     * The total quantity of items in the order
     */
    @Schema(description = "Total quantity of items in the order", example = "5")
    @Min(value = 1, message = "Order quantity must be at least 1")
    private int orderQuantity;
    
    /**
     * The order items (optional, for more detailed rule evaluation)
     */
    @Schema(description = "Order items (optional, for more detailed rule evaluation)")
    private List<OrderItemRequest> orderItems = new ArrayList<>();
    
    /**
     * The order date in ISO format (optional, defaults to current date)
     */
    @Schema(description = "Order date in ISO format (optional)", example = "2025-07-21")
    private String orderDate;
    
    /**
     * Whether to apply multiple discounts if applicable
     */
    @Schema(description = "Whether to apply multiple discounts if applicable", example = "false")
    private boolean applyMultipleDiscounts = false;
    
    /**
     * The product category for category-specific discounts (optional)
     */
    @Schema(description = "Product category for category-specific discounts (optional)", example = "ELECTRONICS")
    private String productCategory;
    
    /**
     * Promotion code entered by the customer (optional)
     */
    @Schema(description = "Promotion code entered by the customer (optional)", example = "SUMMER25")
    @Pattern(regexp = "^[A-Z0-9_-]*$", message = "Promotion code should contain only uppercase letters, numbers, underscores and hyphens")
    private String promotionCode;
    
    /**
     * Default constructor
     */
    public CustomerDiscountRequest() {
    }
    
    /**
     * Constructor with essential fields
     *
     * @param customerName The customer's name
     * @param customerAge The customer's age
     * @param loyaltyTier The customer's loyalty tier
     * @param orderAmount The total order amount
     * @param orderQuantity The total order quantity
     */
    public CustomerDiscountRequest(String customerName, int customerAge, String loyaltyTier, 
                                  double orderAmount, int orderQuantity) {
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.loyaltyTier = loyaltyTier;
        this.orderAmount = orderAmount;
        this.orderQuantity = orderQuantity;
    }
    
    /**
     * Full constructor with all fields
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param customerAge The customer's age
     * @param loyaltyTier The customer's loyalty tier
     * @param customerEmail The customer's email
     * @param orderAmount The total order amount
     * @param orderQuantity The total order quantity
     * @param orderItems The order items
     * @param orderDate The order date
     * @param applyMultipleDiscounts Whether to apply multiple discounts
     * @param productCategory The product category
     * @param promotionCode The promotion code
     */
    public CustomerDiscountRequest(Long customerId, String customerName, int customerAge, 
                                  String loyaltyTier, String customerEmail, double orderAmount, 
                                  int orderQuantity, List<OrderItemRequest> orderItems, 
                                  String orderDate, boolean applyMultipleDiscounts, 
                                  String productCategory, String promotionCode) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.loyaltyTier = loyaltyTier;
        this.customerEmail = customerEmail;
        this.orderAmount = orderAmount;
        this.orderQuantity = orderQuantity;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        this.orderDate = orderDate;
        this.applyMultipleDiscounts = applyMultipleDiscounts;
        this.productCategory = productCategory;
        this.promotionCode = promotionCode;
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
    
    public int getCustomerAge() {
        return customerAge;
    }
    
    public void setCustomerAge(int customerAge) {
        this.customerAge = customerAge;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public double getOrderAmount() {
        return orderAmount;
    }
    
    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public int getOrderQuantity() {
        return orderQuantity;
    }
    
    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
    
    public List<OrderItemRequest> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<OrderItemRequest> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    
    public boolean isApplyMultipleDiscounts() {
        return applyMultipleDiscounts;
    }
    
    public void setApplyMultipleDiscounts(boolean applyMultipleDiscounts) {
        this.applyMultipleDiscounts = applyMultipleDiscounts;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public String getPromotionCode() {
        return promotionCode;
    }
    
    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }
    
    /**
     * Add an order item to the request
     * 
     * @param item The order item to add
     */
    public void addOrderItem(OrderItemRequest item) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(item);
    }
    
    @Override
    public String toString() {
        return "CustomerDiscountRequest{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerAge=" + customerAge +
                ", loyaltyTier='" + loyaltyTier + '\'' +
                ", orderAmount=" + orderAmount +
                ", orderQuantity=" + orderQuantity +
                ", orderItems.size=" + (orderItems != null ? orderItems.size() : 0) +
                ", productCategory='" + productCategory + '\'' +
                ", promotionCode='" + promotionCode + '\'' +
                '}';
    }
    
    /**
     * Inner class representing an order item in the request
     */
    @Schema(description = "Order item details")
    public static class OrderItemRequest {
        
        /**
         * The product ID
         */
        @Schema(description = "Product ID", example = "PROD-123")
        @NotBlank(message = "Product ID is required")
        private String productId;
        
        /**
         * The product name
         */
        @Schema(description = "Product name", example = "Smartphone")
        @NotBlank(message = "Product name is required")
        private String productName;
        
        /**
         * The product price
         */
        @Schema(description = "Product price", example = "599.99")
        @Min(value = 0, message = "Price must be non-negative")
        private double price;
        
        /**
         * The quantity ordered
         */
        @Schema(description = "Quantity ordered", example = "2")
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
        
        /**
         * The product category
         */
        @Schema(description = "Product category", example = "ELECTRONICS")
        private String category;
        
        /**
         * Default constructor
         */
        public OrderItemRequest() {
        }
        
        /**
         * Constructor with all fields
         *
         * @param productId The product ID
         * @param productName The product name
         * @param price The product price
         * @param quantity The quantity ordered
         * @param category The product category
         */
        public OrderItemRequest(String productId, String productName, double price, int quantity, String category) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
        }
        
        // Getters and setters
        
        public String getProductId() {
            return productId;
        }
        
        public void setProductId(String productId) {
            this.productId = productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
}