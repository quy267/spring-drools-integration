package com.example.springdroolsintegration.testdata;

import com.example.springdroolsintegration.model.entity.Customer;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Test data builder for Customer entity following the builder pattern.
 * Provides fluent API for creating Customer instances with default values for testing.
 */
public class CustomerTestDataBuilder {
    
    private Customer customer;
    
    private CustomerTestDataBuilder() {
        this.customer = new Customer();
        // Set default values for testing
        this.customer.setId(1L);
        this.customer.setName("John Doe");
        this.customer.setAge(30);
        this.customer.setLoyaltyTier("BRONZE");
        this.customer.setEmail("john.doe@example.com");
        this.customer.setPhoneNumber("555-0123");
        this.customer.setRegistrationDate(LocalDate.now().minusYears(1));
        this.customer.setTotalSpent(500.0);
        this.customer.setOrderCount(5);
        this.customer.setLastOrderDate(LocalDateTime.now().minusDays(30));
        this.customer.setMarketingOptIn(true);
    }
    
    /**
     * Creates a new CustomerTestDataBuilder instance with default values.
     * @return new CustomerTestDataBuilder instance
     */
    public static CustomerTestDataBuilder aCustomer() {
        return new CustomerTestDataBuilder();
    }
    
    /**
     * Creates a CustomerTestDataBuilder for a child customer (age < 18).
     * @return CustomerTestDataBuilder with child-specific defaults
     */
    public static CustomerTestDataBuilder aChildCustomer() {
        return new CustomerTestDataBuilder()
                .withAge(16)
                .withName("Jane Child")
                .withEmail("jane.child@example.com")
                .withTotalSpent(100.0)
                .withOrderCount(2);
    }
    
    /**
     * Creates a CustomerTestDataBuilder for an adult customer (age 18-64).
     * @return CustomerTestDataBuilder with adult-specific defaults
     */
    public static CustomerTestDataBuilder anAdultCustomer() {
        return new CustomerTestDataBuilder()
                .withAge(35)
                .withName("Mike Adult")
                .withEmail("mike.adult@example.com")
                .withLoyaltyTier("SILVER")
                .withTotalSpent(1500.0)
                .withOrderCount(15);
    }
    
    /**
     * Creates a CustomerTestDataBuilder for a senior customer (age >= 65).
     * @return CustomerTestDataBuilder with senior-specific defaults
     */
    public static CustomerTestDataBuilder aSeniorCustomer() {
        return new CustomerTestDataBuilder()
                .withAge(70)
                .withName("Robert Senior")
                .withEmail("robert.senior@example.com")
                .withLoyaltyTier("GOLD")
                .withTotalSpent(3000.0)
                .withOrderCount(30);
    }
    
    /**
     * Creates a CustomerTestDataBuilder for a premium customer with high spending.
     * @return CustomerTestDataBuilder with premium customer defaults
     */
    public static CustomerTestDataBuilder aPremiumCustomer() {
        return new CustomerTestDataBuilder()
                .withAge(45)
                .withName("Sarah Premium")
                .withEmail("sarah.premium@example.com")
                .withLoyaltyTier("PLATINUM")
                .withTotalSpent(10000.0)
                .withOrderCount(100)
                .withMarketingOptIn(true);
    }
    
    public CustomerTestDataBuilder withId(Long id) {
        this.customer.setId(id);
        return this;
    }
    
    public CustomerTestDataBuilder withName(String name) {
        this.customer.setName(name);
        return this;
    }
    
    public CustomerTestDataBuilder withAge(int age) {
        this.customer.setAge(age);
        return this;
    }
    
    public CustomerTestDataBuilder withLoyaltyTier(String loyaltyTier) {
        this.customer.setLoyaltyTier(loyaltyTier);
        return this;
    }
    
    public CustomerTestDataBuilder withEmail(String email) {
        this.customer.setEmail(email);
        return this;
    }
    
    public CustomerTestDataBuilder withPhoneNumber(String phoneNumber) {
        this.customer.setPhoneNumber(phoneNumber);
        return this;
    }
    
    public CustomerTestDataBuilder withRegistrationDate(LocalDate registrationDate) {
        this.customer.setRegistrationDate(registrationDate);
        return this;
    }
    
    public CustomerTestDataBuilder withTotalSpent(double totalSpent) {
        this.customer.setTotalSpent(totalSpent);
        return this;
    }
    
    public CustomerTestDataBuilder withOrderCount(int orderCount) {
        this.customer.setOrderCount(orderCount);
        return this;
    }
    
    public CustomerTestDataBuilder withLastOrderDate(LocalDateTime lastOrderDate) {
        this.customer.setLastOrderDate(lastOrderDate);
        return this;
    }
    
    public CustomerTestDataBuilder withMarketingOptIn(boolean marketingOptIn) {
        this.customer.setMarketingOptIn(marketingOptIn);
        return this;
    }
    
    /**
     * Sets the customer as a new customer (registered recently with minimal activity).
     * @return CustomerTestDataBuilder configured as new customer
     */
    public CustomerTestDataBuilder asNewCustomer() {
        this.customer.setRegistrationDate(LocalDate.now().minusDays(7));
        this.customer.setTotalSpent(0.0);
        this.customer.setOrderCount(0);
        this.customer.setLastOrderDate(null);
        this.customer.setLoyaltyTier("BRONZE");
        return this;
    }
    
    /**
     * Sets the customer as a loyal customer with high activity.
     * @return CustomerTestDataBuilder configured as loyal customer
     */
    public CustomerTestDataBuilder asLoyalCustomer() {
        this.customer.setRegistrationDate(LocalDate.now().minusYears(3));
        this.customer.setTotalSpent(5000.0);
        this.customer.setOrderCount(50);
        this.customer.setLastOrderDate(LocalDateTime.now().minusDays(5));
        this.customer.setLoyaltyTier("GOLD");
        return this;
    }
    
    /**
     * Builds and returns the Customer instance.
     * @return configured Customer instance
     */
    public Customer build() {
        return this.customer;
    }
}