package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.dto.LoanApprovalResponse;
import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.DiscountRule;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import com.example.springdroolsintegration.model.request.LoanApprovalRequest;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance benchmark tests for rule execution services.
 * These tests measure the performance of rule execution for different services.
 */
@SpringBootTest
@ActiveProfiles("test")
public class PerformanceBenchmarkTest {

    @Autowired
    private RuleExecutionService ruleExecutionService;

    @Autowired
    private CustomerDiscountService customerDiscountService;

    @Autowired
    private LoanApprovalService loanApprovalService;

    @Autowired
    private ProductRecommendationService productRecommendationService;

    private static final int SMALL_BATCH_SIZE = 10;
    private static final int MEDIUM_BATCH_SIZE = 50;
    private static final int LARGE_BATCH_SIZE = 100;
    private static final int CONCURRENT_THREADS = 5;
    private static final int WARMUP_ITERATIONS = 3;

    @Test
    @DisplayName("Benchmark RuleExecutionService single fact execution")
    public void benchmarkRuleExecutionServiceSingleFact() {
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            Customer customer = createCustomer("Warmup", i);
            ruleExecutionService.executeRules(customer);
        }

        // Benchmark
        Instant start = Instant.now();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            Customer customer = createCustomer("Customer", i);
            Customer result = ruleExecutionService.executeRules(customer);
            assertNotNull(result, "Result should not be null");
        }
        
        Duration duration = Duration.between(start, Instant.now());
        double avgTimeMs = duration.toMillis() / (double) SMALL_BATCH_SIZE;
        
        System.out.println("RuleExecutionService single fact execution - Average time: " + avgTimeMs + " ms");
        assertTrue(avgTimeMs < 500, "Average execution time should be less than 500ms");
    }

    @Test
    @DisplayName("Benchmark RuleExecutionService batch execution")
    public void benchmarkRuleExecutionServiceBatch() {
        // Create a batch of customers
        List<Customer> customers = IntStream.range(0, MEDIUM_BATCH_SIZE)
                .mapToObj(i -> createCustomer("BatchCustomer", i))
                .collect(Collectors.toList());
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            ruleExecutionService.executeRulesForBatch(new ArrayList<>(customers.subList(0, 5)));
        }

        // Benchmark
        Instant start = Instant.now();
        
        List<Customer> results = ruleExecutionService.executeRulesForBatch(customers);
        
        Duration duration = Duration.between(start, Instant.now());
        double totalTimeMs = duration.toMillis();
        double avgTimePerItemMs = totalTimeMs / MEDIUM_BATCH_SIZE;
        
        System.out.println("RuleExecutionService batch execution - Total time: " + totalTimeMs + " ms, Average time per item: " + avgTimePerItemMs + " ms");
        assertTrue(avgTimePerItemMs < 100, "Average execution time per item should be less than 100ms");
        assertEquals(MEDIUM_BATCH_SIZE, results.size(), "Should return the same number of results");
    }

    @Test
    @DisplayName("Benchmark RuleExecutionService concurrent execution")
    public void benchmarkRuleExecutionServiceConcurrent() throws InterruptedException {
        // Create a list of futures
        List<CompletableFuture<Customer>> futures = new ArrayList<>();
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            Customer customer = createCustomer("WarmupConcurrent", i);
            ruleExecutionService.executeRulesAsync(customer).join();
        }

        // Benchmark
        Instant start = Instant.now();
        
        // Submit concurrent tasks
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            Customer customer = createCustomer("ConcurrentCustomer", i);
            futures.add(ruleExecutionService.executeRulesAsync(customer));
        }
        
        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        Duration duration = Duration.between(start, Instant.now());
        double totalTimeMs = duration.toMillis();
        double avgTimePerThreadMs = totalTimeMs / CONCURRENT_THREADS;
        
        System.out.println("RuleExecutionService concurrent execution - Total time: " + totalTimeMs + 
                " ms, Average time per thread: " + avgTimePerThreadMs + " ms");
        
        // Verify all futures completed successfully
        for (CompletableFuture<Customer> future : futures) {
            assertTrue(future.isDone(), "Future should be completed");
            assertFalse(future.isCompletedExceptionally(), "Future should not have completed exceptionally");
        }
    }

    @Test
    @DisplayName("Benchmark CustomerDiscountService performance")
    public void benchmarkCustomerDiscountService() {
        // Create a batch of requests
        List<CustomerDiscountRequest> requests = IntStream.range(0, MEDIUM_BATCH_SIZE)
                .mapToObj(i -> createDiscountRequest("DiscountCustomer" + i, 30 + i % 50, 100.0 + i * 10))
                .collect(Collectors.toList());
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            customerDiscountService.calculateDiscount(requests.get(i % requests.size()));
        }

        // Benchmark single execution
        Instant singleStart = Instant.now();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(requests.get(i));
            assertNotNull(response, "Response should not be null");
        }
        
        Duration singleDuration = Duration.between(singleStart, Instant.now());
        double avgSingleTimeMs = singleDuration.toMillis() / (double) SMALL_BATCH_SIZE;
        
        System.out.println("CustomerDiscountService single execution - Average time: " + avgSingleTimeMs + " ms");
        
        // Benchmark batch execution
        Instant batchStart = Instant.now();
        
        List<CustomerDiscountResponse> batchResponses = customerDiscountService.calculateDiscountBatch(requests);
        
        Duration batchDuration = Duration.between(batchStart, Instant.now());
        double totalBatchTimeMs = batchDuration.toMillis();
        double avgBatchTimePerItemMs = totalBatchTimeMs / MEDIUM_BATCH_SIZE;
        
        System.out.println("CustomerDiscountService batch execution - Total time: " + totalBatchTimeMs + 
                " ms, Average time per item: " + avgBatchTimePerItemMs + " ms");
        
        assertEquals(MEDIUM_BATCH_SIZE, batchResponses.size(), "Should return the same number of results");
    }

    @Test
    @DisplayName("Benchmark LoanApprovalService performance")
    public void benchmarkLoanApprovalService() {
        // Create a batch of requests
        List<LoanApprovalRequest> requests = IntStream.range(0, MEDIUM_BATCH_SIZE)
                .mapToObj(i -> createLoanRequest("LoanApplicant" + i, 600 + i % 300, 50000.0 + i * 1000))
                .collect(Collectors.toList());
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            loanApprovalService.evaluateLoanApplication(requests.get(i % requests.size()));
        }

        // Benchmark single execution
        Instant singleStart = Instant.now();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            LoanApprovalResponse response = loanApprovalService.evaluateLoanApplication(requests.get(i));
            assertNotNull(response, "Response should not be null");
        }
        
        Duration singleDuration = Duration.between(singleStart, Instant.now());
        double avgSingleTimeMs = singleDuration.toMillis() / (double) SMALL_BATCH_SIZE;
        
        System.out.println("LoanApprovalService single execution - Average time: " + avgSingleTimeMs + " ms");
        
        // Benchmark batch execution
        Instant batchStart = Instant.now();
        
        List<LoanApprovalResponse> batchResponses = loanApprovalService.evaluateLoanApplicationBatch(requests);
        
        Duration batchDuration = Duration.between(batchStart, Instant.now());
        double totalBatchTimeMs = batchDuration.toMillis();
        double avgBatchTimePerItemMs = totalBatchTimeMs / MEDIUM_BATCH_SIZE;
        
        System.out.println("LoanApprovalService batch execution - Total time: " + totalBatchTimeMs + 
                " ms, Average time per item: " + avgBatchTimePerItemMs + " ms");
        
        assertEquals(MEDIUM_BATCH_SIZE, batchResponses.size(), "Should return the same number of results");
    }

    @Test
    @DisplayName("Benchmark ProductRecommendationService performance")
    public void benchmarkProductRecommendationService() {
        // Create a batch of requests
        List<ProductRecommendationRequest> requests = IntStream.range(0, MEDIUM_BATCH_SIZE)
                .mapToObj(i -> createRecommendationRequest("RecommendationCustomer" + i, (long) (1000 + i)))
                .collect(Collectors.toList());
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            productRecommendationService.getRecommendations(requests.get(i % requests.size()));
        }

        // Benchmark single execution
        Instant singleStart = Instant.now();
        
        for (int i = 0; i < SMALL_BATCH_SIZE; i++) {
            ProductRecommendationResponse response = productRecommendationService.getRecommendations(requests.get(i));
            assertNotNull(response, "Response should not be null");
        }
        
        Duration singleDuration = Duration.between(singleStart, Instant.now());
        double avgSingleTimeMs = singleDuration.toMillis() / (double) SMALL_BATCH_SIZE;
        
        System.out.println("ProductRecommendationService single execution - Average time: " + avgSingleTimeMs + " ms");
        
        // Benchmark batch execution
        Instant batchStart = Instant.now();
        
        List<ProductRecommendationResponse> batchResponses = productRecommendationService.getRecommendationsBatch(requests);
        
        Duration batchDuration = Duration.between(batchStart, Instant.now());
        double totalBatchTimeMs = batchDuration.toMillis();
        double avgBatchTimePerItemMs = totalBatchTimeMs / MEDIUM_BATCH_SIZE;
        
        System.out.println("ProductRecommendationService batch execution - Total time: " + totalBatchTimeMs + 
                " ms, Average time per item: " + avgBatchTimePerItemMs + " ms");
        
        assertEquals(MEDIUM_BATCH_SIZE, batchResponses.size(), "Should return the same number of results");
    }

    @Test
    @DisplayName("Benchmark all services with concurrent execution")
    public void benchmarkAllServicesConcurrent() throws InterruptedException, ExecutionException {
        // Create executor service
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        
        // Create requests for each service
        CustomerDiscountRequest discountRequest = createDiscountRequest("ConcurrentDiscount", 35, 200.0);
        LoanApprovalRequest loanRequest = createLoanRequest("ConcurrentLoan", 720, 250000.0);
        ProductRecommendationRequest recommendationRequest = createRecommendationRequest("ConcurrentRecommendation", 5000L);
        
        // Warm up
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            customerDiscountService.calculateDiscount(discountRequest);
            loanApprovalService.evaluateLoanApplication(loanRequest);
            productRecommendationService.getRecommendations(recommendationRequest);
        }

        // Benchmark
        Instant start = Instant.now();
        
        // Submit concurrent tasks for each service
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            // Customer discount tasks
            futures.add(CompletableFuture.supplyAsync(() -> 
                    customerDiscountService.calculateDiscount(discountRequest), executor));
            
            // Loan approval tasks
            futures.add(CompletableFuture.supplyAsync(() -> 
                    loanApprovalService.evaluateLoanApplication(loanRequest), executor));
            
            // Product recommendation tasks
            futures.add(CompletableFuture.supplyAsync(() -> 
                    productRecommendationService.getRecommendations(recommendationRequest), executor));
        }
        
        // Wait for all futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.get(); // Wait for completion
        
        Duration duration = Duration.between(start, Instant.now());
        double totalTimeMs = duration.toMillis();
        double avgTimePerTaskMs = totalTimeMs / futures.size();
        
        System.out.println("All services concurrent execution - Total time: " + totalTimeMs + 
                " ms, Average time per task: " + avgTimePerTaskMs + " ms, Total tasks: " + futures.size());
        
        // Verify all futures completed successfully
        for (CompletableFuture<?> future : futures) {
            assertTrue(future.isDone(), "Future should be completed");
            assertFalse(future.isCompletedExceptionally(), "Future should not have completed exceptionally");
            assertNotNull(future.get(), "Result should not be null");
        }
        
        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * Helper method to create a customer for testing.
     */
    private Customer createCustomer(String namePrefix, int index) {
        Customer customer = new Customer();
        customer.setId((long) index);
        customer.setName(namePrefix + " " + index);
        customer.setAge(30 + (index % 50)); // Ages 30-79
        customer.setLoyaltyTier(index % 3 == 0 ? "GOLD" : index % 3 == 1 ? "SILVER" : "BRONZE");
        return customer;
    }

    /**
     * Helper method to create a discount request for testing.
     */
    private CustomerDiscountRequest createDiscountRequest(String customerName, int age, double orderAmount) {
        CustomerDiscountRequest request = new CustomerDiscountRequest(
                customerName,
                age,
                age > 60 ? "GOLD" : age > 40 ? "SILVER" : "BRONZE",
                orderAmount,
                3
        );
        request.setCustomerEmail(customerName.toLowerCase().replace(" ", ".") + "@example.com");
        
        // Add order items
        CustomerDiscountRequest.OrderItemRequest item1 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-" + (100 + age % 10),
                "Product " + (100 + age % 10),
                99.99,
                1,
                "ELECTRONICS"
        );
        CustomerDiscountRequest.OrderItemRequest item2 = new CustomerDiscountRequest.OrderItemRequest(
                "PROD-" + (200 + age % 5),
                "Product " + (200 + age % 5),
                49.99,
                2,
                "ACCESSORIES"
        );
        request.addOrderItem(item1);
        request.addOrderItem(item2);
        
        return request;
    }

    /**
     * Helper method to create a loan request for testing.
     */
    private LoanApprovalRequest createLoanRequest(String name, int creditScore, double loanAmount) {
        LoanApprovalRequest request = new LoanApprovalRequest();
        String[] nameParts = name.split(" ");
        request.setFirstName(nameParts.length > 0 ? nameParts[0] : name);
        request.setLastName(nameParts.length > 1 ? nameParts[1] : "Applicant");
        request.setDateOfBirth(LocalDate.now().minusYears(35));
        request.setSsn("123-45-" + (1000 + creditScore % 9000));
        request.setEmail(name.toLowerCase().replace(" ", ".") + "@example.com");
        request.setEmploymentStatus("EMPLOYED");
        request.setAnnualIncome(75000.0 + creditScore * 100);
        request.setMonthlyDebtPayments(2000.0);
        request.setCreditScore(creditScore);
        request.setLoanType("MORTGAGE");
        request.setLoanPurpose("PURCHASE");
        request.setLoanAmount(loanAmount);
        request.setLoanTermMonths(360); // 30 years
        request.setInterestRate(4.5);
        request.setHasCoApplicant(false);
        
        return request;
    }

    /**
     * Helper method to create a recommendation request for testing.
     */
    private ProductRecommendationRequest createRecommendationRequest(String name, Long customerId) {
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        String[] nameParts = name.split(" ");
        request.setCustomerId(customerId);
        request.setFirstName(nameParts.length > 0 ? nameParts[0] : name);
        request.setLastName(nameParts.length > 1 ? nameParts[1] : "Customer");
        request.setEmail(name.toLowerCase().replace(" ", ".") + "@example.com");
        request.setAge(30 + (int)(customerId % 40));
        request.setGender("M");
        request.setMaxRecommendations(5);
        request.setIncludeOutOfStock(false);
        
        // Add preferred categories and brands
        if (customerId % 3 == 0) {
            request.addPreferredCategory("ELECTRONICS");
            request.addPreferredBrand("TechBrand");
        } else if (customerId % 3 == 1) {
            request.addPreferredCategory("CLOTHING");
            request.addPreferredBrand("FashionBrand");
        } else {
            request.addPreferredCategory("HOME_GOODS");
            request.addPreferredBrand("HomeBrand");
        }
        
        // Add recently viewed products
        request.addRecentlyViewedProduct("PROD-" + (1000 + customerId % 10));
        
        return request;
    }
}