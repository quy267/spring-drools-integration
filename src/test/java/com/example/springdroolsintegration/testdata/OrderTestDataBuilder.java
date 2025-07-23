package com.example.springdroolsintegration.testdata;

import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Test data builder for Order entity following the builder pattern.
 * Provides fluent API for creating Order instances with default values for testing.
 */
public class OrderTestDataBuilder {
    
    private Order order;
    
    private OrderTestDataBuilder() {
        this.order = new Order();
        // Set default values for testing
        this.order.setId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        this.order.setCustomer(CustomerTestDataBuilder.aCustomer().build());
        this.order.setAmount(299.99);
        this.order.setVolume(2);
        this.order.setOrderDate(LocalDateTime.now());
        this.order.setStatus("PENDING");
        this.order.setShippingAddress("123 Main St, Anytown, CA 12345");
        this.order.setPaymentMethod("CREDIT_CARD");
        this.order.setItems(new ArrayList<>());
        this.order.setDiscountPercentage(0.0);
        this.order.setFinalAmount(299.99);
        this.order.setNotes("Standard test order");
        
        // Add default order items
        this.order.getItems().add(OrderItemTestDataBuilder.anOrderItem().build());
        this.order.getItems().add(OrderItemTestDataBuilder.anOrderItem()
                .withProductId("PROD-002")
                .withProductName("Test Product 2")
                .withPrice(149.99)
                .withQuantity(1)
                .build());
    }
    
    /**
     * Creates a new OrderTestDataBuilder instance with default values.
     * @return new OrderTestDataBuilder instance
     */
    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }
    
    /**
     * Creates an OrderTestDataBuilder for a small order (< $100).
     * @return OrderTestDataBuilder with small order defaults
     */
    public static OrderTestDataBuilder aSmallOrder() {
        return new OrderTestDataBuilder()
                .withAmount(75.00)
                .withVolume(1)
                .withFinalAmount(75.00)
                .withCustomer(CustomerTestDataBuilder.aCustomer().withTotalSpent(200.0).build())
                .withItems(List.of(
                    OrderItemTestDataBuilder.anOrderItem()
                        .withPrice(75.00)
                        .withQuantity(1)
                        .build()
                ));
    }
    
    /**
     * Creates an OrderTestDataBuilder for a medium order ($100-$500).
     * @return OrderTestDataBuilder with medium order defaults
     */
    public static OrderTestDataBuilder aMediumOrder() {
        return new OrderTestDataBuilder()
                .withAmount(350.00)
                .withVolume(3)
                .withFinalAmount(350.00)
                .withCustomer(CustomerTestDataBuilder.anAdultCustomer().build());
    }
    
    /**
     * Creates an OrderTestDataBuilder for a large order (> $500).
     * @return OrderTestDataBuilder with large order defaults
     */
    public static OrderTestDataBuilder aLargeOrder() {
        return new OrderTestDataBuilder()
                .withAmount(750.00)
                .withVolume(5)
                .withFinalAmount(750.00)
                .withCustomer(CustomerTestDataBuilder.aPremiumCustomer().build())
                .withShippingAddress("456 Premium Ave, Luxury City, CA 90210");
    }
    
    /**
     * Creates an OrderTestDataBuilder for a bulk order (high volume).
     * @return OrderTestDataBuilder with bulk order defaults
     */
    public static OrderTestDataBuilder aBulkOrder() {
        return new OrderTestDataBuilder()
                .withAmount(1200.00)
                .withVolume(15)
                .withFinalAmount(1080.00) // 10% bulk discount
                .withDiscountPercentage(10.0)
                .withCustomer(CustomerTestDataBuilder.aPremiumCustomer().build())
                .withNotes("Bulk order with volume discount");
    }
    
    /**
     * Creates an OrderTestDataBuilder for a completed order.
     * @return OrderTestDataBuilder with completed order defaults
     */
    public static OrderTestDataBuilder aCompletedOrder() {
        return new OrderTestDataBuilder()
                .withStatus("COMPLETED")
                .withOrderDate(LocalDateTime.now().minusDays(7))
                .withPaymentMethod("CREDIT_CARD")
                .withNotes("Order completed successfully");
    }
    
    /**
     * Creates an OrderTestDataBuilder for a cancelled order.
     * @return OrderTestDataBuilder with cancelled order defaults
     */
    public static OrderTestDataBuilder aCancelledOrder() {
        return new OrderTestDataBuilder()
                .withStatus("CANCELLED")
                .withOrderDate(LocalDateTime.now().minusDays(2))
                .withNotes("Order cancelled by customer");
    }
    
    /**
     * Creates an OrderTestDataBuilder for a shipped order.
     * @return OrderTestDataBuilder with shipped order defaults
     */
    public static OrderTestDataBuilder aShippedOrder() {
        return new OrderTestDataBuilder()
                .withStatus("SHIPPED")
                .withOrderDate(LocalDateTime.now().minusDays(3))
                .withPaymentMethod("CREDIT_CARD")
                .withNotes("Order shipped via standard delivery");
    }
    
    /**
     * Creates an OrderTestDataBuilder for an order with discount applied.
     * @return OrderTestDataBuilder with discount defaults
     */
    public static OrderTestDataBuilder aDiscountedOrder() {
        return new OrderTestDataBuilder()
                .withAmount(200.00)
                .withDiscountPercentage(15.0)
                .withFinalAmount(170.00)
                .withCustomer(CustomerTestDataBuilder.aSeniorCustomer().build())
                .withNotes("Senior discount applied");
    }
    
    public OrderTestDataBuilder withId(String id) {
        this.order.setId(id);
        return this;
    }
    
    public OrderTestDataBuilder withCustomer(Customer customer) {
        this.order.setCustomer(customer);
        return this;
    }
    
    public OrderTestDataBuilder withAmount(double amount) {
        this.order.setAmount(amount);
        return this;
    }
    
    public OrderTestDataBuilder withVolume(int volume) {
        this.order.setVolume(volume);
        return this;
    }
    
    public OrderTestDataBuilder withOrderDate(LocalDateTime orderDate) {
        this.order.setOrderDate(orderDate);
        return this;
    }
    
    public OrderTestDataBuilder withStatus(String status) {
        this.order.setStatus(status);
        return this;
    }
    
    public OrderTestDataBuilder withShippingAddress(String shippingAddress) {
        this.order.setShippingAddress(shippingAddress);
        return this;
    }
    
    public OrderTestDataBuilder withPaymentMethod(String paymentMethod) {
        this.order.setPaymentMethod(paymentMethod);
        return this;
    }
    
    public OrderTestDataBuilder withItems(List<Order.OrderItem> items) {
        this.order.setItems(new ArrayList<>(items));
        return this;
    }
    
    public OrderTestDataBuilder withDiscountPercentage(double discountPercentage) {
        this.order.setDiscountPercentage(discountPercentage);
        return this;
    }
    
    public OrderTestDataBuilder withFinalAmount(double finalAmount) {
        this.order.setFinalAmount(finalAmount);
        return this;
    }
    
    public OrderTestDataBuilder withNotes(String notes) {
        this.order.setNotes(notes);
        return this;
    }
    
    /**
     * Adds a single order item to the order.
     * @param item the OrderItem to add
     * @return OrderTestDataBuilder with added item
     */
    public OrderTestDataBuilder addItem(Order.OrderItem item) {
        if (this.order.getItems() == null) {
            this.order.setItems(new ArrayList<>());
        }
        this.order.getItems().add(item);
        return this;
    }
    
    /**
     * Adds multiple order items to the order.
     * @param items the OrderItems to add
     * @return OrderTestDataBuilder with added items
     */
    public OrderTestDataBuilder addItems(List<Order.OrderItem> items) {
        if (this.order.getItems() == null) {
            this.order.setItems(new ArrayList<>());
        }
        this.order.getItems().addAll(items);
        return this;
    }
    
    /**
     * Clears all items from the order.
     * @return OrderTestDataBuilder with no items
     */
    public OrderTestDataBuilder withNoItems() {
        this.order.setItems(new ArrayList<>());
        this.order.setAmount(0.0);
        this.order.setVolume(0);
        this.order.setFinalAmount(0.0);
        return this;
    }
    
    /**
     * Sets the order as eligible for free shipping.
     * @return OrderTestDataBuilder configured for free shipping
     */
    public OrderTestDataBuilder withFreeShipping() {
        this.order.setAmount(100.00); // Assuming $100+ qualifies for free shipping
        this.order.setFinalAmount(100.00);
        this.order.setNotes("Eligible for free shipping");
        return this;
    }
    
    /**
     * Sets the order as a rush order with expedited shipping.
     * @return OrderTestDataBuilder configured as rush order
     */
    public OrderTestDataBuilder asRushOrder() {
        this.order.setNotes("Rush order - expedited shipping");
        this.order.setPaymentMethod("CREDIT_CARD");
        this.order.setStatus("PROCESSING");
        return this;
    }
    
    /**
     * Builds and returns the Order instance.
     * @return configured Order instance
     */
    public Order build() {
        return this.order;
    }
    
    /**
     * Helper class for building OrderItem instances.
     */
    public static class OrderItemTestDataBuilder {
        
        private Order.OrderItem orderItem;
        
        private OrderItemTestDataBuilder() {
            this.orderItem = new Order.OrderItem();
            // Set default values for testing
            this.orderItem.setProductId("PROD-001");
            this.orderItem.setProductName("Test Product");
            this.orderItem.setPrice(149.99);
            this.orderItem.setQuantity(1);
            this.orderItem.setCategory("Electronics");
        }
        
        /**
         * Creates a new OrderItemTestDataBuilder instance with default values.
         * @return new OrderItemTestDataBuilder instance
         */
        public static OrderItemTestDataBuilder anOrderItem() {
            return new OrderItemTestDataBuilder();
        }
        
        /**
         * Creates an OrderItemTestDataBuilder for an electronics item.
         * @return OrderItemTestDataBuilder with electronics defaults
         */
        public static OrderItemTestDataBuilder anElectronicsItem() {
            return new OrderItemTestDataBuilder()
                    .withProductName("Smartphone")
                    .withPrice(599.99)
                    .withCategory("Electronics");
        }
        
        /**
         * Creates an OrderItemTestDataBuilder for a clothing item.
         * @return OrderItemTestDataBuilder with clothing defaults
         */
        public static OrderItemTestDataBuilder aClothingItem() {
            return new OrderItemTestDataBuilder()
                    .withProductName("T-Shirt")
                    .withPrice(29.99)
                    .withCategory("Clothing");
        }
        
        /**
         * Creates an OrderItemTestDataBuilder for a high-quantity item.
         * @return OrderItemTestDataBuilder with high quantity defaults
         */
        public static OrderItemTestDataBuilder aHighQuantityItem() {
            return new OrderItemTestDataBuilder()
                    .withQuantity(10)
                    .withPrice(19.99)
                    .withProductName("Bulk Item");
        }
        
        public OrderItemTestDataBuilder withProductId(String productId) {
            this.orderItem.setProductId(productId);
            return this;
        }
        
        public OrderItemTestDataBuilder withProductName(String productName) {
            this.orderItem.setProductName(productName);
            return this;
        }
        
        public OrderItemTestDataBuilder withPrice(double price) {
            this.orderItem.setPrice(price);
            return this;
        }
        
        public OrderItemTestDataBuilder withQuantity(int quantity) {
            this.orderItem.setQuantity(quantity);
            return this;
        }
        
        public OrderItemTestDataBuilder withCategory(String category) {
            this.orderItem.setCategory(category);
            return this;
        }
        
        /**
         * Builds and returns the OrderItem instance.
         * @return configured OrderItem instance
         */
        public Order.OrderItem build() {
            return this.orderItem;
        }
    }
}