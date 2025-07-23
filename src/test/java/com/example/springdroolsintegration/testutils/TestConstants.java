package com.example.springdroolsintegration.testutils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Constants used across test classes for consistent test data.
 */
public final class TestConstants {
    
    private TestConstants() {
        // Utility class - prevent instantiation
    }
    
    // Customer Constants
    public static final class Customer {
        public static final String DEFAULT_NAME = "John Doe";
        public static final String DEFAULT_EMAIL = "john.doe@example.com";
        public static final String DEFAULT_PHONE = "555-0123";
        public static final int DEFAULT_AGE = 30;
        public static final String DEFAULT_LOYALTY_TIER = "BRONZE";
        public static final double DEFAULT_TOTAL_SPENT = 500.0;
        public static final int DEFAULT_ORDER_COUNT = 5;
        
        // Age boundaries for testing
        public static final int CHILD_MAX_AGE = 17;
        public static final int ADULT_MIN_AGE = 18;
        public static final int ADULT_MAX_AGE = 64;
        public static final int SENIOR_MIN_AGE = 65;
        
        // Loyalty tiers
        public static final List<String> LOYALTY_TIERS = List.of("BRONZE", "SILVER", "GOLD", "PLATINUM");
    }
    
    // Product Constants
    public static final class Product {
        public static final String DEFAULT_SKU = "PROD-001";
        public static final String DEFAULT_NAME = "Test Product";
        public static final String DEFAULT_CATEGORY = "Electronics";
        public static final String DEFAULT_BRAND = "TestBrand";
        public static final double DEFAULT_PRICE = 299.99;
        public static final double DEFAULT_COST = 150.00;
        public static final int DEFAULT_INVENTORY = 100;
        
        // Categories
        public static final List<String> CATEGORIES = List.of(
            "Electronics", "Clothing", "Home & Garden", "Sports", "Books", "Toys"
        );
        
        // Product tags
        public static final Set<String> DEFAULT_TAGS = Set.of("electronics", "smartphone", "mobile");
    }
    
    // Order Constants
    public static final class Order {
        public static final double SMALL_ORDER_THRESHOLD = 100.0;
        public static final double LARGE_ORDER_THRESHOLD = 500.0;
        public static final double FREE_SHIPPING_THRESHOLD = 100.0;
        public static final int BULK_ORDER_VOLUME = 10;
        
        public static final List<String> ORDER_STATUSES = List.of(
            "PENDING", "PROCESSING", "SHIPPED", "COMPLETED", "CANCELLED"
        );
        
        public static final List<String> PAYMENT_METHODS = List.of(
            "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER"
        );
    }
    
    // Loan Application Constants
    public static final class LoanApplication {
        public static final List<String> LOAN_TYPES = List.of(
            "MORTGAGE", "AUTO", "PERSONAL", "BUSINESS", "STUDENT"
        );
        
        public static final List<String> LOAN_PURPOSES = List.of(
            "HOME_PURCHASE", "REFINANCE", "VEHICLE_PURCHASE", "DEBT_CONSOLIDATION", "BUSINESS_EXPANSION"
        );
        
        // Credit score ranges
        public static final int POOR_CREDIT_MAX = 579;
        public static final int FAIR_CREDIT_MIN = 580;
        public static final int FAIR_CREDIT_MAX = 669;
        public static final int GOOD_CREDIT_MIN = 670;
        public static final int GOOD_CREDIT_MAX = 739;
        public static final int EXCELLENT_CREDIT_MIN = 740;
        
        // Default loan values
        public static final double DEFAULT_LOAN_AMOUNT = 250000.0;
        public static final int DEFAULT_LOAN_TERM_MONTHS = 360;
        public static final double DEFAULT_INTEREST_RATE = 4.5;
    }
    
    // Applicant Constants
    public static final class Applicant {
        public static final String DEFAULT_FIRST_NAME = "John";
        public static final String DEFAULT_LAST_NAME = "Smith";
        public static final String DEFAULT_SSN = "123-45-6789";
        public static final String DEFAULT_EMAIL = "john.smith@example.com";
        public static final double DEFAULT_ANNUAL_INCOME = 75000.0;
        public static final int DEFAULT_CREDIT_SCORE = 720;
        
        public static final List<String> EMPLOYMENT_STATUSES = List.of(
            "EMPLOYED", "UNEMPLOYED", "SELF_EMPLOYED", "RETIRED", "STUDENT"
        );
    }
    
    // Date Constants
    public static final class Dates {
        public static final LocalDate DEFAULT_REGISTRATION_DATE = LocalDate.now().minusYears(1);
        public static final LocalDateTime DEFAULT_ORDER_DATE = LocalDateTime.now();
        public static final LocalDate DEFAULT_APPLICATION_DATE = LocalDate.now();
        public static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1985, 6, 15);
    }
    
    // Discount Constants
    public static final class Discounts {
        public static final double CHILD_DISCOUNT_PERCENTAGE = 15.0;
        public static final double SENIOR_DISCOUNT_PERCENTAGE = 10.0;
        public static final double BULK_DISCOUNT_PERCENTAGE = 10.0;
        public static final double LOYALTY_BRONZE_DISCOUNT = 5.0;
        public static final double LOYALTY_SILVER_DISCOUNT = 10.0;
        public static final double LOYALTY_GOLD_DISCOUNT = 15.0;
        public static final double LOYALTY_PLATINUM_DISCOUNT = 20.0;
    }
    
    // Test Data Ranges
    public static final class Ranges {
        public static final int MIN_AGE = 1;
        public static final int MAX_AGE = 120;
        public static final double MIN_PRICE = 0.01;
        public static final double MAX_PRICE = 10000.0;
        public static final int MIN_QUANTITY = 1;
        public static final int MAX_QUANTITY = 100;
        public static final double MIN_INCOME = 0.0;
        public static final double MAX_INCOME = 1000000.0;
    }
}