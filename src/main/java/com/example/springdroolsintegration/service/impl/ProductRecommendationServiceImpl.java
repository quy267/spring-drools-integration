package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.config.CacheConfig;
import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.mapper.ProductMapper;
import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.entity.Product;
import com.example.springdroolsintegration.model.entity.RecommendationCustomer;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import com.example.springdroolsintegration.service.ProductRecommendationService;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of the ProductRecommendationService interface.
 * This service handles the execution of product recommendation rules for customers.
 */
@Service
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRecommendationServiceImpl.class);
    
    private final RuleExecutionService ruleExecutionService;
    private final ProductMapper productMapper;
    
    // Mock product repository (in a real application, this would be a repository or service)
    private final Map<Long, Product> productRepository = new HashMap<>();
    
    // Statistics tracking
    private final ConcurrentHashMap<String, AtomicLong> recommendationCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> categoryRecommendationCounts = new ConcurrentHashMap<>();
    private final AtomicLong totalRecommendations = new AtomicLong(0);
    private final AtomicLong totalBatchRecommendations = new AtomicLong(0);
    private final AtomicLong totalProductBasedRecommendations = new AtomicLong(0);
    
    /**
     * Constructor for ProductRecommendationServiceImpl.
     * Uses constructor injection for dependencies as per Spring Boot best practices.
     *
     * @param ruleExecutionService The rule execution service
     * @param productMapper The product mapper
     */
    public ProductRecommendationServiceImpl(RuleExecutionService ruleExecutionService, ProductMapper productMapper) {
        this.ruleExecutionService = ruleExecutionService;
        this.productMapper = productMapper;
        
        // Initialize with some mock products (in a real application, these would come from a database)
        initializeMockProducts();
        
        logger.info("ProductRecommendationService initialized");
    }
    
    @Override
    @Cacheable(value = CacheConfig.PRODUCT_RECOMMENDATION_CACHE, 
               key = "#request.customerId + '-' + #request.currentProductId + '-' + #request.maxRecommendations",
               unless = "#result == null")
    public ProductRecommendationResponse getRecommendations(ProductRecommendationRequest request) {
        if (request == null) {
            throw new RuleExecutionException("Cannot get recommendations for null request");
        }
        
        String executionId = UUID.randomUUID().toString();
        MDC.put("executionId", executionId);
        
        logger.debug("Getting recommendations for customer: {}, execution ID: {}", 
                request.getCustomerId(), executionId);
        
        try {
            // Convert request to domain entities
            RecommendationCustomer customer = productMapper.requestToCustomer(request);
            
            // Create a list to hold recommended products
            List<ProductRecommendationResponse.RecommendedProduct> recommendedProducts = new ArrayList<>();
            
            // Create a facts list for rule execution
            List<Object> facts = new ArrayList<>();
            facts.add(customer);
            
            // Add some products as facts (in a real application, these would be selected based on criteria)
            List<Product> candidateProducts = new ArrayList<>(productRepository.values());
            facts.addAll(candidateProducts);
            
            // Create a recommendation context object to hold results
            RecommendationContext context = new RecommendationContext(customer, candidateProducts, request.getMaxRecommendations());
            facts.add(context);
            
            // Execute rules on all facts
            for (Object fact : facts) {
                ruleExecutionService.executeRules(fact);
            }
            
            // Get recommended products from the context
            for (RecommendationResult result : context.getResults()) {
                ProductRecommendationResponse.RecommendedProduct recommendedProduct = 
                        productMapper.productToRecommendedProduct(
                                result.getProduct(), 
                                result.getScore(), 
                                result.getReason(), 
                                result.getRule(), 
                                result.getType());
                recommendedProducts.add(recommendedProduct);
            }
            
            // Create response
            Product currentProduct = null;
            if (request.getCurrentProductId() != null) {
                try {
                    Long productIdLong = Long.parseLong(request.getCurrentProductId());
                    currentProduct = productRepository.get(productIdLong);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid product ID format: {}", request.getCurrentProductId());
                }
            }
            
            ProductRecommendationResponse response = productMapper.createResponse(
                    customer, 
                    currentProduct, 
                    "PERSONALIZED", 
                    recommendedProducts, 
                    context.getAppliedRules());
            
            // Update statistics
            updateRecommendationStatistics(recommendedProducts);
            totalRecommendations.incrementAndGet();
            
            logger.debug("Recommendations generated. Count: {}, execution ID: {}", 
                    recommendedProducts.size(), executionId);
            
            return response;
        } catch (Exception e) {
            logger.error("Error getting recommendations for customer: {}, execution ID: {}", 
                    request.getCustomerId(), executionId, e);
            throw new RuleExecutionException("Error getting recommendations: " + e.getMessage(), e);
        } finally {
            MDC.remove("executionId");
        }
    }
    
    @Override
    public List<ProductRecommendationResponse> getRecommendationsBatch(List<ProductRecommendationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new RuleExecutionException("Cannot get recommendations for null or empty requests list");
        }
        
        logger.debug("Getting recommendations for batch of {} requests", requests.size());
        
        List<ProductRecommendationResponse> responses = requests.stream()
                .map(this::getRecommendations)
                .collect(Collectors.toList());
        
        totalBatchRecommendations.incrementAndGet();
        
        logger.debug("Batch recommendations completed for {} requests", responses.size());
        
        return responses;
    }
    
    @Override
    public CompletableFuture<ProductRecommendationResponse> getRecommendationsAsync(ProductRecommendationRequest request) {
        return CompletableFuture.supplyAsync(() -> getRecommendations(request));
    }
    
    @Override
    @Cacheable(value = CacheConfig.PRODUCT_RECOMMENDATION_CACHE, 
               key = "'product-based-' + #productId + '-' + #maxRecommendations",
               unless = "#result == null")
    public ProductRecommendationResponse getProductBasedRecommendations(String productId, int maxRecommendations) {
        if (productId == null || productId.isEmpty()) {
            throw new RuleExecutionException("Cannot get recommendations for null or empty product ID");
        }
        
        String executionId = UUID.randomUUID().toString();
        MDC.put("executionId", executionId);
        
        logger.debug("Getting product-based recommendations for product: {}, execution ID: {}", 
                productId, executionId);
        
        try {
            // Get the current product
            Long productIdLong = Long.parseLong(productId);
            Product currentProduct = productRepository.get(productIdLong);
            if (currentProduct == null) {
                throw new RuleExecutionException("Product not found with ID: " + productId);
            }
            
            // Create a list to hold recommended products
            List<ProductRecommendationResponse.RecommendedProduct> recommendedProducts = new ArrayList<>();
            
            // Create a facts list for rule execution
            List<Object> facts = new ArrayList<>();
            facts.add(currentProduct);
            
            // Add some products as facts (in a real application, these would be selected based on criteria)
            List<Product> candidateProducts = new ArrayList<>(productRepository.values());
            facts.addAll(candidateProducts);
            
            // Create a product-based recommendation context
            ProductBasedRecommendationContext context = new ProductBasedRecommendationContext(
                    currentProduct, candidateProducts, maxRecommendations);
            facts.add(context);
            
            // Execute rules on all facts
            for (Object fact : facts) {
                ruleExecutionService.executeRules(fact);
            }
            
            // Get recommended products from the context
            for (RecommendationResult result : context.getResults()) {
                ProductRecommendationResponse.RecommendedProduct recommendedProduct = 
                        productMapper.productToRecommendedProduct(
                                result.getProduct(), 
                                result.getScore(), 
                                result.getReason(), 
                                result.getRule(), 
                                result.getType());
                recommendedProducts.add(recommendedProduct);
            }
            
            // Create a dummy customer for the response
            RecommendationCustomer dummyCustomer = new RecommendationCustomer();
            dummyCustomer.setId(0L);
            dummyCustomer.setFirstName("Product-Based");
            dummyCustomer.setLastName("Recommendation");
            
            // Create response
            ProductRecommendationResponse response = productMapper.createResponse(
                    dummyCustomer, 
                    currentProduct, 
                    "PRODUCT_BASED", 
                    recommendedProducts, 
                    context.getAppliedRules());
            
            // Update statistics
            updateRecommendationStatistics(recommendedProducts);
            totalProductBasedRecommendations.incrementAndGet();
            
            logger.debug("Product-based recommendations generated. Count: {}, execution ID: {}", 
                    recommendedProducts.size(), executionId);
            
            return response;
        } catch (Exception e) {
            logger.error("Error getting product-based recommendations for product: {}, execution ID: {}", 
                    productId, executionId, e);
            throw new RuleExecutionException("Error getting product-based recommendations: " + e.getMessage(), e);
        } finally {
            MDC.remove("executionId");
        }
    }
    
    @Override
    public Map<String, Object> getRecommendationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalRecommendations", totalRecommendations.get());
        statistics.put("totalBatchRecommendations", totalBatchRecommendations.get());
        statistics.put("totalProductBasedRecommendations", totalProductBasedRecommendations.get());
        
        Map<String, Long> recommendationTypeCounts = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : recommendationCounts.entrySet()) {
            recommendationTypeCounts.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("recommendationTypeCounts", recommendationTypeCounts);
        
        Map<String, Long> categoryCounts = new HashMap<>();
        for (Map.Entry<String, AtomicLong> entry : categoryRecommendationCounts.entrySet()) {
            categoryCounts.put(entry.getKey(), entry.getValue().get());
        }
        statistics.put("categoryRecommendationCounts", categoryCounts);
        
        return statistics;
    }
    
    /**
     * Updates recommendation statistics based on the recommended products.
     * 
     * @param recommendedProducts The list of recommended products
     */
    private void updateRecommendationStatistics(List<ProductRecommendationResponse.RecommendedProduct> recommendedProducts) {
        for (ProductRecommendationResponse.RecommendedProduct product : recommendedProducts) {
            // Update recommendation type counts
            if (product.getType() != null && !product.getType().isEmpty()) {
                recommendationCounts.computeIfAbsent(product.getType(), k -> new AtomicLong(0))
                        .incrementAndGet();
            }
            
            // Update category counts
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                categoryRecommendationCounts.computeIfAbsent(product.getCategory(), k -> new AtomicLong(0))
                        .incrementAndGet();
            }
        }
    }
    
    /**
     * Initializes mock products for testing.
     * In a real application, these would come from a database.
     */
    private void initializeMockProducts() {
        // Electronics
        Product smartphone = new Product();
        smartphone.setId(1001L);
        smartphone.setSku("ELEC-001");
        smartphone.setName("Smartphone X");
        smartphone.setDescription("Latest smartphone with advanced features");
        smartphone.setCategory("ELECTRONICS");
        smartphone.setSubcategory("PHONES");
        smartphone.setBrand("TechBrand");
        smartphone.setPrice(799.99);
        smartphone.setSalePrice(749.99);
        smartphone.setInStock(true);
        smartphone.setAverageRating(4.5);
        smartphone.setRatingCount(120);
        productRepository.put(smartphone.getId(), smartphone);
        
        Product laptop = new Product();
        laptop.setId(1002L);
        laptop.setSku("ELEC-002");
        laptop.setName("Laptop Pro");
        laptop.setDescription("High-performance laptop for professionals");
        laptop.setCategory("ELECTRONICS");
        laptop.setSubcategory("COMPUTERS");
        laptop.setBrand("TechBrand");
        laptop.setPrice(1299.99);
        laptop.setSalePrice(1199.99);
        laptop.setInStock(true);
        laptop.setAverageRating(4.7);
        laptop.setRatingCount(85);
        productRepository.put(laptop.getId(), laptop);
        
        // Clothing
        Product tShirt = new Product();
        tShirt.setId(1003L);
        tShirt.setSku("CLOTH-001");
        tShirt.setName("Cotton T-Shirt");
        tShirt.setDescription("Comfortable cotton t-shirt for everyday wear");
        tShirt.setCategory("CLOTHING");
        tShirt.setSubcategory("TOPS");
        tShirt.setBrand("FashionBrand");
        tShirt.setPrice(29.99);
        tShirt.setSalePrice(24.99);
        tShirt.setInStock(true);
        tShirt.setAverageRating(4.2);
        tShirt.setRatingCount(210);
        productRepository.put(tShirt.getId(), tShirt);
        
        Product jeans = new Product();
        jeans.setId(1004L);
        jeans.setSku("CLOTH-002");
        jeans.setName("Denim Jeans");
        jeans.setDescription("Classic denim jeans with modern fit");
        jeans.setCategory("CLOTHING");
        jeans.setSubcategory("BOTTOMS");
        jeans.setBrand("FashionBrand");
        jeans.setPrice(59.99);
        jeans.setSalePrice(49.99);
        jeans.setInStock(true);
        jeans.setAverageRating(4.0);
        jeans.setRatingCount(150);
        productRepository.put(jeans.getId(), jeans);
        
        // Home Goods
        Product coffeemaker = new Product();
        coffeemaker.setId(1005L);
        coffeemaker.setSku("HOME-001");
        coffeemaker.setName("Premium Coffee Maker");
        coffeemaker.setDescription("Automatic coffee maker with multiple brewing options");
        coffeemaker.setCategory("HOME_GOODS");
        coffeemaker.setSubcategory("KITCHEN");
        coffeemaker.setBrand("HomeBrand");
        coffeemaker.setPrice(129.99);
        coffeemaker.setSalePrice(99.99);
        coffeemaker.setInStock(true);
        coffeemaker.setAverageRating(4.3);
        coffeemaker.setRatingCount(75);
        productRepository.put(coffeemaker.getId(), coffeemaker);
        
        Product bedding = new Product();
        bedding.setId(1006L);
        bedding.setSku("HOME-002");
        bedding.setName("Luxury Bedding Set");
        bedding.setDescription("High-quality bedding set with duvet cover and pillowcases");
        bedding.setCategory("HOME_GOODS");
        bedding.setSubcategory("BEDROOM");
        bedding.setBrand("HomeBrand");
        bedding.setPrice(149.99);
        bedding.setSalePrice(129.99);
        bedding.setInStock(true);
        bedding.setAverageRating(4.6);
        bedding.setRatingCount(90);
        productRepository.put(bedding.getId(), bedding);
    }
    
    /**
     * Context class for holding recommendation results.
     * This class is used to pass data between rules and collect results.
     */
    public static class RecommendationContext {
        private final RecommendationCustomer customer;
        private final List<Product> candidateProducts;
        private final int maxRecommendations;
        private final List<RecommendationResult> results = new ArrayList<>();
        private String appliedRules = "";
        
        public RecommendationContext(RecommendationCustomer customer, List<Product> candidateProducts, int maxRecommendations) {
            this.customer = customer;
            this.candidateProducts = candidateProducts;
            this.maxRecommendations = maxRecommendations;
        }
        
        public RecommendationCustomer getCustomer() {
            return customer;
        }
        
        public List<Product> getCandidateProducts() {
            return candidateProducts;
        }
        
        public int getMaxRecommendations() {
            return maxRecommendations;
        }
        
        public List<RecommendationResult> getResults() {
            return results;
        }
        
        public void addResult(Product product, double score, String reason, String rule, String type) {
            if (results.size() < maxRecommendations) {
                results.add(new RecommendationResult(product, score, reason, rule, type));
            }
        }
        
        public String getAppliedRules() {
            return appliedRules;
        }
        
        public void setAppliedRules(String appliedRules) {
            this.appliedRules = appliedRules;
        }
        
        public void addAppliedRule(String rule) {
            if (appliedRules.isEmpty()) {
                appliedRules = rule;
            } else {
                appliedRules += ", " + rule;
            }
        }
    }
    
    /**
     * Context class for holding product-based recommendation results.
     * This class is used to pass data between rules and collect results.
     */
    public static class ProductBasedRecommendationContext {
        private final Product currentProduct;
        private final List<Product> candidateProducts;
        private final int maxRecommendations;
        private final List<RecommendationResult> results = new ArrayList<>();
        private String appliedRules = "";
        
        public ProductBasedRecommendationContext(Product currentProduct, List<Product> candidateProducts, int maxRecommendations) {
            this.currentProduct = currentProduct;
            this.candidateProducts = candidateProducts;
            this.maxRecommendations = maxRecommendations;
        }
        
        public Product getCurrentProduct() {
            return currentProduct;
        }
        
        public List<Product> getCandidateProducts() {
            return candidateProducts;
        }
        
        public int getMaxRecommendations() {
            return maxRecommendations;
        }
        
        public List<RecommendationResult> getResults() {
            return results;
        }
        
        public void addResult(Product product, double score, String reason, String rule, String type) {
            if (results.size() < maxRecommendations) {
                results.add(new RecommendationResult(product, score, reason, rule, type));
            }
        }
        
        public String getAppliedRules() {
            return appliedRules;
        }
        
        public void setAppliedRules(String appliedRules) {
            this.appliedRules = appliedRules;
        }
        
        public void addAppliedRule(String rule) {
            if (appliedRules.isEmpty()) {
                appliedRules = rule;
            } else {
                appliedRules += ", " + rule;
            }
        }
    }
    
    /**
     * Class for holding a recommendation result.
     */
    public static class RecommendationResult {
        private final Product product;
        private final double score;
        private final String reason;
        private final String rule;
        private final String type;
        
        public RecommendationResult(Product product, double score, String reason, String rule, String type) {
            this.product = product;
            this.score = score;
            this.reason = reason;
            this.rule = rule;
            this.type = type;
        }
        
        public Product getProduct() {
            return product;
        }
        
        public double getScore() {
            return score;
        }
        
        public String getReason() {
            return reason;
        }
        
        public String getRule() {
            return rule;
        }
        
        public String getType() {
            return type;
        }
    }
}