package com.example.springdroolsintegration.testutils;

import com.example.springdroolsintegration.model.entity.Applicant;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.LoanApplication;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.entity.Product;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Custom assertion helpers for domain-specific objects and common test scenarios.
 * Uses AssertJ for fluent assertions with domain-specific validation logic.
 */
public final class TestAssertions {
    
    private TestAssertions() {
        // Utility class - prevent instantiation
    }
    
    // Customer Assertions
    
    /**
     * Asserts that a customer has valid basic properties.
     * @param customer the customer to validate
     */
    public static void assertValidCustomer(Customer customer) {
        assertThat(customer).isNotNull();
        assertThat(customer.getName()).isNotBlank();
        assertThat(customer.getAge()).isBetween(TestConstants.Ranges.MIN_AGE, TestConstants.Ranges.MAX_AGE);
        assertThat(customer.getEmail()).isNotBlank().contains("@");
        assertThat(customer.getLoyaltyTier()).isIn(TestConstants.Customer.LOYALTY_TIERS);
        assertThat(customer.getTotalSpent()).isNotNegative();
        assertThat(customer.getOrderCount()).isNotNegative();
    }
    
    /**
     * Asserts that a customer is a child (age < 18).
     * @param customer the customer to validate
     */
    public static void assertChildCustomer(Customer customer) {
        assertValidCustomer(customer);
        assertThat(customer.getAge()).isLessThan(TestConstants.Customer.ADULT_MIN_AGE);
    }
    
    /**
     * Asserts that a customer is an adult (age 18-64).
     * @param customer the customer to validate
     */
    public static void assertAdultCustomer(Customer customer) {
        assertValidCustomer(customer);
        assertThat(customer.getAge())
            .isBetween(TestConstants.Customer.ADULT_MIN_AGE, TestConstants.Customer.ADULT_MAX_AGE);
    }
    
    /**
     * Asserts that a customer is a senior (age >= 65).
     * @param customer the customer to validate
     */
    public static void assertSeniorCustomer(Customer customer) {
        assertValidCustomer(customer);
        assertThat(customer.getAge()).isGreaterThanOrEqualTo(TestConstants.Customer.SENIOR_MIN_AGE);
    }
    
    /**
     * Asserts that a customer has the expected loyalty tier.
     * @param customer the customer to validate
     * @param expectedTier the expected loyalty tier
     */
    public static void assertCustomerLoyaltyTier(Customer customer, String expectedTier) {
        assertValidCustomer(customer);
        assertThat(customer.getLoyaltyTier()).isEqualTo(expectedTier);
    }
    
    // Product Assertions
    
    /**
     * Asserts that a product has valid basic properties.
     * @param product the product to validate
     */
    public static void assertValidProduct(Product product) {
        assertThat(product).isNotNull();
        assertThat(product.getSku()).isNotBlank();
        assertThat(product.getName()).isNotBlank();
        assertThat(product.getCategory()).isNotBlank();
        assertThat(product.getPrice()).isPositive();
        assertThat(product.getCost()).isNotNegative();
        assertThat(product.getInventoryLevel()).isNotNegative();
    }
    
    /**
     * Asserts that a product is in stock.
     * @param product the product to validate
     */
    public static void assertProductInStock(Product product) {
        assertValidProduct(product);
        assertThat(product.isInStock()).isTrue();
        assertThat(product.getInventoryLevel()).isPositive();
    }
    
    /**
     * Asserts that a product is out of stock.
     * @param product the product to validate
     */
    public static void assertProductOutOfStock(Product product) {
        assertValidProduct(product);
        assertThat(product.isInStock()).isFalse();
        assertThat(product.getInventoryLevel()).isZero();
    }
    
    /**
     * Asserts that a product is on sale.
     * @param product the product to validate
     */
    public static void assertProductOnSale(Product product) {
        assertValidProduct(product);
        assertThat(product.isOnSale()).isTrue();
        assertThat(product.getSalePrice()).isNotNull().isLessThan(product.getPrice());
    }
    
    /**
     * Asserts that a product has a high rating (>= 4.0).
     * @param product the product to validate
     */
    public static void assertHighRatedProduct(Product product) {
        assertValidProduct(product);
        assertThat(product.getAverageRating()).isGreaterThanOrEqualTo(4.0);
        assertThat(product.getRatingCount()).isPositive();
    }
    
    // Order Assertions
    
    /**
     * Asserts that an order has valid basic properties.
     * @param order the order to validate
     */
    public static void assertValidOrder(Order order) {
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNotBlank();
        assertThat(order.getCustomer()).isNotNull();
        assertThat(order.getAmount()).isPositive();
        assertThat(order.getVolume()).isPositive();
        assertThat(order.getStatus()).isIn(TestConstants.Order.ORDER_STATUSES);
        assertThat(order.getPaymentMethod()).isIn(TestConstants.Order.PAYMENT_METHODS);
        assertThat(order.getFinalAmount()).isNotNegative();
    }
    
    /**
     * Asserts that an order is a small order (< $100).
     * @param order the order to validate
     */
    public static void assertSmallOrder(Order order) {
        assertValidOrder(order);
        assertThat(order.getAmount()).isLessThan(TestConstants.Order.SMALL_ORDER_THRESHOLD);
    }
    
    /**
     * Asserts that an order is a large order (> $500).
     * @param order the order to validate
     */
    public static void assertLargeOrder(Order order) {
        assertValidOrder(order);
        assertThat(order.getAmount()).isGreaterThan(TestConstants.Order.LARGE_ORDER_THRESHOLD);
    }
    
    /**
     * Asserts that an order is a bulk order (high volume).
     * @param order the order to validate
     */
    public static void assertBulkOrder(Order order) {
        assertValidOrder(order);
        assertThat(order.getVolume()).isGreaterThanOrEqualTo(TestConstants.Order.BULK_ORDER_VOLUME);
    }
    
    /**
     * Asserts that an order has a discount applied.
     * @param order the order to validate
     */
    public static void assertOrderWithDiscount(Order order) {
        assertValidOrder(order);
        assertThat(order.getDiscountPercentage()).isPositive();
        assertThat(order.getFinalAmount()).isLessThan(order.getAmount());
    }
    
    /**
     * Asserts that an order is eligible for free shipping.
     * @param order the order to validate
     */
    public static void assertOrderEligibleForFreeShipping(Order order) {
        assertValidOrder(order);
        assertThat(order.getAmount()).isGreaterThanOrEqualTo(TestConstants.Order.FREE_SHIPPING_THRESHOLD);
    }
    
    // Loan Application Assertions
    
    /**
     * Asserts that a loan application has valid basic properties.
     * @param loanApplication the loan application to validate
     */
    public static void assertValidLoanApplication(LoanApplication loanApplication) {
        assertThat(loanApplication).isNotNull();
        assertThat(loanApplication.getApplicationNumber()).isNotBlank();
        assertThat(loanApplication.getApplicant()).isNotNull();
        assertThat(loanApplication.getLoanType()).isIn(TestConstants.LoanApplication.LOAN_TYPES);
        assertThat(loanApplication.getLoanPurpose()).isIn(TestConstants.LoanApplication.LOAN_PURPOSES);
        assertThat(loanApplication.getLoanAmount()).isPositive();
        assertThat(loanApplication.getLoanTermMonths()).isPositive();
        assertThat(loanApplication.getInterestRate()).isPositive();
        assertThat(loanApplication.getApplicationDate()).isNotNull();
    }
    
    /**
     * Asserts that a loan application is approved.
     * @param loanApplication the loan application to validate
     */
    public static void assertLoanApplicationApproved(LoanApplication loanApplication) {
        assertValidLoanApplication(loanApplication);
        assertThat(loanApplication.isApproved()).isTrue();
        assertThat(loanApplication.getStatus()).isEqualTo("APPROVED");
        assertThat(loanApplication.getDecisionDate()).isNotNull();
        assertThat(loanApplication.getDecisionReason()).isNotBlank();
    }
    
    /**
     * Asserts that a loan application is rejected.
     * @param loanApplication the loan application to validate
     */
    public static void assertLoanApplicationRejected(LoanApplication loanApplication) {
        assertValidLoanApplication(loanApplication);
        assertThat(loanApplication.isApproved()).isFalse();
        assertThat(loanApplication.getStatus()).isEqualTo("REJECTED");
        assertThat(loanApplication.getDecisionDate()).isNotNull();
        assertThat(loanApplication.getDecisionReason()).isNotBlank();
    }
    
    /**
     * Asserts that a loan application is for a mortgage.
     * @param loanApplication the loan application to validate
     */
    public static void assertMortgageLoan(LoanApplication loanApplication) {
        assertValidLoanApplication(loanApplication);
        assertThat(loanApplication.getLoanType()).isEqualTo("MORTGAGE");
        assertThat(loanApplication.getPropertyAddress()).isNotBlank();
        assertThat(loanApplication.getPropertyValue()).isPositive();
    }
    
    // Applicant Assertions
    
    /**
     * Asserts that an applicant has valid basic properties.
     * @param applicant the applicant to validate
     */
    public static void assertValidApplicant(Applicant applicant) {
        assertThat(applicant).isNotNull();
        assertThat(applicant.getFirstName()).isNotBlank();
        assertThat(applicant.getLastName()).isNotBlank();
        assertThat(applicant.getDateOfBirth()).isNotNull().isBefore(LocalDate.now());
        assertThat(applicant.getSsn()).isNotBlank().matches("\\d{3}-\\d{2}-\\d{4}");
        assertThat(applicant.getEmail()).isNotBlank().contains("@");
        assertThat(applicant.getCreditScore()).isBetween(300, 850);
        assertThat(applicant.getEmploymentStatus()).isIn(TestConstants.Applicant.EMPLOYMENT_STATUSES);
    }
    
    /**
     * Asserts that an applicant has excellent credit (740+).
     * @param applicant the applicant to validate
     */
    public static void assertExcellentCreditApplicant(Applicant applicant) {
        assertValidApplicant(applicant);
        assertThat(applicant.getCreditScore()).isGreaterThanOrEqualTo(TestConstants.LoanApplication.EXCELLENT_CREDIT_MIN);
    }
    
    /**
     * Asserts that an applicant has good credit (670-739).
     * @param applicant the applicant to validate
     */
    public static void assertGoodCreditApplicant(Applicant applicant) {
        assertValidApplicant(applicant);
        assertThat(applicant.getCreditScore())
            .isBetween(TestConstants.LoanApplication.GOOD_CREDIT_MIN, TestConstants.LoanApplication.GOOD_CREDIT_MAX);
    }
    
    /**
     * Asserts that an applicant has fair credit (580-669).
     * @param applicant the applicant to validate
     */
    public static void assertFairCreditApplicant(Applicant applicant) {
        assertValidApplicant(applicant);
        assertThat(applicant.getCreditScore())
            .isBetween(TestConstants.LoanApplication.FAIR_CREDIT_MIN, TestConstants.LoanApplication.FAIR_CREDIT_MAX);
    }
    
    /**
     * Asserts that an applicant has poor credit (<580).
     * @param applicant the applicant to validate
     */
    public static void assertPoorCreditApplicant(Applicant applicant) {
        assertValidApplicant(applicant);
        assertThat(applicant.getCreditScore()).isLessThanOrEqualTo(TestConstants.LoanApplication.POOR_CREDIT_MAX);
    }
    
    // HTTP Response Assertions
    
    /**
     * Asserts that an HTTP response is successful (2xx status).
     * @param result the MvcResult to validate
     */
    public static void assertSuccessfulResponse(MvcResult result) {
        assertThat(result.getResponse().getStatus()).isBetween(200, 299);
    }
    
    /**
     * Asserts that an HTTP response has a specific status code.
     * @param result the MvcResult to validate
     * @param expectedStatus the expected HTTP status code
     */
    public static void assertResponseStatus(MvcResult result, int expectedStatus) {
        assertThat(result.getResponse().getStatus()).isEqualTo(expectedStatus);
    }
    
    /**
     * Asserts that an HTTP response contains JSON content.
     * @param result the MvcResult to validate
     */
    public static void assertJsonResponse(MvcResult result) {
        assertThat(result.getResponse().getContentType()).contains("application/json");
        assertThat(TestUtils.getResponseContent(result)).isNotBlank();
    }
    
    // Collection Assertions
    
    /**
     * Asserts that a list is not null and not empty.
     * @param list the list to validate
     * @param <T> the type parameter
     */
    public static <T> void assertNotEmptyList(List<T> list) {
        assertThat(list).isNotNull().isNotEmpty();
    }
    
    /**
     * Asserts that a list has the expected size.
     * @param list the list to validate
     * @param expectedSize the expected size
     * @param <T> the type parameter
     */
    public static <T> void assertListSize(List<T> list, int expectedSize) {
        assertThat(list).isNotNull().hasSize(expectedSize);
    }
    
    // Date/Time Assertions
    
    /**
     * Asserts that a date is within the last year.
     * @param date the date to validate
     */
    public static void assertDateWithinLastYear(LocalDate date) {
        assertThat(date).isNotNull()
            .isAfter(LocalDate.now().minusYears(1))
            .isBeforeOrEqualTo(LocalDate.now());
    }
    
    /**
     * Asserts that a datetime is within the last month.
     * @param dateTime the datetime to validate
     */
    public static void assertDateTimeWithinLastMonth(LocalDateTime dateTime) {
        assertThat(dateTime).isNotNull()
            .isAfter(LocalDateTime.now().minusMonths(1))
            .isBeforeOrEqualTo(LocalDateTime.now());
    }
    
    // Discount Assertions
    
    /**
     * Asserts that a discount percentage is valid (0-100).
     * @param discountPercentage the discount percentage to validate
     */
    public static void assertValidDiscountPercentage(double discountPercentage) {
        assertThat(discountPercentage).isBetween(0.0, 100.0);
    }
    
    /**
     * Asserts that a final amount reflects the correct discount.
     * @param originalAmount the original amount
     * @param discountPercentage the discount percentage
     * @param finalAmount the final amount after discount
     */
    public static void assertCorrectDiscountApplied(double originalAmount, double discountPercentage, double finalAmount) {
        double expectedFinalAmount = originalAmount * (1 - discountPercentage / 100);
        assertThat(finalAmount).isCloseTo(expectedFinalAmount, within(0.01));
    }
}