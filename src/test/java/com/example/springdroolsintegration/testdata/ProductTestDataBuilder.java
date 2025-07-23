package com.example.springdroolsintegration.testdata;

import com.example.springdroolsintegration.model.entity.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Test data builder for Product entity following the builder pattern.
 * Provides fluent API for creating Product instances with default values for testing.
 */
public class ProductTestDataBuilder {
    
    private Product product;
    
    private ProductTestDataBuilder() {
        this.product = new Product();
        // Set default values for testing
        this.product.setId(1L);
        this.product.setSku("PROD-001");
        this.product.setName("Test Product");
        this.product.setDescription("A test product for unit testing");
        this.product.setShortDescription("Test product");
        this.product.setCategory("Electronics");
        this.product.setSubcategory("Smartphones");
        this.product.setBrand("TestBrand");
        this.product.setManufacturer("Test Manufacturer");
        this.product.setPrice(299.99);
        this.product.setCost(150.00);
        this.product.setMarginPercentage(50.0);
        this.product.setInventoryLevel(100);
        this.product.setReorderLevel(20);
        this.product.setMaxInventoryLevel(500);
        this.product.setInStock(true);
        this.product.setOnSale(false);
        this.product.setFeatured(false);
        this.product.setNewArrival(false);
        this.product.setBestseller(false);
        this.product.setSeasonal(false);
        this.product.setAverageRating(4.0);
        this.product.setRatingCount(25);
        this.product.setReviewCount(15);
        this.product.setViewCount(500);
        this.product.setPurchaseCount(50);
        this.product.setWishListCount(10);
        this.product.setCartAddCount(75);
        this.product.setReturnCount(2);
        this.product.setReturnRate(4.0);
        this.product.setCreatedAt(LocalDateTime.now().minusMonths(6));
        this.product.setUpdatedAt(LocalDateTime.now());
        this.product.setLaunchDate(LocalDate.now().minusMonths(6));
        this.product.setWeight(0.5);
        this.product.setLength(15.0);
        this.product.setWidth(8.0);
        this.product.setHeight(1.0);
        this.product.setColor("Black");
        this.product.setSize("Medium");
        this.product.setMaterial("Plastic");
        this.product.setTags(new HashSet<>(Set.of("electronics", "smartphone", "mobile")));
        this.product.setKeywords(new HashSet<>(Set.of("phone", "mobile", "communication")));
        this.product.setRelatedProducts(new HashSet<>());
        this.product.setFrequentlyBoughtTogether(new HashSet<>());
        this.product.setComplementaryProducts(new HashSet<>());
        this.product.setSubstituteProducts(new HashSet<>());
    }
    
    /**
     * Creates a new ProductTestDataBuilder instance with default values.
     * @return new ProductTestDataBuilder instance
     */
    public static ProductTestDataBuilder aProduct() {
        return new ProductTestDataBuilder();
    }
    
    /**
     * Creates a ProductTestDataBuilder for an electronics product.
     * @return ProductTestDataBuilder with electronics defaults
     */
    public static ProductTestDataBuilder anElectronicsProduct() {
        return new ProductTestDataBuilder()
                .withCategory("Electronics")
                .withSubcategory("Smartphones")
                .withBrand("TechBrand")
                .withPrice(599.99)
                .withCost(300.00)
                .withTags(Set.of("electronics", "technology", "gadget"));
    }
    
    /**
     * Creates a ProductTestDataBuilder for a clothing product.
     * @return ProductTestDataBuilder with clothing defaults
     */
    public static ProductTestDataBuilder aClothingProduct() {
        return new ProductTestDataBuilder()
                .withCategory("Clothing")
                .withSubcategory("Shirts")
                .withBrand("FashionBrand")
                .withPrice(49.99)
                .withCost(20.00)
                .withSize("Large")
                .withColor("Blue")
                .withMaterial("Cotton")
                .withTags(Set.of("clothing", "fashion", "apparel"));
    }
    
    /**
     * Creates a ProductTestDataBuilder for a home & garden product.
     * @return ProductTestDataBuilder with home & garden defaults
     */
    public static ProductTestDataBuilder aHomeGardenProduct() {
        return new ProductTestDataBuilder()
                .withCategory("Home & Garden")
                .withSubcategory("Furniture")
                .withBrand("HomeBrand")
                .withPrice(199.99)
                .withCost(80.00)
                .withWeight(25.0)
                .withTags(Set.of("home", "furniture", "decor"));
    }
    
    /**
     * Creates a ProductTestDataBuilder for a bestseller product.
     * @return ProductTestDataBuilder with bestseller defaults
     */
    public static ProductTestDataBuilder aBestsellerProduct() {
        return new ProductTestDataBuilder()
                .withBestseller(true)
                .withFeatured(true)
                .withAverageRating(4.8)
                .withRatingCount(500)
                .withReviewCount(300)
                .withPurchaseCount(1000)
                .withViewCount(5000);
    }
    
    /**
     * Creates a ProductTestDataBuilder for a new arrival product.
     * @return ProductTestDataBuilder with new arrival defaults
     */
    public static ProductTestDataBuilder aNewArrivalProduct() {
        return new ProductTestDataBuilder()
                .withNewArrival(true)
                .withLaunchDate(LocalDate.now().minusDays(7))
                .withCreatedAt(LocalDateTime.now().minusDays(7))
                .withViewCount(100)
                .withPurchaseCount(5);
    }
    
    /**
     * Creates a ProductTestDataBuilder for a sale product.
     * @return ProductTestDataBuilder with sale defaults
     */
    public static ProductTestDataBuilder aSaleProduct() {
        return new ProductTestDataBuilder()
                .withOnSale(true)
                .withPrice(299.99)
                .withSalePrice(199.99)
                .withFeatured(true);
    }
    
    /**
     * Creates a ProductTestDataBuilder for an out-of-stock product.
     * @return ProductTestDataBuilder with out-of-stock defaults
     */
    public static ProductTestDataBuilder anOutOfStockProduct() {
        return new ProductTestDataBuilder()
                .withInStock(false)
                .withInventoryLevel(0)
                .withReorderLevel(50);
    }
    
    /**
     * Creates a ProductTestDataBuilder for a high-rated product.
     * @return ProductTestDataBuilder with high rating defaults
     */
    public static ProductTestDataBuilder aHighRatedProduct() {
        return new ProductTestDataBuilder()
                .withAverageRating(4.9)
                .withRatingCount(1000)
                .withReviewCount(750)
                .withReturnRate(1.0);
    }
    
    /**
     * Creates a ProductTestDataBuilder for a seasonal product.
     * @return ProductTestDataBuilder with seasonal defaults
     */
    public static ProductTestDataBuilder aSeasonalProduct() {
        return new ProductTestDataBuilder()
                .withSeasonal(true)
                .withSeason("Winter")
                .withCategory("Clothing")
                .withSubcategory("Coats");
    }
    
    public ProductTestDataBuilder withId(Long id) {
        this.product.setId(id);
        return this;
    }
    
    public ProductTestDataBuilder withSku(String sku) {
        this.product.setSku(sku);
        return this;
    }
    
    public ProductTestDataBuilder withName(String name) {
        this.product.setName(name);
        return this;
    }
    
    public ProductTestDataBuilder withDescription(String description) {
        this.product.setDescription(description);
        return this;
    }
    
    public ProductTestDataBuilder withShortDescription(String shortDescription) {
        this.product.setShortDescription(shortDescription);
        return this;
    }
    
    public ProductTestDataBuilder withCategory(String category) {
        this.product.setCategory(category);
        return this;
    }
    
    public ProductTestDataBuilder withSubcategory(String subcategory) {
        this.product.setSubcategory(subcategory);
        return this;
    }
    
    public ProductTestDataBuilder withBrand(String brand) {
        this.product.setBrand(brand);
        return this;
    }
    
    public ProductTestDataBuilder withManufacturer(String manufacturer) {
        this.product.setManufacturer(manufacturer);
        return this;
    }
    
    public ProductTestDataBuilder withPrice(double price) {
        this.product.setPrice(price);
        return this;
    }
    
    public ProductTestDataBuilder withSalePrice(Double salePrice) {
        this.product.setSalePrice(salePrice);
        return this;
    }
    
    public ProductTestDataBuilder withCost(double cost) {
        this.product.setCost(cost);
        return this;
    }
    
    public ProductTestDataBuilder withMarginPercentage(double marginPercentage) {
        this.product.setMarginPercentage(marginPercentage);
        return this;
    }
    
    public ProductTestDataBuilder withInventoryLevel(int inventoryLevel) {
        this.product.setInventoryLevel(inventoryLevel);
        return this;
    }
    
    public ProductTestDataBuilder withReorderLevel(int reorderLevel) {
        this.product.setReorderLevel(reorderLevel);
        return this;
    }
    
    public ProductTestDataBuilder withMaxInventoryLevel(int maxInventoryLevel) {
        this.product.setMaxInventoryLevel(maxInventoryLevel);
        return this;
    }
    
    public ProductTestDataBuilder withInStock(boolean inStock) {
        this.product.setInStock(inStock);
        return this;
    }
    
    public ProductTestDataBuilder withOnSale(boolean onSale) {
        this.product.setOnSale(onSale);
        return this;
    }
    
    public ProductTestDataBuilder withFeatured(boolean featured) {
        this.product.setFeatured(featured);
        return this;
    }
    
    public ProductTestDataBuilder withNewArrival(boolean newArrival) {
        this.product.setNewArrival(newArrival);
        return this;
    }
    
    public ProductTestDataBuilder withBestseller(boolean bestseller) {
        this.product.setBestseller(bestseller);
        return this;
    }
    
    public ProductTestDataBuilder withSeasonal(boolean seasonal) {
        this.product.setSeasonal(seasonal);
        return this;
    }
    
    public ProductTestDataBuilder withSeason(String season) {
        this.product.setSeason(season);
        return this;
    }
    
    public ProductTestDataBuilder withAverageRating(double averageRating) {
        this.product.setAverageRating(averageRating);
        return this;
    }
    
    public ProductTestDataBuilder withRatingCount(int ratingCount) {
        this.product.setRatingCount(ratingCount);
        return this;
    }
    
    public ProductTestDataBuilder withReviewCount(int reviewCount) {
        this.product.setReviewCount(reviewCount);
        return this;
    }
    
    public ProductTestDataBuilder withViewCount(int viewCount) {
        this.product.setViewCount(viewCount);
        return this;
    }
    
    public ProductTestDataBuilder withPurchaseCount(int purchaseCount) {
        this.product.setPurchaseCount(purchaseCount);
        return this;
    }
    
    public ProductTestDataBuilder withWishListCount(int wishListCount) {
        this.product.setWishListCount(wishListCount);
        return this;
    }
    
    public ProductTestDataBuilder withCartAddCount(int cartAddCount) {
        this.product.setCartAddCount(cartAddCount);
        return this;
    }
    
    public ProductTestDataBuilder withReturnCount(int returnCount) {
        this.product.setReturnCount(returnCount);
        return this;
    }
    
    public ProductTestDataBuilder withReturnRate(double returnRate) {
        this.product.setReturnRate(returnRate);
        return this;
    }
    
    public ProductTestDataBuilder withCreatedAt(LocalDateTime createdAt) {
        this.product.setCreatedAt(createdAt);
        return this;
    }
    
    public ProductTestDataBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.product.setUpdatedAt(updatedAt);
        return this;
    }
    
    public ProductTestDataBuilder withLaunchDate(LocalDate launchDate) {
        this.product.setLaunchDate(launchDate);
        return this;
    }
    
    public ProductTestDataBuilder withDiscontinuationDate(LocalDate discontinuationDate) {
        this.product.setDiscontinuationDate(discontinuationDate);
        return this;
    }
    
    public ProductTestDataBuilder withWeight(Double weight) {
        this.product.setWeight(weight);
        return this;
    }
    
    public ProductTestDataBuilder withLength(Double length) {
        this.product.setLength(length);
        return this;
    }
    
    public ProductTestDataBuilder withWidth(Double width) {
        this.product.setWidth(width);
        return this;
    }
    
    public ProductTestDataBuilder withHeight(Double height) {
        this.product.setHeight(height);
        return this;
    }
    
    public ProductTestDataBuilder withColor(String color) {
        this.product.setColor(color);
        return this;
    }
    
    public ProductTestDataBuilder withSize(String size) {
        this.product.setSize(size);
        return this;
    }
    
    public ProductTestDataBuilder withMaterial(String material) {
        this.product.setMaterial(material);
        return this;
    }
    
    public ProductTestDataBuilder withTags(Set<String> tags) {
        this.product.setTags(new HashSet<>(tags));
        return this;
    }
    
    public ProductTestDataBuilder withKeywords(Set<String> keywords) {
        this.product.setKeywords(new HashSet<>(keywords));
        return this;
    }
    
    public ProductTestDataBuilder withRelatedProducts(Set<String> relatedProducts) {
        this.product.setRelatedProducts(new HashSet<>(relatedProducts));
        return this;
    }
    
    public ProductTestDataBuilder withFrequentlyBoughtTogether(Set<String> frequentlyBoughtTogether) {
        this.product.setFrequentlyBoughtTogether(new HashSet<>(frequentlyBoughtTogether));
        return this;
    }
    
    public ProductTestDataBuilder withComplementaryProducts(Set<String> complementaryProducts) {
        this.product.setComplementaryProducts(new HashSet<>(complementaryProducts));
        return this;
    }
    
    public ProductTestDataBuilder withSubstituteProducts(Set<String> substituteProducts) {
        this.product.setSubstituteProducts(new HashSet<>(substituteProducts));
        return this;
    }
    
    /**
     * Sets the product as discontinued.
     * @return ProductTestDataBuilder configured as discontinued
     */
    public ProductTestDataBuilder asDiscontinued() {
        this.product.setDiscontinuationDate(LocalDate.now().minusMonths(1));
        this.product.setInStock(false);
        this.product.setInventoryLevel(0);
        return this;
    }
    
    /**
     * Sets the product as premium with high price and quality.
     * @return ProductTestDataBuilder configured as premium
     */
    public ProductTestDataBuilder asPremium() {
        this.product.setPrice(999.99);
        this.product.setCost(400.00);
        this.product.setAverageRating(4.8);
        this.product.setFeatured(true);
        this.product.setBrand("Premium Brand");
        return this;
    }
    
    /**
     * Sets the product as budget-friendly with low price.
     * @return ProductTestDataBuilder configured as budget
     */
    public ProductTestDataBuilder asBudget() {
        this.product.setPrice(19.99);
        this.product.setCost(8.00);
        this.product.setBrand("Budget Brand");
        this.product.setAverageRating(3.5);
        return this;
    }
    
    /**
     * Builds and returns the Product instance.
     * @return configured Product instance
     */
    public Product build() {
        return this.product;
    }
}