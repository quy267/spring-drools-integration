package com.example.springdroolsintegration.model.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer entity for the discount rules use case.
 * This entity represents a customer in the system and contains all relevant
 * information needed for applying discount rules.
 */
public class Customer {
    
    /**
     * Unique identifier for the customer
     */
    private Long id;
    
    /**
     * Full name of the customer
     */
    @NotBlank(message = "Customer name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    /**
     * Age of the customer in years
     */
    @Min(value = 0, message = "Age must be a positive number")
    private int age;
    
    /**
     * Customer's loyalty tier (e.g., BRONZE, SILVER, GOLD, PLATINUM)
     */
    @NotNull(message = "Loyalty tier is required")
    private String loyaltyTier;
    
    /**
     * Customer's email address
     */
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * Customer's phone number
     */
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    /**
     * Date when the customer was registered
     */
    private LocalDate registrationDate;
    
    /**
     * Total amount spent by the customer
     */
    private double totalSpent;
    
    /**
     * Number of orders placed by the customer
     */
    private int orderCount;
    
    /**
     * Date and time of the customer's last order
     */
    private LocalDateTime lastOrderDate;
    
    /**
     * Whether the customer has opted in for marketing communications
     */
    private boolean marketingOptIn;
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    /**
     * Constructor with essential fields
     *
     * @param name The customer's name
     * @param age The customer's age
     * @param loyaltyTier The customer's loyalty tier
     */
    public Customer(String name, int age, String loyaltyTier) {
        this.name = name;
        this.age = age;
        this.loyaltyTier = loyaltyTier;
        this.registrationDate = LocalDate.now();
    }
    
    /**
     * Full constructor with all fields
     *
     * @param id The customer's ID
     * @param name The customer's name
     * @param age The customer's age
     * @param loyaltyTier The customer's loyalty tier
     * @param email The customer's email
     * @param phoneNumber The customer's phone number
     * @param registrationDate The customer's registration date
     * @param totalSpent The total amount spent by the customer
     * @param orderCount The number of orders placed by the customer
     * @param lastOrderDate The date and time of the customer's last order
     * @param marketingOptIn Whether the customer has opted in for marketing
     */
    public Customer(Long id, String name, int age, String loyaltyTier, String email, 
                   String phoneNumber, LocalDate registrationDate, double totalSpent, 
                   int orderCount, LocalDateTime lastOrderDate, boolean marketingOptIn) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.loyaltyTier = loyaltyTier;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.totalSpent = totalSpent;
        this.orderCount = orderCount;
        this.lastOrderDate = lastOrderDate;
        this.marketingOptIn = marketingOptIn;
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
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getLoyaltyTier() {
        return loyaltyTier;
    }
    
    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
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
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public int getOrderCount() {
        return orderCount;
    }
    
    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
    
    public LocalDateTime getLastOrderDate() {
        return lastOrderDate;
    }
    
    public void setLastOrderDate(LocalDateTime lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
    
    public boolean isMarketingOptIn() {
        return marketingOptIn;
    }
    
    public void setMarketingOptIn(boolean marketingOptIn) {
        this.marketingOptIn = marketingOptIn;
    }
    
    /**
     * Upgrades the customer's loyalty tier based on total spent
     * This is a business method that can be used in rules
     */
    public void upgradeLoyaltyTier() {
        if (totalSpent >= 10000) {
            this.loyaltyTier = "PLATINUM";
        } else if (totalSpent >= 5000) {
            this.loyaltyTier = "GOLD";
        } else if (totalSpent >= 1000) {
            this.loyaltyTier = "SILVER";
        } else {
            this.loyaltyTier = "BRONZE";
        }
    }
    
    /**
     * Checks if the customer is a senior (age 65 or older)
     * This is a business method that can be used in rules
     * 
     * @return true if the customer is a senior, false otherwise
     */
    public boolean isSenior() {
        return age >= 65;
    }
    
    /**
     * Checks if the customer is a new customer (registered within the last 30 days)
     * This is a business method that can be used in rules
     * 
     * @return true if the customer is new, false otherwise
     */
    public boolean isNewCustomer() {
        if (registrationDate == null) {
            return false;
        }
        return registrationDate.isAfter(LocalDate.now().minusDays(30));
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", loyaltyTier='" + loyaltyTier + '\'' +
                ", email='" + email + '\'' +
                ", registrationDate=" + registrationDate +
                ", totalSpent=" + totalSpent +
                ", orderCount=" + orderCount +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}