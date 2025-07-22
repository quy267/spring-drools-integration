package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * PurchaseHistory entity for the product recommendation rules use case.
 * This entity represents a customer's purchase history and contains all relevant
 * information needed for product recommendations.
 */
public class PurchaseHistory {
    
    /**
     * Unique identifier for the purchase history record
     */
    private Long id;
    
    /**
     * The customer who made the purchase
     */
    @NotNull(message = "Customer is required")
    private RecommendationCustomer customer;
    
    /**
     * The customer's ID (for reference)
     */
    private Long customerId;
    
    /**
     * The order number or reference
     */
    @NotBlank(message = "Order number is required")
    @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "Order number must be 5-20 alphanumeric characters")
    private String orderNumber;
    
    /**
     * The date and time when the purchase was made
     */
    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDateTime purchaseDate;
    
    /**
     * The total amount of the purchase
     */
    @Min(value = 0, message = "Total amount must be non-negative")
    private double totalAmount;
    
    /**
     * The number of items in the purchase
     */
    @Min(value = 1, message = "Item count must be at least 1")
    private int itemCount;
    
    /**
     * The items purchased
     */
    private List<PurchaseItem> items = new ArrayList<>();
    
    /**
     * The payment method used (e.g., CREDIT_CARD, PAYPAL, BANK_TRANSFER)
     */
    private String paymentMethod;
    
    /**
     * The shipping method used (e.g., STANDARD, EXPRESS, PICKUP)
     */
    private String shippingMethod;
    
    /**
     * The shipping address
     */
    @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
    private String shippingAddress;
    
    /**
     * The billing address
     */
    @Size(max = 500, message = "Billing address cannot exceed 500 characters")
    private String billingAddress;
    
    /**
     * The status of the order (e.g., COMPLETED, SHIPPED, DELIVERED, RETURNED)
     */
    private String orderStatus;
    
    /**
     * The channel through which the purchase was made (e.g., ONLINE, IN_STORE, MOBILE_APP)
     */
    private String purchaseChannel;
    
    /**
     * The device used for the purchase (e.g., DESKTOP, MOBILE, TABLET)
     */
    private String deviceType;
    
    /**
     * The coupon code used (if any)
     */
    private String couponCode;
    
    /**
     * The discount amount applied
     */
    @Min(value = 0, message = "Discount amount must be non-negative")
    private double discountAmount;
    
    /**
     * The tax amount applied
     */
    @Min(value = 0, message = "Tax amount must be non-negative")
    private double taxAmount;
    
    /**
     * The shipping amount charged
     */
    @Min(value = 0, message = "Shipping amount must be non-negative")
    private double shippingAmount;
    
    /**
     * Whether the order was a gift
     */
    private boolean isGift;
    
    /**
     * The gift message (if any)
     */
    @Size(max = 500, message = "Gift message cannot exceed 500 characters")
    private String giftMessage;
    
    /**
     * Whether the order has been returned
     */
    private boolean isReturned;
    
    /**
     * The date and time when the order was returned (if applicable)
     */
    @PastOrPresent(message = "Return date cannot be in the future")
    private LocalDateTime returnDate;
    
    /**
     * The reason for the return (if applicable)
     */
    @Size(max = 500, message = "Return reason cannot exceed 500 characters")
    private String returnReason;
    
    /**
     * The items that were returned (if any)
     */
    private Set<String> returnedItems = new HashSet<>();
    
    /**
     * Whether the customer left a review for this purchase
     */
    private boolean hasReview;
    
    /**
     * The rating given by the customer (1-5)
     */
    @Min(value = 0, message = "Rating must be non-negative")
    private int rating;
    
    /**
     * The review text
     */
    @Size(max = 1000, message = "Review text cannot exceed 1000 characters")
    private String reviewText;
    
    /**
     * The date and time when the review was submitted
     */
    @PastOrPresent(message = "Review date cannot be in the future")
    private LocalDateTime reviewDate;
    
    /**
     * Categories of products purchased
     */
    private Set<String> categories = new HashSet<>();
    
    /**
     * Brands of products purchased
     */
    private Set<String> brands = new HashSet<>();
    
    /**
     * Tags associated with the purchase
     */
    private Set<String> tags = new HashSet<>();
    
    /**
     * Default constructor
     */
    public PurchaseHistory() {
        this.purchaseDate = LocalDateTime.now();
        this.orderStatus = "COMPLETED";
    }
    
    /**
     * Constructor with essential fields
     *
     * @param customer The customer who made the purchase
     * @param orderNumber The order number
     * @param totalAmount The total amount of the purchase
     */
    public PurchaseHistory(RecommendationCustomer customer, String orderNumber, double totalAmount) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.purchaseDate = LocalDateTime.now();
        this.orderStatus = "COMPLETED";
    }
    
    /**
     * Constructor with items
     *
     * @param customer The customer who made the purchase
     * @param orderNumber The order number
     * @param totalAmount The total amount of the purchase
     * @param items The items purchased
     */
    public PurchaseHistory(RecommendationCustomer customer, String orderNumber, double totalAmount, List<PurchaseItem> items) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.items = items != null ? items : new ArrayList<>();
        this.itemCount = this.items.size();
        this.purchaseDate = LocalDateTime.now();
        this.orderStatus = "COMPLETED";
        
        // Extract categories and brands from items
        if (this.items != null) {
            for (PurchaseItem item : this.items) {
                if (item.getCategory() != null) {
                    this.categories.add(item.getCategory());
                }
                if (item.getBrand() != null) {
                    this.brands.add(item.getBrand());
                }
            }
        }
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RecommendationCustomer getCustomer() {
        return customer;
    }
    
    public void setCustomer(RecommendationCustomer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
    public List<PurchaseItem> getItems() {
        return items;
    }
    
    public void setItems(List<PurchaseItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        this.itemCount = this.items.size();
        
        // Extract categories and brands from items
        this.categories.clear();
        this.brands.clear();
        if (this.items != null) {
            for (PurchaseItem item : this.items) {
                if (item.getCategory() != null) {
                    this.categories.add(item.getCategory());
                }
                if (item.getBrand() != null) {
                    this.brands.add(item.getBrand());
                }
            }
        }
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getShippingMethod() {
        return shippingMethod;
    }
    
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public String getPurchaseChannel() {
        return purchaseChannel;
    }
    
    public void setPurchaseChannel(String purchaseChannel) {
        this.purchaseChannel = purchaseChannel;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getCouponCode() {
        return couponCode;
    }
    
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public double getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public double getShippingAmount() {
        return shippingAmount;
    }
    
    public void setShippingAmount(double shippingAmount) {
        this.shippingAmount = shippingAmount;
    }
    
    public boolean isGift() {
        return isGift;
    }
    
    public void setGift(boolean gift) {
        isGift = gift;
    }
    
    public String getGiftMessage() {
        return giftMessage;
    }
    
    public void setGiftMessage(String giftMessage) {
        this.giftMessage = giftMessage;
    }
    
    public boolean isReturned() {
        return isReturned;
    }
    
    public void setReturned(boolean returned) {
        isReturned = returned;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getReturnReason() {
        return returnReason;
    }
    
    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }
    
    public Set<String> getReturnedItems() {
        return returnedItems;
    }
    
    public void setReturnedItems(Set<String> returnedItems) {
        this.returnedItems = returnedItems != null ? returnedItems : new HashSet<>();
    }
    
    public boolean isHasReview() {
        return hasReview;
    }
    
    public void setHasReview(boolean hasReview) {
        this.hasReview = hasReview;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
        this.hasReview = rating > 0 || (reviewText != null && !reviewText.isEmpty());
    }
    
    public String getReviewText() {
        return reviewText;
    }
    
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
        this.hasReview = (reviewText != null && !reviewText.isEmpty()) || rating > 0;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
    
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public Set<String> getCategories() {
        return categories;
    }
    
    public void setCategories(Set<String> categories) {
        this.categories = categories != null ? categories : new HashSet<>();
    }
    
    public Set<String> getBrands() {
        return brands;
    }
    
    public void setBrands(Set<String> brands) {
        this.brands = brands != null ? brands : new HashSet<>();
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags != null ? tags : new HashSet<>();
    }
    
    /**
     * Adds an item to the purchase history.
     * 
     * @param item The item to add
     */
    public void addItem(PurchaseItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        this.itemCount = this.items.size();
        
        // Update categories and brands
        if (item.getCategory() != null) {
            if (this.categories == null) {
                this.categories = new HashSet<>();
            }
            this.categories.add(item.getCategory());
        }
        if (item.getBrand() != null) {
            if (this.brands == null) {
                this.brands = new HashSet<>();
            }
            this.brands.add(item.getBrand());
        }
    }
    
    /**
     * Adds a returned item to the purchase history.
     * 
     * @param productId The product ID of the returned item
     */
    public void addReturnedItem(String productId) {
        if (this.returnedItems == null) {
            this.returnedItems = new HashSet<>();
        }
        this.returnedItems.add(productId);
        this.isReturned = true;
        if (this.returnDate == null) {
            this.returnDate = LocalDateTime.now();
        }
    }
    
    /**
     * Adds a tag to the purchase history.
     * 
     * @param tag The tag to add
     */
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        this.tags.add(tag);
    }
    
    /**
     * Adds a review to the purchase history.
     * 
     * @param rating The rating (1-5)
     * @param reviewText The review text
     */
    public void addReview(int rating, String reviewText) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.hasReview = true;
        this.reviewDate = LocalDateTime.now();
    }
    
    /**
     * Calculates the subtotal (total amount before tax and shipping).
     * 
     * @return The subtotal
     */
    public double getSubtotal() {
        return totalAmount - taxAmount - shippingAmount;
    }
    
    /**
     * Calculates the discount percentage.
     * 
     * @return The discount percentage
     */
    public double getDiscountPercentage() {
        double subtotal = getSubtotal() + discountAmount;
        if (subtotal > 0) {
            return (discountAmount / subtotal) * 100;
        }
        return 0;
    }
    
    /**
     * Checks if the purchase contains a product with the specified ID.
     * 
     * @param productId The product ID to check
     * @return true if the purchase contains the product, false otherwise
     */
    public boolean containsProduct(String productId) {
        if (items == null || productId == null) {
            return false;
        }
        return items.stream().anyMatch(item -> productId.equals(item.getProductId()));
    }
    
    /**
     * Checks if the purchase contains a product in the specified category.
     * 
     * @param category The category to check
     * @return true if the purchase contains a product in the category, false otherwise
     */
    public boolean containsCategory(String category) {
        if (categories == null || category == null) {
            return false;
        }
        return categories.contains(category);
    }
    
    /**
     * Checks if the purchase contains a product from the specified brand.
     * 
     * @param brand The brand to check
     * @return true if the purchase contains a product from the brand, false otherwise
     */
    public boolean containsBrand(String brand) {
        if (brands == null || brand == null) {
            return false;
        }
        return brands.contains(brand);
    }
    
    /**
     * Checks if the purchase is recent (within the last 30 days).
     * 
     * @return true if the purchase is recent, false otherwise
     */
    public boolean isRecent() {
        if (purchaseDate == null) {
            return false;
        }
        return purchaseDate.isAfter(LocalDateTime.now().minusDays(30));
    }
    
    /**
     * Checks if the purchase is a high-value purchase (total amount > $100).
     * 
     * @return true if the purchase is high-value, false otherwise
     */
    public boolean isHighValue() {
        return totalAmount > 100;
    }
    
    /**
     * Checks if the purchase is a bulk purchase (item count >= 5).
     * 
     * @return true if the purchase is a bulk purchase, false otherwise
     */
    public boolean isBulkPurchase() {
        return itemCount >= 5;
    }
    
    @Override
    public String toString() {
        return "PurchaseHistory{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", orderNumber='" + orderNumber + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", totalAmount=" + totalAmount +
                ", itemCount=" + itemCount +
                ", orderStatus='" + orderStatus + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseHistory that = (PurchaseHistory) o;
        return Objects.equals(id, that.id) || 
               Objects.equals(orderNumber, that.orderNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, orderNumber);
    }
    
    /**
     * Inner class representing an item in a purchase.
     */
    public static class PurchaseItem {
        
        /**
         * The product ID
         */
        @NotBlank(message = "Product ID is required")
        private String productId;
        
        /**
         * The product SKU
         */
        private String sku;
        
        /**
         * The product name
         */
        @NotBlank(message = "Product name is required")
        private String productName;
        
        /**
         * The product category
         */
        private String category;
        
        /**
         * The product subcategory
         */
        private String subcategory;
        
        /**
         * The product brand
         */
        private String brand;
        
        /**
         * The quantity purchased
         */
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
        
        /**
         * The unit price
         */
        @Min(value = 0, message = "Unit price must be non-negative")
        private double unitPrice;
        
        /**
         * The total price (unit price * quantity)
         */
        @Min(value = 0, message = "Total price must be non-negative")
        private double totalPrice;
        
        /**
         * The discount amount applied to this item
         */
        @Min(value = 0, message = "Discount amount must be non-negative")
        private double discountAmount;
        
        /**
         * Whether the item was returned
         */
        private boolean returned;
        
        /**
         * Default constructor
         */
        public PurchaseItem() {
        }
        
        /**
         * Constructor with essential fields
         *
         * @param productId The product ID
         * @param productName The product name
         * @param quantity The quantity purchased
         * @param unitPrice The unit price
         */
        public PurchaseItem(String productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }
        
        /**
         * Full constructor
         *
         * @param productId The product ID
         * @param sku The product SKU
         * @param productName The product name
         * @param category The product category
         * @param subcategory The product subcategory
         * @param brand The product brand
         * @param quantity The quantity purchased
         * @param unitPrice The unit price
         * @param discountAmount The discount amount
         */
        public PurchaseItem(String productId, String sku, String productName, String category, 
                           String subcategory, String brand, int quantity, double unitPrice, 
                           double discountAmount) {
            this.productId = productId;
            this.sku = sku;
            this.productName = productName;
            this.category = category;
            this.subcategory = subcategory;
            this.brand = brand;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice - discountAmount;
            this.discountAmount = discountAmount;
        }
        
        // Getters and setters
        
        public String getProductId() {
            return productId;
        }
        
        public void setProductId(String productId) {
            this.productId = productId;
        }
        
        public String getSku() {
            return sku;
        }
        
        public void setSku(String sku) {
            this.sku = sku;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
        
        public String getSubcategory() {
            return subcategory;
        }
        
        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
        }
        
        public String getBrand() {
            return brand;
        }
        
        public void setBrand(String brand) {
            this.brand = brand;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.totalPrice = quantity * unitPrice - discountAmount;
        }
        
        public double getUnitPrice() {
            return unitPrice;
        }
        
        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice - discountAmount;
        }
        
        public double getTotalPrice() {
            return totalPrice;
        }
        
        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }
        
        public double getDiscountAmount() {
            return discountAmount;
        }
        
        public void setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
            this.totalPrice = quantity * unitPrice - discountAmount;
        }
        
        public boolean isReturned() {
            return returned;
        }
        
        public void setReturned(boolean returned) {
            this.returned = returned;
        }
        
        /**
         * Calculates the discount percentage.
         * 
         * @return The discount percentage
         */
        public double getDiscountPercentage() {
            double originalPrice = quantity * unitPrice;
            if (originalPrice > 0) {
                return (discountAmount / originalPrice) * 100;
            }
            return 0;
        }
        
        @Override
        public String toString() {
            return "PurchaseItem{" +
                    "productId='" + productId + '\'' +
                    ", productName='" + productName + '\'' +
                    ", quantity=" + quantity +
                    ", unitPrice=" + unitPrice +
                    ", totalPrice=" + totalPrice +
                    '}';
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PurchaseItem that = (PurchaseItem) o;
            return Objects.equals(productId, that.productId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(productId);
        }
    }
}