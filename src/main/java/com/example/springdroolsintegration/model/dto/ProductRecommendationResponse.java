package com.example.springdroolsintegration.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for product recommendation rule execution response.
 * This class returns the results of product recommendation rule evaluation.
 */
@Schema(description = "Response model for product recommendation rule evaluation results")
public class ProductRecommendationResponse {
    
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
     * The current product ID (if any)
     */
    @Schema(description = "Current product ID (if any)", example = "PROD-123")
    private String currentProductId;
    
    /**
     * The current product name (if any)
     */
    @Schema(description = "Current product name (if any)", example = "Smartphone X")
    private String currentProductName;
    
    /**
     * The recommendation type used
     */
    @Schema(description = "Recommendation type used", example = "SIMILAR")
    private String recommendationType;
    
    /**
     * The list of recommended products
     */
    @Schema(description = "List of recommended products")
    private List<RecommendedProduct> recommendations = new ArrayList<>();
    
    /**
     * The categories included in the recommendations
     */
    @Schema(description = "Categories included in the recommendations", example = "[\"ELECTRONICS\", \"ACCESSORIES\"]")
    private Set<String> categories = new HashSet<>();
    
    /**
     * The brands included in the recommendations
     */
    @Schema(description = "Brands included in the recommendations", example = "[\"Apple\", \"Samsung\"]")
    private Set<String> brands = new HashSet<>();
    
    /**
     * The rules that were applied
     */
    @Schema(description = "Rules that were applied", example = "Similar Products Rule, Complementary Products Rule")
    private String appliedRules;
    
    /**
     * The timestamp when the recommendations were generated
     */
    @Schema(description = "Timestamp when the recommendations were generated")
    private LocalDateTime timestamp;
    
    /**
     * Any additional information or notes about the recommendations
     */
    @Schema(description = "Additional information or notes about the recommendations")
    private String notes;
    
    /**
     * Default constructor
     */
    public ProductRecommendationResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param recommendationType The recommendation type used
     */
    public ProductRecommendationResponse(Long customerId, String customerName, String recommendationType) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.recommendationType = recommendationType;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with current product context
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param currentProductId The current product ID
     * @param currentProductName The current product name
     * @param recommendationType The recommendation type used
     */
    public ProductRecommendationResponse(Long customerId, String customerName, 
                                        String currentProductId, String currentProductName, 
                                        String recommendationType) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentProductId = currentProductId;
        this.currentProductName = currentProductName;
        this.recommendationType = recommendationType;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Full constructor with all fields
     *
     * @param customerId The customer's ID
     * @param customerName The customer's name
     * @param currentProductId The current product ID
     * @param currentProductName The current product name
     * @param recommendationType The recommendation type used
     * @param recommendations The list of recommended products
     * @param categories The categories included in the recommendations
     * @param brands The brands included in the recommendations
     * @param appliedRules The rules that were applied
     * @param timestamp The timestamp when the recommendations were generated
     * @param notes Any additional information or notes
     */
    public ProductRecommendationResponse(Long customerId, String customerName, 
                                        String currentProductId, String currentProductName, 
                                        String recommendationType, List<RecommendedProduct> recommendations, 
                                        Set<String> categories, Set<String> brands, 
                                        String appliedRules, LocalDateTime timestamp, String notes) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentProductId = currentProductId;
        this.currentProductName = currentProductName;
        this.recommendationType = recommendationType;
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
        this.categories = categories != null ? categories : new HashSet<>();
        this.brands = brands != null ? brands : new HashSet<>();
        this.appliedRules = appliedRules;
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
    
    public String getCurrentProductId() {
        return currentProductId;
    }
    
    public void setCurrentProductId(String currentProductId) {
        this.currentProductId = currentProductId;
    }
    
    public String getCurrentProductName() {
        return currentProductName;
    }
    
    public void setCurrentProductName(String currentProductName) {
        this.currentProductName = currentProductName;
    }
    
    public String getRecommendationType() {
        return recommendationType;
    }
    
    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }
    
    public List<RecommendedProduct> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<RecommendedProduct> recommendations) {
        this.recommendations = recommendations != null ? recommendations : new ArrayList<>();
        
        // Extract categories and brands from recommendations
        this.categories.clear();
        this.brands.clear();
        if (this.recommendations != null) {
            for (RecommendedProduct product : this.recommendations) {
                if (product.getCategory() != null) {
                    this.categories.add(product.getCategory());
                }
                if (product.getBrand() != null) {
                    this.brands.add(product.getBrand());
                }
            }
        }
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
    
    public String getAppliedRules() {
        return appliedRules;
    }
    
    public void setAppliedRules(String appliedRules) {
        this.appliedRules = appliedRules;
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
     * Adds a recommended product to the response
     * 
     * @param product The recommended product to add
     */
    public void addRecommendation(RecommendedProduct product) {
        if (this.recommendations == null) {
            this.recommendations = new ArrayList<>();
        }
        this.recommendations.add(product);
        
        // Update categories and brands
        if (product.getCategory() != null) {
            if (this.categories == null) {
                this.categories = new HashSet<>();
            }
            this.categories.add(product.getCategory());
        }
        if (product.getBrand() != null) {
            if (this.brands == null) {
                this.brands = new HashSet<>();
            }
            this.brands.add(product.getBrand());
        }
    }
    
    /**
     * Gets the number of recommendations
     * 
     * @return The number of recommendations
     */
    public int getRecommendationCount() {
        return recommendations != null ? recommendations.size() : 0;
    }
    
    /**
     * Gets the number of unique categories in the recommendations
     * 
     * @return The number of unique categories
     */
    public int getCategoryCount() {
        return categories != null ? categories.size() : 0;
    }
    
    /**
     * Gets the number of unique brands in the recommendations
     * 
     * @return The number of unique brands
     */
    public int getBrandCount() {
        return brands != null ? brands.size() : 0;
    }
    
    @Override
    public String toString() {
        return "ProductRecommendationResponse{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", currentProductId='" + currentProductId + '\'' +
                ", recommendationType='" + recommendationType + '\'' +
                ", recommendations.size=" + getRecommendationCount() +
                ", timestamp=" + timestamp +
                '}';
    }
    
    /**
     * Inner class representing a recommended product
     */
    @Schema(description = "Recommended product details")
    public static class RecommendedProduct {
        
        /**
         * The product ID
         */
        @Schema(description = "Product ID", example = "PROD-456")
        private String productId;
        
        /**
         * The product SKU
         */
        @Schema(description = "Product SKU", example = "SKU12345")
        private String sku;
        
        /**
         * The product name
         */
        @Schema(description = "Product name", example = "Wireless Headphones")
        private String name;
        
        /**
         * The product description
         */
        @Schema(description = "Product description", example = "High-quality wireless headphones with noise cancellation")
        private String description;
        
        /**
         * The product category
         */
        @Schema(description = "Product category", example = "ELECTRONICS")
        private String category;
        
        /**
         * The product subcategory
         */
        @Schema(description = "Product subcategory", example = "AUDIO")
        private String subcategory;
        
        /**
         * The product brand
         */
        @Schema(description = "Product brand", example = "Sony")
        private String brand;
        
        /**
         * The product price
         */
        @Schema(description = "Product price", example = "199.99")
        private double price;
        
        /**
         * The product sale price (if on sale)
         */
        @Schema(description = "Product sale price (if on sale)", example = "149.99")
        private Double salePrice;
        
        /**
         * Whether the product is in stock
         */
        @Schema(description = "Whether the product is in stock", example = "true")
        private boolean inStock;
        
        /**
         * The product's average rating (1-5)
         */
        @Schema(description = "Product's average rating (1-5)", example = "4.5")
        private double averageRating;
        
        /**
         * The number of ratings
         */
        @Schema(description = "Number of ratings", example = "120")
        private int ratingCount;
        
        /**
         * The recommendation score (higher is better)
         */
        @Schema(description = "Recommendation score (higher is better)", example = "0.95")
        private double score;
        
        /**
         * The recommendation reason
         */
        @Schema(description = "Recommendation reason", example = "Based on your purchase history")
        private String reason;
        
        /**
         * The rule that generated this recommendation
         */
        @Schema(description = "Rule that generated this recommendation", example = "Similar Products Rule")
        private String rule;
        
        /**
         * The recommendation type (e.g., SIMILAR, COMPLEMENTARY, POPULAR, TRENDING)
         */
        @Schema(description = "Recommendation type", example = "SIMILAR")
        private String type;
        
        /**
         * Default constructor
         */
        public RecommendedProduct() {
        }
        
        /**
         * Constructor with essential fields
         *
         * @param productId The product ID
         * @param name The product name
         * @param price The product price
         * @param score The recommendation score
         */
        public RecommendedProduct(String productId, String name, double price, double score) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.score = score;
        }
        
        /**
         * Constructor with additional fields
         *
         * @param productId The product ID
         * @param sku The product SKU
         * @param name The product name
         * @param category The product category
         * @param brand The product brand
         * @param price The product price
         * @param inStock Whether the product is in stock
         * @param score The recommendation score
         * @param reason The recommendation reason
         */
        public RecommendedProduct(String productId, String sku, String name, 
                                 String category, String brand, double price, 
                                 boolean inStock, double score, String reason) {
            this.productId = productId;
            this.sku = sku;
            this.name = name;
            this.category = category;
            this.brand = brand;
            this.price = price;
            this.inStock = inStock;
            this.score = score;
            this.reason = reason;
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
        
        public double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public Double getSalePrice() {
            return salePrice;
        }
        
        public void setSalePrice(Double salePrice) {
            this.salePrice = salePrice;
        }
        
        public boolean isInStock() {
            return inStock;
        }
        
        public void setInStock(boolean inStock) {
            this.inStock = inStock;
        }
        
        public double getAverageRating() {
            return averageRating;
        }
        
        public void setAverageRating(double averageRating) {
            this.averageRating = averageRating;
        }
        
        public int getRatingCount() {
            return ratingCount;
        }
        
        public void setRatingCount(int ratingCount) {
            this.ratingCount = ratingCount;
        }
        
        public double getScore() {
            return score;
        }
        
        public void setScore(double score) {
            this.score = score;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getRule() {
            return rule;
        }
        
        public void setRule(String rule) {
            this.rule = rule;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        /**
         * Gets the current price of the product, taking into account sale price if available.
         * 
         * @return The current price
         */
        public double getCurrentPrice() {
            return salePrice != null ? salePrice : price;
        }
        
        /**
         * Gets the discount percentage if the product is on sale.
         * 
         * @return The discount percentage, or 0 if the product is not on sale
         */
        public double getDiscountPercentage() {
            if (salePrice != null && price > 0) {
                return ((price - salePrice) / price) * 100;
            }
            return 0;
        }
        
        /**
         * Checks if the product is on sale.
         * 
         * @return true if the product is on sale, false otherwise
         */
        public boolean isOnSale() {
            return salePrice != null && salePrice < price;
        }
        
        @Override
        public String toString() {
            return "RecommendedProduct{" +
                    "productId='" + productId + '\'' +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", score=" + score +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }
}