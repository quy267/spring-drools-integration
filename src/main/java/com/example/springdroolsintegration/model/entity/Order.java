package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Order entity for the discount rules use case.
 * This entity represents a customer order in the system and contains all relevant
 * information needed for applying discount rules.
 */
public class Order {
    
    /**
     * Unique identifier for the order
     */
    private String id;
    
    /**
     * The customer who placed the order
     */
    @NotNull(message = "Customer is required")
    private Customer customer;
    
    /**
     * The total amount of the order in the base currency
     */
    @Min(value = 0, message = "Order amount must be non-negative")
    private double amount;
    
    /**
     * The total number of items in the order
     */
    @Min(value = 1, message = "Order must contain at least one item")
    private int volume;
    
    /**
     * The date and time when the order was placed
     */
    @NotNull(message = "Order date is required")
    @PastOrPresent(message = "Order date cannot be in the future")
    private LocalDateTime orderDate;
    
    /**
     * The current status of the order (e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
     */
    @NotNull(message = "Order status is required")
    private String status;
    
    /**
     * The shipping address for the order
     */
    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;
    
    /**
     * The payment method used for the order
     */
    private String paymentMethod;
    
    /**
     * The list of items in the order
     */
    private List<OrderItem> items = new ArrayList<>();
    
    /**
     * The discount percentage applied to the order
     */
    private double discountPercentage;
    
    /**
     * The final amount after applying discounts
     */
    private double finalAmount;
    
    /**
     * Notes or comments about the order
     */
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    /**
     * Default constructor
     */
    public Order() {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    /**
     * Constructor with essential fields
     *
     * @param customer The customer who placed the order
     * @param amount The total amount of the order
     * @param volume The total number of items in the order
     */
    public Order(Customer customer, double amount, int volume) {
        this.id = UUID.randomUUID().toString();
        this.customer = customer;
        this.amount = amount;
        this.volume = volume;
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.finalAmount = amount; // Initially, final amount equals the original amount
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The order ID
     * @param customer The customer who placed the order
     * @param amount The total amount of the order
     * @param volume The total number of items in the order
     * @param orderDate The date and time when the order was placed
     * @param status The current status of the order
     * @param shippingAddress The shipping address for the order
     * @param paymentMethod The payment method used for the order
     * @param items The list of items in the order
     * @param discountPercentage The discount percentage applied to the order
     * @param finalAmount The final amount after applying discounts
     * @param notes Notes or comments about the order
     */
    public Order(String id, Customer customer, double amount, int volume, 
                LocalDateTime orderDate, String status, String shippingAddress, 
                String paymentMethod, List<OrderItem> items, double discountPercentage, 
                double finalAmount, String notes) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.volume = volume;
        this.orderDate = orderDate;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.items = items;
        this.discountPercentage = discountPercentage;
        this.finalAmount = finalAmount;
        this.notes = notes;
    }
    
    // Getters and setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
        // Update final amount if no discount has been applied
        if (discountPercentage == 0) {
            this.finalAmount = amount;
        } else {
            // Recalculate final amount with the existing discount
            applyDiscount(discountPercentage);
        }
    }
    
    public int getVolume() {
        return volume;
    }
    
    public void setVolume(int volume) {
        this.volume = volume;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
        // Recalculate volume and amount based on items
        recalculateFromItems();
    }
    
    public double getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
        // Recalculate final amount
        applyDiscount(discountPercentage);
    }
    
    public double getFinalAmount() {
        return finalAmount;
    }
    
    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Adds an item to the order
     * 
     * @param item The item to add
     */
    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        // Recalculate volume and amount
        recalculateFromItems();
    }
    
    /**
     * Removes an item from the order
     * 
     * @param item The item to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean removeItem(OrderItem item) {
        if (items == null) {
            return false;
        }
        boolean removed = items.remove(item);
        if (removed) {
            // Recalculate volume and amount
            recalculateFromItems();
        }
        return removed;
    }
    
    /**
     * Recalculates the order volume and amount based on the items
     */
    private void recalculateFromItems() {
        if (items == null || items.isEmpty()) {
            volume = 0;
            amount = 0;
            finalAmount = 0;
            return;
        }
        
        volume = items.stream().mapToInt(OrderItem::getQuantity).sum();
        amount = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        
        // Recalculate final amount with the existing discount
        applyDiscount(discountPercentage);
    }
    
    /**
     * Applies a discount to the order
     * 
     * @param percentage The discount percentage to apply
     */
    public void applyDiscount(double percentage) {
        this.discountPercentage = percentage;
        this.finalAmount = amount * (1 - percentage / 100.0);
    }
    
    /**
     * Checks if the order is a large order (amount >= 1000)
     * This is a business method that can be used in rules
     * 
     * @return true if the order is large, false otherwise
     */
    public boolean isLargeOrder() {
        return amount >= 1000;
    }
    
    /**
     * Checks if the order is a bulk order (volume >= 10)
     * This is a business method that can be used in rules
     * 
     * @return true if the order is a bulk order, false otherwise
     */
    public boolean isBulkOrder() {
        return volume >= 10;
    }
    
    /**
     * Checks if the order is eligible for free shipping
     * This is a business method that can be used in rules
     * 
     * @return true if the order is eligible for free shipping, false otherwise
     */
    public boolean isEligibleForFreeShipping() {
        return amount >= 50;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customer=" + (customer != null ? customer.getId() : "null") +
                ", amount=" + amount +
                ", volume=" + volume +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", finalAmount=" + finalAmount +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * Inner class representing an item in an order
     */
    public static class OrderItem {
        private String productId;
        private String productName;
        private double price;
        private int quantity;
        private String category;
        
        public OrderItem() {
        }
        
        public OrderItem(String productId, String productName, double price, int quantity, String category) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
        }
        
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
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderItem orderItem = (OrderItem) o;
            return Objects.equals(productId, orderItem.productId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(productId);
        }
    }
}