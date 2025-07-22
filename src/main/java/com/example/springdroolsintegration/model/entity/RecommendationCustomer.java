package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Customer entity for the product recommendation rules use case.
 * This entity represents a customer in the system and contains all relevant
 * information needed for product recommendations.
 * 
 * Note: This is separate from the Customer entity used in the discount rules use case
 * to avoid conflicts and allow for different attributes and behaviors.
 */
public class RecommendationCustomer {
    
    /**
     * Unique identifier for the customer
     */
    private Long id;
    
    /**
     * Customer's unique account number
     */
    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[A-Z0-9]{6,10}$", message = "Account number must be 6-10 alphanumeric characters")
    private String accountNumber;
    
    /**
     * First name of the customer
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    /**
     * Last name of the customer
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    /**
     * Email address of the customer
     */
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * Phone number of the customer
     */
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    /**
     * Date of birth of the customer
     */
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    /**
     * Gender of the customer (M, F, O for other, or null if not specified)
     */
    @Pattern(regexp = "^[MFO]$", message = "Gender must be M, F, or O")
    private String gender;
    
    /**
     * Customer's postal/zip code
     */
    @Size(max = 10, message = "Zip code cannot exceed 10 characters")
    private String zipCode;
    
    /**
     * Customer's city of residence
     */
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;
    
    /**
     * Customer's state/province of residence
     */
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;
    
    /**
     * Customer's country of residence
     */
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;
    
    /**
     * Date when the customer was registered
     */
    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;
    
    /**
     * Date and time of the customer's last login
     */
    private LocalDateTime lastLoginDate;
    
    /**
     * Date and time of the customer's last purchase
     */
    private LocalDateTime lastPurchaseDate;
    
    /**
     * Total number of orders placed by the customer
     */
    @Min(value = 0, message = "Order count must be non-negative")
    private int orderCount;
    
    /**
     * Total amount spent by the customer
     */
    @Min(value = 0, message = "Total spent must be non-negative")
    private double totalSpent;
    
    /**
     * Average order value
     */
    @Min(value = 0, message = "Average order value must be non-negative")
    private double averageOrderValue;
    
    /**
     * Customer's loyalty tier (e.g., BRONZE, SILVER, GOLD, PLATINUM)
     */
    private String loyaltyTier;
    
    /**
     * Loyalty points accumulated by the customer
     */
    @Min(value = 0, message = "Loyalty points must be non-negative")
    private int loyaltyPoints;
    
    /**
     * Whether the customer has opted in for marketing communications
     */
    private boolean marketingOptIn;
    
    /**
     * Whether the customer has a mobile app installed
     */
    private boolean hasAppInstalled;
    
    /**
     * Customer's preferred shopping categories
     */
    private Set<String> preferredCategories = new HashSet<>();
    
    /**
     * Customer's preferred brands
     */
    private Set<String> preferredBrands = new HashSet<>();
    
    /**
     * Customer's preferred shopping channels (e.g., ONLINE, IN_STORE, MOBILE_APP)
     */
    private Set<String> preferredChannels = new HashSet<>();
    
    /**
     * Customer's preferred payment methods
     */
    private Set<String> preferredPaymentMethods = new HashSet<>();
    
    /**
     * Customer's preferred delivery methods
     */
    private Set<String> preferredDeliveryMethods = new HashSet<>();
    
    /**
     * Customer's browsing history categories
     */
    private Set<String> browsingHistory = new HashSet<>();
    
    /**
     * Customer's abandoned cart items (product IDs)
     */
    private Set<String> abandonedCartItems = new HashSet<>();
    
    /**
     * Customer's wish list items (product IDs)
     */
    private Set<String> wishListItems = new HashSet<>();
    
    /**
     * Customer's recently viewed products (product IDs)
     */
    private Set<String> recentlyViewedProducts = new HashSet<>();
    
    /**
     * Customer's recently purchased products (product IDs)
     */
    private Set<String> recentlyPurchasedProducts = new HashSet<>();
    
    /**
     * Customer's satisfaction score (1-5)
     */
    @Min(value = 0, message = "Satisfaction score must be non-negative")
    private int satisfactionScore;
    
    /**
     * Customer's Net Promoter Score (-100 to 100)
     */
    private int netPromoterScore;
    
    /**
     * Whether the customer has returned products
     */
    private boolean hasReturns;
    
    /**
     * Number of returns made by the customer
     */
    @Min(value = 0, message = "Return count must be non-negative")
    private int returnCount;
    
    /**
     * Default constructor
     */
    public RecommendationCustomer() {
        this.registrationDate = LocalDate.now();
    }
    
    /**
     * Constructor with essential fields
     *
     * @param accountNumber The customer's account number
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param email The customer's email
     */
    public RecommendationCustomer(String accountNumber, String firstName, String lastName, String email) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = LocalDate.now();
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The customer's ID
     * @param accountNumber The customer's account number
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param email The customer's email
     * @param phoneNumber The customer's phone number
     * @param dateOfBirth The customer's date of birth
     * @param gender The customer's gender
     * @param zipCode The customer's zip code
     * @param city The customer's city
     * @param state The customer's state
     * @param country The customer's country
     * @param registrationDate The customer's registration date
     * @param lastLoginDate The customer's last login date
     * @param lastPurchaseDate The customer's last purchase date
     * @param orderCount The customer's order count
     * @param totalSpent The customer's total spent
     * @param averageOrderValue The customer's average order value
     * @param loyaltyTier The customer's loyalty tier
     * @param loyaltyPoints The customer's loyalty points
     * @param marketingOptIn Whether the customer has opted in for marketing
     * @param hasAppInstalled Whether the customer has the app installed
     * @param preferredCategories The customer's preferred categories
     * @param preferredBrands The customer's preferred brands
     * @param preferredChannels The customer's preferred channels
     * @param preferredPaymentMethods The customer's preferred payment methods
     * @param preferredDeliveryMethods The customer's preferred delivery methods
     * @param browsingHistory The customer's browsing history
     * @param abandonedCartItems The customer's abandoned cart items
     * @param wishListItems The customer's wish list items
     * @param recentlyViewedProducts The customer's recently viewed products
     * @param recentlyPurchasedProducts The customer's recently purchased products
     * @param satisfactionScore The customer's satisfaction score
     * @param netPromoterScore The customer's net promoter score
     * @param hasReturns Whether the customer has returns
     * @param returnCount The customer's return count
     */
    public RecommendationCustomer(Long id, String accountNumber, String firstName, String lastName, 
                                 String email, String phoneNumber, LocalDate dateOfBirth, String gender, 
                                 String zipCode, String city, String state, String country, 
                                 LocalDate registrationDate, LocalDateTime lastLoginDate, 
                                 LocalDateTime lastPurchaseDate, int orderCount, double totalSpent, 
                                 double averageOrderValue, String loyaltyTier, int loyaltyPoints, 
                                 boolean marketingOptIn, boolean hasAppInstalled, 
                                 Set<String> preferredCategories, Set<String> preferredBrands, 
                                 Set<String> preferredChannels, Set<String> preferredPaymentMethods, 
                                 Set<String> preferredDeliveryMethods, Set<String> browsingHistory, 
                                 Set<String> abandonedCartItems, Set<String> wishListItems, 
                                 Set<String> recentlyViewedProducts, Set<String> recentlyPurchasedProducts, 
                                 int satisfactionScore, int netPromoterScore, 
                                 boolean hasReturns, int returnCount) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
        this.lastPurchaseDate = lastPurchaseDate;
        this.orderCount = orderCount;
        this.totalSpent = totalSpent;
        this.averageOrderValue = averageOrderValue;
        this.loyaltyTier = loyaltyTier;
        this.loyaltyPoints = loyaltyPoints;
        this.marketingOptIn = marketingOptIn;
        this.hasAppInstalled = hasAppInstalled;
        this.preferredCategories = preferredCategories != null ? preferredCategories : new HashSet<>();
        this.preferredBrands = preferredBrands != null ? preferredBrands : new HashSet<>();
        this.preferredChannels = preferredChannels != null ? preferredChannels : new HashSet<>();
        this.preferredPaymentMethods = preferredPaymentMethods != null ? preferredPaymentMethods : new HashSet<>();
        this.preferredDeliveryMethods = preferredDeliveryMethods != null ? preferredDeliveryMethods : new HashSet<>();
        this.browsingHistory = browsingHistory != null ? browsingHistory : new HashSet<>();
        this.abandonedCartItems = abandonedCartItems != null ? abandonedCartItems : new HashSet<>();
        this.wishListItems = wishListItems != null ? wishListItems : new HashSet<>();
        this.recentlyViewedProducts = recentlyViewedProducts != null ? recentlyViewedProducts : new HashSet<>();
        this.recentlyPurchasedProducts = recentlyPurchasedProducts != null ? recentlyPurchasedProducts : new HashSet<>();
        this.satisfactionScore = satisfactionScore;
        this.netPromoterScore = netPromoterScore;
        this.hasReturns = hasReturns;
        this.returnCount = returnCount;
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
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
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }
    
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
    
    public LocalDateTime getLastPurchaseDate() {
        return lastPurchaseDate;
    }
    
    public void setLastPurchaseDate(LocalDateTime lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }
    
    public int getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
        updateAverageOrderValue();
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
        updateAverageOrderValue();
    }
    
    public double getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }
    
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
    
    public boolean isMarketingOptIn() {
        return marketingOptIn;
    }
    
    public void setMarketingOptIn(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }
    
    public boolean isHasAppInstalled() {
        return hasAppInstalled;
    }
    
    public void setHasAppInstalled(boolean hasAppInstalled) {
        this.hasAppInstalled = hasAppInstalled;
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
    
    public Set<String> getPreferredChannels() {
        return preferredChannels;
    }
    
    public void setPreferredChannels(Set<String> preferredChannels) {
        this.preferredChannels = preferredChannels != null ? preferredChannels : new HashSet<>();
    }
    
    public Set<String> getPreferredPaymentMethods() {
        return preferredPaymentMethods;
    }
    
    public void setPreferredPaymentMethods(Set<String> preferredPaymentMethods) {
        this.preferredPaymentMethods = preferredPaymentMethods != null ? preferredPaymentMethods : new HashSet<>();
    }
    
    public Set<String> getPreferredDeliveryMethods() {
        return preferredDeliveryMethods;
    }
    
    public void setPreferredDeliveryMethods(Set<String> preferredDeliveryMethods) {
        this.preferredDeliveryMethods = preferredDeliveryMethods != null ? preferredDeliveryMethods : new HashSet<>();
    }
    
    public Set<String> getBrowsingHistory() {
        return browsingHistory;
    }
    
    public void setBrowsingHistory(Set<String> browsingHistory) {
        this.browsingHistory = browsingHistory != null ? browsingHistory : new HashSet<>();
    }
    
    public Set<String> getAbandonedCartItems() {
        return abandonedCartItems;
    }
    
    public void setAbandonedCartItems(Set<String> abandonedCartItems) {
        this.abandonedCartItems = abandonedCartItems != null ? abandonedCartItems : new HashSet<>();
    }
    
    public Set<String> getWishListItems() {
        return wishListItems;
    }
    
    public void setWishListItems(Set<String> wishListItems) {
        this.wishListItems = wishListItems != null ? wishListItems : new HashSet<>();
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
    
    public int getSatisfactionScore() {
        return satisfactionScore;
    }
    
    public void setSatisfactionScore(int satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }
    
    public int getNetPromoterScore() {
        return netPromoterScore;
    }
    
    public void setNetPromoterScore(int netPromoterScore) {
        this.netPromoterScore = netPromoterScore;
    }
    
    public boolean isHasReturns() {
        return hasReturns;
    }
    
    public void setHasReturns(boolean hasReturns) {
        this.hasReturns = hasReturns;
    }
    
    public int getReturnCount() {
        return returnCount;
    }
    
    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
        this.hasReturns = returnCount > 0;
    }
    
    /**
     * Updates the average order value based on total spent and order count.
     */
    private void updateAverageOrderValue() {
        if (orderCount > 0) {
            this.averageOrderValue = totalSpent / orderCount;
        } else {
            this.averageOrderValue = 0;
        }
    }
    
    /**
     * Adds a preferred category to the customer's preferences.
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
     * Adds a preferred brand to the customer's preferences.
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
     * Adds a product to the customer's recently viewed products.
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
     * Adds a product to the customer's recently purchased products.
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
     * Adds a product to the customer's wish list.
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
     * Adds a product to the customer's abandoned cart.
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
     * Records a new purchase for the customer.
     * 
     * @param amount The purchase amount
     * @param productIds The product IDs purchased
     */
    public void recordPurchase(double amount, Set<String> productIds) {
        this.totalSpent += amount;
        this.orderCount++;
        this.lastPurchaseDate = LocalDateTime.now();
        updateAverageOrderValue();
        
        if (productIds != null && !productIds.isEmpty()) {
            if (this.recentlyPurchasedProducts == null) {
                this.recentlyPurchasedProducts = new HashSet<>();
            }
            this.recentlyPurchasedProducts.addAll(productIds);
            
            // Remove purchased items from abandoned cart and wish list
            if (this.abandonedCartItems != null) {
                this.abandonedCartItems.removeAll(productIds);
            }
            if (this.wishListItems != null) {
                this.wishListItems.removeAll(productIds);
            }
        }
    }
    
    /**
     * Records a product return for the customer.
     * 
     * @param amount The return amount
     * @param productId The product ID returned
     */
    public void recordReturn(double amount, String productId) {
        this.totalSpent -= amount;
        this.returnCount++;
        this.hasReturns = true;
        updateAverageOrderValue();
        
        if (productId != null && !productId.isEmpty() && this.recentlyPurchasedProducts != null) {
            this.recentlyPurchasedProducts.remove(productId);
        }
    }
    
    /**
     * Updates the customer's loyalty tier based on total spent and order count.
     * This is a business method that can be used in rules.
     */
    public void updateLoyaltyTier() {
        if (totalSpent >= 10000 || orderCount >= 50) {
            this.loyaltyTier = "PLATINUM";
        } else if (totalSpent >= 5000 || orderCount >= 25) {
            this.loyaltyTier = "GOLD";
        } else if (totalSpent >= 1000 || orderCount >= 10) {
            this.loyaltyTier = "SILVER";
        } else {
            this.loyaltyTier = "BRONZE";
        }
    }
    
    /**
     * Checks if the customer is a new customer (registered within the last 30 days).
     * 
     * @return true if the customer is new, false otherwise
     */
    public boolean isNewCustomer() {
        if (registrationDate == null) {
            return false;
        }
        return registrationDate.isAfter(LocalDate.now().minusDays(30));
    }
    
    /**
     * Checks if the customer is a loyal customer (has been registered for more than 1 year).
     * 
     * @return true if the customer is loyal, false otherwise
     */
    public boolean isLoyalCustomer() {
        if (registrationDate == null) {
            return false;
        }
        return registrationDate.isBefore(LocalDate.now().minusYears(1));
    }
    
    /**
     * Checks if the customer is a high-value customer (average order value > $100).
     * 
     * @return true if the customer is high-value, false otherwise
     */
    public boolean isHighValueCustomer() {
        return averageOrderValue > 100;
    }
    
    /**
     * Checks if the customer is a frequent shopper (more than 12 orders per year).
     * 
     * @return true if the customer is a frequent shopper, false otherwise
     */
    public boolean isFrequentShopper() {
        if (registrationDate == null) {
            return false;
        }
        
        long daysRegistered = registrationDate.until(LocalDate.now()).getDays();
        if (daysRegistered <= 0) {
            return false;
        }
        
        // Calculate orders per year
        double ordersPerYear = (orderCount * 365.0) / daysRegistered;
        return ordersPerYear > 12;
    }
    
    /**
     * Checks if the customer has been inactive (no purchases in the last 90 days).
     * 
     * @return true if the customer is inactive, false otherwise
     */
    public boolean isInactive() {
        if (lastPurchaseDate == null) {
            return registrationDate.isBefore(LocalDate.now().minusDays(90));
        }
        return lastPurchaseDate.isBefore(LocalDateTime.now().minusDays(90));
    }
    
    /**
     * Gets the full name of the customer.
     * 
     * @return The full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Calculates the age of the customer based on the date of birth.
     * 
     * @return The age in years, or 0 if date of birth is not set
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
    
    @Override
    public String toString() {
        return "RecommendationCustomer{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", loyaltyTier='" + loyaltyTier + '\'' +
                ", orderCount=" + orderCount +
                ", totalSpent=" + totalSpent +
                ", averageOrderValue=" + averageOrderValue +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecommendationCustomer that = (RecommendationCustomer) o;
        return Objects.equals(id, that.id) || 
               Objects.equals(accountNumber, that.accountNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber);
    }
}