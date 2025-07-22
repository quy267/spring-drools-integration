package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Product entity for the product recommendation rules use case.
 * This entity represents a product in the system and contains all relevant
 * information needed for product recommendations.
 */
public class Product {
    
    /**
     * Unique identifier for the product
     */
    private Long id;
    
    /**
     * Product's unique SKU (Stock Keeping Unit)
     */
    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9-]{5,20}$", message = "SKU must be 5-20 alphanumeric characters")
    private String sku;
    
    /**
     * Name of the product
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    private String name;
    
    /**
     * Description of the product
     */
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    /**
     * Short description or tagline for the product
     */
    @Size(max = 200, message = "Short description cannot exceed 200 characters")
    private String shortDescription;
    
    /**
     * Primary category of the product
     */
    @NotBlank(message = "Category is required")
    private String category;
    
    /**
     * Subcategory of the product
     */
    private String subcategory;
    
    /**
     * Brand of the product
     */
    private String brand;
    
    /**
     * Manufacturer of the product
     */
    private String manufacturer;
    
    /**
     * Regular price of the product
     */
    @Min(value = 0, message = "Price must be non-negative")
    private double price;
    
    /**
     * Sale price of the product (if on sale)
     */
    @Min(value = 0, message = "Sale price must be non-negative")
    private Double salePrice;
    
    /**
     * Cost of the product
     */
    @Min(value = 0, message = "Cost must be non-negative")
    private double cost;
    
    /**
     * Profit margin percentage
     */
    private double marginPercentage;
    
    /**
     * Current inventory level
     */
    @Min(value = 0, message = "Inventory level must be non-negative")
    private int inventoryLevel;
    
    /**
     * Minimum inventory level before reordering
     */
    @Min(value = 0, message = "Reorder level must be non-negative")
    private int reorderLevel;
    
    /**
     * Maximum inventory level
     */
    @Min(value = 0, message = "Maximum inventory level must be non-negative")
    private int maxInventoryLevel;
    
    /**
     * Whether the product is in stock
     */
    private boolean inStock;
    
    /**
     * Whether the product is on sale
     */
    private boolean onSale;
    
    /**
     * Whether the product is featured
     */
    private boolean featured;
    
    /**
     * Whether the product is a new arrival
     */
    private boolean newArrival;
    
    /**
     * Whether the product is a bestseller
     */
    private boolean bestseller;
    
    /**
     * Whether the product is a seasonal item
     */
    private boolean seasonal;
    
    /**
     * The season for seasonal products (e.g., SPRING, SUMMER, FALL, WINTER)
     */
    private String season;
    
    /**
     * Average rating of the product (1-5)
     */
    private double averageRating;
    
    /**
     * Number of ratings received
     */
    @Min(value = 0, message = "Rating count must be non-negative")
    private int ratingCount;
    
    /**
     * Number of reviews received
     */
    @Min(value = 0, message = "Review count must be non-negative")
    private int reviewCount;
    
    /**
     * Number of times the product has been viewed
     */
    @Min(value = 0, message = "View count must be non-negative")
    private int viewCount;
    
    /**
     * Number of times the product has been purchased
     */
    @Min(value = 0, message = "Purchase count must be non-negative")
    private int purchaseCount;
    
    /**
     * Number of times the product has been added to a wish list
     */
    @Min(value = 0, message = "Wish list count must be non-negative")
    private int wishListCount;
    
    /**
     * Number of times the product has been added to a cart
     */
    @Min(value = 0, message = "Cart add count must be non-negative")
    private int cartAddCount;
    
    /**
     * Number of times the product has been returned
     */
    @Min(value = 0, message = "Return count must be non-negative")
    private int returnCount;
    
    /**
     * Return rate percentage
     */
    @Min(value = 0, message = "Return rate must be non-negative")
    private double returnRate;
    
    /**
     * Date when the product was created
     */
    @NotNull(message = "Creation date is required")
    private LocalDateTime createdAt;
    
    /**
     * Date when the product was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Date when the product was launched
     */
    private LocalDate launchDate;
    
    /**
     * Date when the product will be discontinued
     */
    private LocalDate discontinuationDate;
    
    /**
     * Weight of the product in kilograms
     */
    private Double weight;
    
    /**
     * Length of the product in centimeters
     */
    private Double length;
    
    /**
     * Width of the product in centimeters
     */
    private Double width;
    
    /**
     * Height of the product in centimeters
     */
    private Double height;
    
    /**
     * Color of the product
     */
    private String color;
    
    /**
     * Size of the product
     */
    private String size;
    
    /**
     * Material of the product
     */
    private String material;
    
    /**
     * Tags associated with the product
     */
    private Set<String> tags = new HashSet<>();
    
    /**
     * Keywords for search optimization
     */
    private Set<String> keywords = new HashSet<>();
    
    /**
     * IDs of related products
     */
    private Set<String> relatedProducts = new HashSet<>();
    
    /**
     * IDs of frequently bought together products
     */
    private Set<String> frequentlyBoughtTogether = new HashSet<>();
    
    /**
     * IDs of complementary products
     */
    private Set<String> complementaryProducts = new HashSet<>();
    
    /**
     * IDs of substitute products
     */
    private Set<String> substituteProducts = new HashSet<>();
    
    /**
     * Default constructor
     */
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param sku The product's SKU
     * @param name The product's name
     * @param category The product's category
     * @param price The product's price
     */
    public Product(String sku, String name, String category, double price) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.inventoryLevel = 0;
        this.inStock = false;
    }
    
    /**
     * Constructor with inventory information
     *
     * @param sku The product's SKU
     * @param name The product's name
     * @param category The product's category
     * @param price The product's price
     * @param inventoryLevel The product's inventory level
     */
    public Product(String sku, String name, String category, double price, int inventoryLevel) {
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.price = price;
        this.inventoryLevel = inventoryLevel;
        this.inStock = inventoryLevel > 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
        updateMarginPercentage();
    }
    
    public Double getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
        this.onSale = salePrice != null && salePrice < price;
    }
    
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
        updateMarginPercentage();
    }
    
    public double getMarginPercentage() {
        return marginPercentage;
    }
    
    public void setMarginPercentage(double marginPercentage) {
        this.marginPercentage = marginPercentage;
    }
    
    public int getInventoryLevel() {
        return inventoryLevel;
    }
    
    public void setInventoryLevel(int inventoryLevel) {
        this.inventoryLevel = inventoryLevel;
        this.inStock = inventoryLevel > 0;
    }
    
    public int getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public int getMaxInventoryLevel() {
        return maxInventoryLevel;
    }
    
    public void setMaxInventoryLevel(int maxInventoryLevel) {
        this.maxInventoryLevel = maxInventoryLevel;
    }
    
    public boolean isInStock() {
        return inStock;
    }
    
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    
    public boolean isOnSale() {
        return onSale;
    }
    
    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }
    
    public boolean isFeatured() {
        return featured;
    }
    
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public boolean isNewArrival() {
        return newArrival;
    }
    
    public void setNewArrival(boolean newArrival) {
        this.newArrival = newArrival;
    }
    
    public boolean isBestseller() {
        return bestseller;
    }
    
    public void setBestseller(boolean bestseller) {
        this.bestseller = bestseller;
    }
    
    public boolean isSeasonal() {
        return seasonal;
    }
    
    public void setSeasonal(boolean seasonal) {
        this.seasonal = seasonal;
    }
    
    public String getSeason() {
        return season;
    }
    
    public void setSeason(String season) {
        this.season = season;
        this.seasonal = season != null && !season.isEmpty();
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
    
    public int getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
    
    public int getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }
    
    public int getPurchaseCount() {
        return purchaseCount;
    }
    
    public void setPurchaseCount(int purchaseCount) {
        this.purchaseCount = purchaseCount;
        updateBestsellerStatus();
    }
    
    public int getWishListCount() {
        return wishListCount;
    }
    
    public void setWishListCount(int wishListCount) {
        this.wishListCount = wishListCount;
    }
    
    public int getCartAddCount() {
        return cartAddCount;
    }
    
    public void setCartAddCount(int cartAddCount) {
        this.cartAddCount = cartAddCount;
    }
    
    public int getReturnCount() {
        return returnCount;
    }
    
    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
        updateReturnRate();
    }
    
    public double getReturnRate() {
        return returnRate;
    }
    
    public void setReturnRate(double returnRate) {
        this.returnRate = returnRate;
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
    
    public LocalDate getLaunchDate() {
        return launchDate;
    }
    
    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
        updateNewArrivalStatus();
    }
    
    public LocalDate getDiscontinuationDate() {
        return discontinuationDate;
    }
    
    public void setDiscontinuationDate(LocalDate discontinuationDate) {
        this.discontinuationDate = discontinuationDate;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getLength() {
        return length;
    }
    
    public void setLength(Double length) {
        this.length = length;
    }
    
    public Double getWidth() {
        return width;
    }
    
    public void setWidth(Double width) {
        this.width = width;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public Set<String> getTags() {
        return tags;
    }
    
    public void setTags(Set<String> tags) {
        this.tags = tags != null ? tags : new HashSet<>();
    }
    
    public Set<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords != null ? keywords : new HashSet<>();
    }
    
    public Set<String> getRelatedProducts() {
        return relatedProducts;
    }
    
    public void setRelatedProducts(Set<String> relatedProducts) {
        this.relatedProducts = relatedProducts != null ? relatedProducts : new HashSet<>();
    }
    
    public Set<String> getFrequentlyBoughtTogether() {
        return frequentlyBoughtTogether;
    }
    
    public void setFrequentlyBoughtTogether(Set<String> frequentlyBoughtTogether) {
        this.frequentlyBoughtTogether = frequentlyBoughtTogether != null ? frequentlyBoughtTogether : new HashSet<>();
    }
    
    public Set<String> getComplementaryProducts() {
        return complementaryProducts;
    }
    
    public void setComplementaryProducts(Set<String> complementaryProducts) {
        this.complementaryProducts = complementaryProducts != null ? complementaryProducts : new HashSet<>();
    }
    
    public Set<String> getSubstituteProducts() {
        return substituteProducts;
    }
    
    public void setSubstituteProducts(Set<String> substituteProducts) {
        this.substituteProducts = substituteProducts != null ? substituteProducts : new HashSet<>();
    }
    
    /**
     * Updates the margin percentage based on price and cost.
     */
    private void updateMarginPercentage() {
        if (price > 0 && cost > 0) {
            this.marginPercentage = ((price - cost) / price) * 100;
        } else {
            this.marginPercentage = 0;
        }
    }
    
    /**
     * Updates the return rate based on purchase count and return count.
     */
    private void updateReturnRate() {
        if (purchaseCount > 0) {
            this.returnRate = ((double) returnCount / purchaseCount) * 100;
        } else {
            this.returnRate = 0;
        }
    }
    
    /**
     * Updates the bestseller status based on purchase count.
     * A product is considered a bestseller if it has been purchased more than 100 times.
     */
    private void updateBestsellerStatus() {
        this.bestseller = purchaseCount > 100;
    }
    
    /**
     * Updates the new arrival status based on launch date.
     * A product is considered a new arrival if it was launched within the last 30 days.
     */
    private void updateNewArrivalStatus() {
        if (launchDate != null) {
            this.newArrival = launchDate.isAfter(LocalDate.now().minusDays(30));
        } else {
            this.newArrival = false;
        }
    }
    
    /**
     * Adds a tag to the product.
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
     * Adds a keyword to the product.
     * 
     * @param keyword The keyword to add
     */
    public void addKeyword(String keyword) {
        if (this.keywords == null) {
            this.keywords = new HashSet<>();
        }
        this.keywords.add(keyword);
    }
    
    /**
     * Adds a related product to the product.
     * 
     * @param productId The product ID to add
     */
    public void addRelatedProduct(String productId) {
        if (this.relatedProducts == null) {
            this.relatedProducts = new HashSet<>();
        }
        this.relatedProducts.add(productId);
    }
    
    /**
     * Adds a frequently bought together product to the product.
     * 
     * @param productId The product ID to add
     */
    public void addFrequentlyBoughtTogether(String productId) {
        if (this.frequentlyBoughtTogether == null) {
            this.frequentlyBoughtTogether = new HashSet<>();
        }
        this.frequentlyBoughtTogether.add(productId);
    }
    
    /**
     * Adds a complementary product to the product.
     * 
     * @param productId The product ID to add
     */
    public void addComplementaryProduct(String productId) {
        if (this.complementaryProducts == null) {
            this.complementaryProducts = new HashSet<>();
        }
        this.complementaryProducts.add(productId);
    }
    
    /**
     * Adds a substitute product to the product.
     * 
     * @param productId The product ID to add
     */
    public void addSubstituteProduct(String productId) {
        if (this.substituteProducts == null) {
            this.substituteProducts = new HashSet<>();
        }
        this.substituteProducts.add(productId);
    }
    
    /**
     * Records a view of the product.
     */
    public void recordView() {
        this.viewCount++;
    }
    
    /**
     * Records a purchase of the product.
     * 
     * @param quantity The quantity purchased
     */
    public void recordPurchase(int quantity) {
        this.purchaseCount += quantity;
        this.inventoryLevel -= quantity;
        this.inStock = inventoryLevel > 0;
        updateBestsellerStatus();
    }
    
    /**
     * Records a return of the product.
     * 
     * @param quantity The quantity returned
     */
    public void recordReturn(int quantity) {
        this.returnCount += quantity;
        this.inventoryLevel += quantity;
        this.inStock = inventoryLevel > 0;
        updateReturnRate();
    }
    
    /**
     * Records a rating of the product.
     * 
     * @param rating The rating (1-5)
     */
    public void recordRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        double totalRating = this.averageRating * this.ratingCount;
        this.ratingCount++;
        this.averageRating = (totalRating + rating) / this.ratingCount;
    }
    
    /**
     * Records a review of the product.
     */
    public void recordReview() {
        this.reviewCount++;
    }
    
    /**
     * Records an addition to a wish list.
     */
    public void recordWishListAdd() {
        this.wishListCount++;
    }
    
    /**
     * Records an addition to a cart.
     */
    public void recordCartAdd() {
        this.cartAddCount++;
    }
    
    /**
     * Checks if the product needs to be reordered.
     * 
     * @return true if the inventory level is below the reorder level, false otherwise
     */
    public boolean needsReorder() {
        return inventoryLevel <= reorderLevel;
    }
    
    /**
     * Checks if the product is highly rated (average rating >= 4.0).
     * 
     * @return true if the product is highly rated, false otherwise
     */
    public boolean isHighlyRated() {
        return averageRating >= 4.0 && ratingCount >= 10;
    }
    
    /**
     * Checks if the product is popular (view count > 1000).
     * 
     * @return true if the product is popular, false otherwise
     */
    public boolean isPopular() {
        return viewCount > 1000;
    }
    
    /**
     * Checks if the product has a high return rate (return rate > 10%).
     * 
     * @return true if the product has a high return rate, false otherwise
     */
    public boolean hasHighReturnRate() {
        return returnRate > 10.0;
    }
    
    /**
     * Gets the current price of the product, taking into account sale price if available.
     * 
     * @return The current price
     */
    public double getCurrentPrice() {
        return onSale && salePrice != null ? salePrice : price;
    }
    
    /**
     * Gets the discount percentage if the product is on sale.
     * 
     * @return The discount percentage, or 0 if the product is not on sale
     */
    public double getDiscountPercentage() {
        if (onSale && salePrice != null && price > 0) {
            return ((price - salePrice) / price) * 100;
        }
        return 0;
    }
    
    /**
     * Checks if the product is in the specified category or subcategory.
     * 
     * @param categoryName The category or subcategory name to check
     * @return true if the product is in the specified category or subcategory, false otherwise
     */
    public boolean isInCategory(String categoryName) {
        if (categoryName == null) {
            return false;
        }
        return categoryName.equalsIgnoreCase(category) || 
               categoryName.equalsIgnoreCase(subcategory);
    }
    
    /**
     * Checks if the product is from the specified brand.
     * 
     * @param brandName The brand name to check
     * @return true if the product is from the specified brand, false otherwise
     */
    public boolean isFromBrand(String brandName) {
        if (brandName == null || brand == null) {
            return false;
        }
        return brandName.equalsIgnoreCase(brand);
    }
    
    /**
     * Checks if the product has the specified tag.
     * 
     * @param tagName The tag name to check
     * @return true if the product has the specified tag, false otherwise
     */
    public boolean hasTag(String tagName) {
        if (tagName == null || tags == null) {
            return false;
        }
        return tags.contains(tagName);
    }
    
    /**
     * Checks if the product is related to the specified product.
     * 
     * @param productId The product ID to check
     * @return true if the product is related to the specified product, false otherwise
     */
    public boolean isRelatedTo(String productId) {
        if (productId == null || relatedProducts == null) {
            return false;
        }
        return relatedProducts.contains(productId);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", salePrice=" + salePrice +
                ", inventoryLevel=" + inventoryLevel +
                ", inStock=" + inStock +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) || 
               Objects.equals(sku, product.sku);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, sku);
    }
}