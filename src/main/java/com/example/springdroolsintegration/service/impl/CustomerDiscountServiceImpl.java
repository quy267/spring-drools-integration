package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.mapper.CustomerMapper;
import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.DiscountRule;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import com.example.springdroolsintegration.service.CustomerDiscountService;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomerDiscountService interface.
 * This service handles the execution of discount rules for customer orders.
 */
@Service
public class CustomerDiscountServiceImpl implements CustomerDiscountService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDiscountServiceImpl.class);
    
    private final RuleExecutionService ruleExecutionService;
    private final CustomerMapper customerMapper;
    
    // Statistics tracking
    private final ConcurrentHashMap<String, AtomicLong> discountCounts = new ConcurrentHashMap<>();
    private final AtomicLong totalDiscountCalculations = new AtomicLong(0);
    private final AtomicLong totalBatchCalculations = new AtomicLong(0);
    
    /**
     * Constructor for CustomerDiscountServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleExecutionService The rule execution service
     * @param customerMapper The customer mapper
     */
    public CustomerDiscountServiceImpl(RuleExecutionService ruleExecutionService, CustomerMapper customerMapper) {
        this.ruleExecutionService = ruleExecutionService;
        this.customerMapper = customerMapper;
        
        logger.info("CustomerDiscountService initialized");
    }
    
    @Override
    public CustomerDiscountResponse calculateDiscount(CustomerDiscountRequest request) {
        if (request == null) {
            throw new RuleExecutionException("Cannot calculate discount for null request");
        }
        
        logger.debug("Calculating discount for customer: {}, order amount: {}", 
                request.getCustomerName(), request.getOrderAmount());
        
        try {
            // Convert request to domain entities
            Customer customer = customerMapper.requestToCustomer(request);
            Order order = customerMapper.requestToOrder(request);
            DiscountRule discountRule = new DiscountRule();
            
            // Create a facts list for rule execution
            List<Object> facts = new ArrayList<>();
            facts.add(customer);
            facts.add(order);
            facts.add(discountRule);
            
            // Execute rules on all facts
            for (Object fact : facts) {
                ruleExecutionService.executeRules(fact);
            }
            
            // Create response from the results
            CustomerDiscountResponse response = createResponse(customer, order, discountRule);
            
            // Update statistics
            updateDiscountStatistics(discountRule.getName(), discountRule.getDiscountPercentage());
            totalDiscountCalculations.incrementAndGet();
            
            logger.debug("Discount calculation completed. Discount: {}%, Applied rule: {}", 
                    response.getDiscountPercentage(), response.getAppliedRules());
            
            return response;
        } catch (Exception e) {
            logger.error("Error calculating discount for customer: {}", request.getCustomerName(), e);
            throw new RuleExecutionException("Error calculating discount: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<CustomerDiscountResponse> calculateDiscountBatch(List<CustomerDiscountRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new RuleExecutionException("Cannot calculate discounts for null or empty requests list");
        }
        
        logger.debug("Calculating discounts for batch of {} requests", requests.size());
        
        List<CustomerDiscountResponse> responses = requests.stream()
                .map(this::calculateDiscount)
                .collect(Collectors.toList());
        
        totalBatchCalculations.incrementAndGet();
        
        logger.debug("Batch discount calculation completed for {} requests", responses.size());
        
        return responses;
    }
    
    @Override
    public CompletableFuture<CustomerDiscountResponse> calculateDiscountAsync(CustomerDiscountRequest request) {
        return CompletableFuture.supplyAsync(() -> calculateDiscount(request));
    }
    
    @Override
    public Map<String, Object> getDiscountStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalDiscountCalculations", totalDiscountCalculations.get());
        statistics.put("totalBatchCalculations", totalBatchCalculations.get());
        
        Map<String, Long> discountRuleCounts = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : discountCounts.entrySet()) {
            discountRuleCounts.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("discountRuleCounts", discountRuleCounts);
        
        return statistics;
    }
    
    /**
     * Creates a response from the rule execution results.
     * 
     * @param customer The customer entity
     * @param order The order entity
     * @param discountRule The discount rule entity
     * @return The customer discount response
     */
    private CustomerDiscountResponse createResponse(Customer customer, Order order, DiscountRule discountRule) {
        CustomerDiscountResponse response = new CustomerDiscountResponse();
        
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getName());
        response.setLoyaltyTier(customer.getLoyaltyTier());
        response.setOriginalAmount(order.getAmount());
        response.setDiscountPercentage(discountRule.getDiscountPercentage());
        response.setDiscountAmount(calculateDiscountAmount(order.getAmount(), discountRule.getDiscountPercentage()));
        response.setFinalAmount(order.getAmount() - response.getDiscountAmount());
        response.setAppliedRules(discountRule.getName());
        response.setOrderId(order.getId());
        response.setTimestamp(LocalDateTime.now());
        response.setNotes("Order volume: " + order.getVolume() + " items");
        
        // Add discount details if available
        if (discountRule.getDiscountPercentage() > 0) {
            CustomerDiscountResponse.DiscountDetail detail = new CustomerDiscountResponse.DiscountDetail(
                    discountRule.getId(),
                    discountRule.getName(),
                    discountRule.getDiscountPercentage(),
                    response.getDiscountAmount(),
                    discountRule.getPriority()
            );
            response.addDiscount(detail);
        }
        
        return response;
    }
    
    /**
     * Calculates the discount amount based on the order amount and discount percentage.
     * 
     * @param orderAmount The order amount
     * @param discountPercentage The discount percentage
     * @return The calculated discount amount
     */
    private double calculateDiscountAmount(double orderAmount, double discountPercentage) {
        return orderAmount * (discountPercentage / 100.0);
    }
    
    /**
     * Updates discount statistics for a specific rule.
     * 
     * @param appliedRule The rule that was applied
     * @param discountPercentage The discount percentage that was applied
     */
    private void updateDiscountStatistics(String appliedRule, double discountPercentage) {
        if (appliedRule != null && !appliedRule.isEmpty()) {
            discountCounts.computeIfAbsent(appliedRule, k -> new AtomicLong(0)).incrementAndGet();
        }
    }
}