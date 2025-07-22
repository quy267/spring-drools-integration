package com.example.springdroolsintegration.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request model for product recommendation rule execution.
 * This DTO captures input data for product recommendation rule evaluation.
 */
@Schema(description = "Request model for product recommendation rule evaluation")
public class ProductRecommendationRequest {
    
    /**
     * The customer's ID (optional if customer details are provided)
     */
    @Schema(description = "Customer ID (optional if customer details are provided)", example = "1001")
    private Long customerId;
    
    /**
     * The customer's account number (optional if customer ID is provided)
     */
    @Schema(description = "Customer account number (optional if customer ID is provided)", example = "CUST12345")
    @Pattern(regexp = "^[A-Z0-9]{6,10}$", message = "Account number must be 6-10 alphanumeric characters")
    private String accountNumber;
    
    /**
     * The customer's first name
     */
    @Schema(description = "Customer first name", example = "John")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    /**
     * The customer's last name
     */
    @Schema(description = "Customer last name", example = "Doe")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    /**
     * The customer's email address
     */
    @Schema(description = "Customer email address", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * The customer's age
     */
    @Schema(description = "Customer age", example = "35")
    @Min(value = 0, message = "Age must be non-negative")
    private Integer age;
    
    /**
     * The customer's gender (M, F, O for other)
     */
    @Schema(description = "Customer gender (M, F, O for other)", example = "M")
    @Pattern(regexp = "^[MFO]$", message = "Gender must be M, F, or O")
    private String gender;
    
    /**
     * The customer's loyalty tier (e.g., BRONZE, SILVER, GOLD, PLATINUM)
     */
    @Schema(description = "Customer loyalty tier", example = "GOLD")
    private String loyaltyTier;
    
    /**
     * The customer's zip/postal code
     */
    @Schema(description = "Customer zip/postal code", example = "10001")
    @Size(max = 10, message = "Zip code cannot exceed 10 characters")
    private String zipCode;
    
    /**
     * The customer's city
     */
    @Schema(description = "Customer city", example = "New York")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;
    
    /**
     * The customer's state/province
     */
    @Schema(description = "Customer state/province", example = "NY")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;
    
    /**
     * The customer's country
     */
    @Schema(description = "Customer country", example = "USA")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;
    
    /**
     * The customer's preferred shopping categories
     */
    @Schema(description = "Customer preferred shopping categories", example = "[\"ELECTRONICS\", \"BOOKS\"]")
    private Set<String> preferredCategories = new HashSet<>();
    
    /**
     * The customer's preferred brands
     */
    @Schema(description = "Customer preferred brands", example = "[\"Apple\", \"Samsung\"]")
    private Set<String> preferredBrands = new HashSet<>();
    
    /**
     * The customer's recently viewed product IDs
     */
    @Schema(description = "Customer recently viewed product IDs", example = "[\"PROD-123\", \"PROD-456\"]")
    private Set<String> recentlyViewedProducts = new HashSet<>();
    
    /**
     * The customer's recently purchased product IDs
     */
    @Schema(description = "Customer recently purchased product IDs", example = "[\"PROD-789\", \"PROD-012\"]")
    private Set<String> recentlyPurchasedProducts = new HashSet<>();
    
    /**
     * The customer's wish list product IDs
     */
    @Schema(description = "Customer wish list product IDs", example = "[\"PROD-345\", \"PROD-678\"]")
    private Set<String> wishListItems = new HashSet<>();
    
    /**
     * The customer's abandoned cart product IDs
     */
    @Schema(description = "Customer abandoned cart product IDs", example = "[\"PROD-901\", \"PROD-234\"]")
    private Set<String> abandonedCartItems = new HashSet<>();
    
    /**
     * The current product ID (for related product recommendations)
     */
    @Schema(description = "Current product ID (for related product recommendations)", example = "PROD-567")
    private String currentProductId;
    
    /**
     * The current product category (for category-based recommendations)
     */
    @Schema(description = "Current product category (for category-based recommendations)", example = "ELECTRONICS")
    private String currentCategory;
    
    /**
     * The current product brand (for brand-based recommendations)
     */
    @Schema(description = "Current product brand (for brand-based recommendations)", example = "Apple")
    private String currentBrand;
    
    /**
     * The current shopping cart product IDs
     */
    @Schema(description = "Current shopping cart product IDs", example = "[\"PROD-123\", \"PROD-456\"]")
    private Set<String> currentCartItems = new HashSet<>();
    
    /**
     * The current season (e.g., SPRING, SUMMER, FALL, WINTER)
     */
    @Schema(description = "Current season (e.g., SPRING, SUMMER, FALL, WINTER)", example = "SUMMER")
    private String currentSeason;
    
    /**
     * The current shopping channel (e.g., ONLINE, IN_STORE, MOBILE_APP)
     */
    @Schema(description = "Current shopping channel (e.g., ONLINE, IN_STORE, MOBILE_APP)", example = "ONLINE")
    private String shoppingChannel;
    
    /**
     * The current device type (e.g., DESKTOP, MOBILE, TABLET)
     */
    @Schema(description = "Current device type (e.g., DESKTOP, MOBILE, TABLET)", example = "MOBILE")
    private String deviceType;
    
    /**
     * The maximum number of recommendations to return
     */
    @Schema(description = "Maximum number of recommendations to return", example = "5")
    @Min(value = 1, message = "Maximum recommendations must be at least 1")
    private int maxRecommendations = 5;
    
    /**
     * Whether to include out-of-stock products in recommendations
     */
    @Schema(description = "Whether to include out-of-stock products in recommendations", example = "false")
    private boolean includeOutOfStock = false;
    
    /**
     * Whether to include products the customer has already purchased
     */
    @Schema(description = "Whether to include products the customer has already purchased", example = "false")
    private boolean includePreviouslyPurchased = false;
    
    /**
     * The recommendation type (e.g., SIMILAR, COMPLEMENTARY, POPULAR, TRENDING)
     */
    @Schema(description = "Recommendation type (e.g., SIMILAR, COMPLEMENTARY, POPULAR, TRENDING)", example = "SIMILAR")
    private String recommendationType;
    
    /**
     * The price range minimum
     */
    @Schema(description = "Price range minimum", example = "0")
    @Min(value = 0, message = "Price range minimum must be non-negative")
    private Double priceRangeMin;
    
    /**
     * The price range maximum
     */
    @Schema(description = "Price range maximum", example = "1000")
    @Min(value = 0, message = "Price range maximum must be non-negative")
    private Double priceRangeMax;
    
    /**
     * Default constructor
     */
    public ProductRecommendationRequest() {
    }
    
    /**
     * Constructor with essential customer fields
     *
     * @param customerId The customer's ID
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param email The customer's email
     */
    public ProductRecommendationRequest(Long customerId, String firstName, String lastName, String email) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    /**
     * Constructor with current product context
     *
     * @param customerId The customer's ID
     * @param currentProductId The current product ID
     * @param recommendationType The recommendation type
     * @param maxRecommendations The maximum number of recommendations
     */
    public ProductRecommendationRequest(Long customerId, String currentProductId, 
                                       String recommendationType, int maxRecommendations) {
        this.customerId = customerId;
        this.currentProductId = currentProductId;
        this.recommendationType = recommendationType;
        this.maxRecommendations = maxRecommendations;
    }
    
    // Getters and setters
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public Set<String> getPreferredCategories() {
        return preferredCategories;
    }
    
    public void setPreferredCategories(Set<String> preferredCategories) {
        this.preferredCategories = preferredCategories != null ? preferredCategories : new HashSet<>();
    }
    
    public Set<String> getPreferredBrands() {
        return preferredBrands;
    }
    
    public void setPreferredBrands(Set<String> preferredBrands) {
        this.preferredBrands = preferredBrands != null ? preferredBrands : new HashSet<>();
    }
    
    public Set<String> getRecentlyViewedProducts() {
        return recentlyViewedProducts;
    }
    
    public void setRecentlyViewedProducts(Set<String> recentlyViewedProducts) {
        this.recentlyViewedProducts = recentlyViewedProducts != null ? recentlyViewedProducts : new HashSet<>();
    }
    
    public Set<String> getRecentlyPurchasedProducts() {
        return recentlyPurchasedProducts;
    }
    
    public void setRecentlyPurchasedProducts(Set<String> recentlyPurchasedProducts) {
        this.recentlyPurchasedProducts = recentlyPurchasedProducts != null ? recentlyPurchasedProducts : new HashSet<>();
    }
    
    public Set<String> getWishListItems() {
        return wishListItems;
    }
    
    public void setWishListItems(Set<String> wishListItems) {
        this.wishListItems = wishListItems != null ? wishListItems : new HashSet<>();
    }
    
    public Set<String> getAbandonedCartItems() {
        return abandonedCartItems;
    }
    
    public void setAbandonedCartItems(Set<String> abandonedCartItems) {
        this.abandonedCartItems = abandonedCartItems != null ? abandonedCartItems : new HashSet<>();
    }
    
    public String getCurrentProductId() {
        return currentProductId;
    }
    
    public void setCurrentProductId(String currentProductId) {
        this.currentProductId = currentProductId;
    }
    
    public String getCurrentCategory() {
        return currentCategory;
    }
    
    public void setCurrentCategory(String currentCategory) {
        this.currentCategory = currentCategory;
    }
    
    public String getCurrentBrand() {
        return currentBrand;
    }
    
    public void setCurrentBrand(String currentBrand) {
        this.currentBrand = currentBrand;
    }
    
    public Set<String> getCurrentCartItems() {
        return currentCartItems;
    }
    
    public void setCurrentCartItems(Set<String> currentCartItems) {
        this.currentCartItems = currentCartItems != null ? currentCartItems : new HashSet<>();
    }
    
    public String getCurrentSeason() {
        return currentSeason;
    }
    
    public void setCurrentSeason(String currentSeason) {
        this.currentSeason = currentSeason;
    }
    
    public String getShoppingChannel() {
        return shoppingChannel;
    }
    
    public void setShoppingChannel(String shoppingChannel) {
        this.shoppingChannel = shoppingChannel;
    }
    
    public String getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    
    public int getMaxRecommendations() {
        return maxRecommendations;
    }
    
    public void setMaxRecommendations(int maxRecommendations) {
        this.maxRecommendations = maxRecommendations;
    }
    
    public boolean isIncludeOutOfStock() {
        return includeOutOfStock;
    }
    
    public void setIncludeOutOfStock(boolean includeOutOfStock) {
        this.includeOutOfStock = includeOutOfStock;
    }
    
    public boolean isIncludePreviouslyPurchased() {
        return includePreviouslyPurchased;
    }
    
    public void setIncludePreviouslyPurchased(boolean includePreviouslyPurchased) {
        this.includePreviouslyPurchased = includePreviouslyPurchased;
    }
    
    public String getRecommendationType() {
        return recommendationType;
    }
    
    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }
    
    public Double getPriceRangeMin() {
        return priceRangeMin;
    }
    
    public void setPriceRangeMin(Double priceRangeMin) {
        this.priceRangeMin = priceRangeMin;
    }
    
    public Double getPriceRangeMax() {
        return priceRangeMax;
    }
    
    public void setPriceRangeMax(Double priceRangeMax) {
        this.priceRangeMax = priceRangeMax;
    }
    
    /**
     * Adds a preferred category to the request.
     * 
     * @param category The category to add
     */
    public void addPreferredCategory(String category) {
        if (this.preferredCategories == null) {
            this.preferredCategories = new HashSet<>();
        }
        this.preferredCategories.add(category);
    }
    
    /**
     * Adds a preferred brand to the request.
     * 
     * @param brand The brand to add
     */
    public void addPreferredBrand(String brand) {
        if (this.preferredBrands == null) {
            this.preferredBrands = new HashSet<>();
        }
        this.preferredBrands.add(brand);
    }
    
    /**
     * Adds a recently viewed product to the request.
     * 
     * @param productId The product ID to add
     */
    public void addRecentlyViewedProduct(String productId) {
        if (this.recentlyViewedProducts == null) {
            this.recentlyViewedProducts = new HashSet<>();
        }
        this.recentlyViewedProducts.add(productId);
    }
    
    /**
     * Adds a recently purchased product to the request.
     * 
     * @param productId The product ID to add
     */
    public void addRecentlyPurchasedProduct(String productId) {
        if (this.recentlyPurchasedProducts == null) {
            this.recentlyPurchasedProducts = new HashSet<>();
        }
        this.recentlyPurchasedProducts.add(productId);
    }
    
    /**
     * Adds a wish list item to the request.
     * 
     * @param productId The product ID to add
     */
    public void addWishListItem(String productId) {
        if (this.wishListItems == null) {
            this.wishListItems = new HashSet<>();
        }
        this.wishListItems.add(productId);
    }
    
    /**
     * Adds an abandoned cart item to the request.
     * 
     * @param productId The product ID to add
     */
    public void addAbandonedCartItem(String productId) {
        if (this.abandonedCartItems == null) {
            this.abandonedCartItems = new HashSet<>();
        }
        this.abandonedCartItems.add(productId);
    }
    
    /**
     * Adds a current cart item to the request.
     * 
     * @param productId The product ID to add
     */
    public void addCurrentCartItem(String productId) {
        if (this.currentCartItems == null) {
            this.currentCartItems = new HashSet<>();
        }
        this.currentCartItems.add(productId);
    }
    
    /**
     * Gets the full name of the customer.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "ProductRecommendationRequest{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", currentProductId='" + currentProductId + '\'' +
                ", recommendationType='" + recommendationType + '\'' +
                ", maxRecommendations=" + maxRecommendations +
                '}';
    }
}