package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.entity.Product;
import com.example.springdroolsintegration.model.entity.RecommendationCustomer;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProductMapper.
 * Tests all mapping scenarios and edge cases for product recommendation mappings.
 */
@DisplayName("ProductMapper Tests")
class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
    }

    @Test
    @DisplayName("Should map ProductRecommendationRequest to RecommendationCustomer correctly")
    void shouldMapRequestToCustomer() {
        // Given
        ProductRecommendationRequest request = createSampleRequest();

        // When
        RecommendationCustomer customer = productMapper.requestToCustomer(request);

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(request.getCustomerId());
        assertThat(customer.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(request.getLastName());
        assertThat(customer.getGender()).isEqualTo(request.getGender());
        assertThat(customer.getEmail()).isEqualTo(request.getEmail());
        assertThat(customer.getPreferredCategories()).isEqualTo(request.getPreferredCategories());
        assertThat(customer.getPreferredBrands()).isEqualTo(request.getPreferredBrands());
        assertThat(customer.getRecentlyViewedProducts()).isEqualTo(request.getRecentlyViewedProducts());
        assertThat(customer.getRecentlyPurchasedProducts()).isEqualTo(request.getRecentlyPurchasedProducts());
        assertThat(customer.getWishListItems()).isEqualTo(request.getWishListItems());
        assertThat(customer.getLastLoginDate()).isNotNull();
        
        // Verify ignored properties are null/default
        assertThat(customer.getPhoneNumber()).isNull();
        assertThat(customer.getRegistrationDate()).isNull();
        assertThat(customer.getLastPurchaseDate()).isNull();
        assertThat(customer.getOrderCount()).isEqualTo(0);
        assertThat(customer.getTotalSpent()).isEqualTo(0.0);
        assertThat(customer.getAverageOrderValue()).isEqualTo(0.0);
        assertThat(customer.getLoyaltyPoints()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void shouldHandleNullRequest() {
        // When & Then
        RecommendationCustomer customer = productMapper.requestToCustomer(null);
        assertThat(customer).isNull();
    }

    @Test
    @DisplayName("Should map Product to RecommendedProduct correctly")
    void shouldMapProductToRecommendedProduct() {
        // Given
        Product product = createSampleProduct();
        double score = 0.85;
        String reason = "Based on purchase history";
        String rule = "PURCHASE_HISTORY_RULE";
        String type = "SIMILAR_PRODUCTS";

        // When
        ProductRecommendationResponse.RecommendedProduct recommendedProduct = 
                productMapper.productToRecommendedProduct(product, score, reason, rule, type);

        // Then
        assertThat(recommendedProduct).isNotNull();
        assertThat(recommendedProduct.getProductId()).isEqualTo(product.getId().toString());
        assertThat(recommendedProduct.getSku()).isEqualTo(product.getSku());
        assertThat(recommendedProduct.getName()).isEqualTo(product.getName());
        assertThat(recommendedProduct.getDescription()).isEqualTo(product.getDescription());
        assertThat(recommendedProduct.getCategory()).isEqualTo(product.getCategory());
        assertThat(recommendedProduct.getSubcategory()).isEqualTo(product.getSubcategory());
        assertThat(recommendedProduct.getBrand()).isEqualTo(product.getBrand());
        assertThat(recommendedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(recommendedProduct.getSalePrice()).isEqualTo(product.getSalePrice());
        assertThat(recommendedProduct.isInStock()).isEqualTo(product.isInStock());
        assertThat(recommendedProduct.getAverageRating()).isEqualTo(product.getAverageRating());
        assertThat(recommendedProduct.getRatingCount()).isEqualTo(product.getRatingCount());
        assertThat(recommendedProduct.getScore()).isEqualTo(score);
        assertThat(recommendedProduct.getReason()).isEqualTo(reason);
        assertThat(recommendedProduct.getRule()).isEqualTo(rule);
        assertThat(recommendedProduct.getType()).isEqualTo(type);
    }

    @Test
    @DisplayName("Should create ProductRecommendationResponse correctly")
    void shouldCreateResponse() {
        // Given
        RecommendationCustomer customer = createSampleCustomer();
        Product currentProduct = createSampleProduct();
        String recommendationType = "SIMILAR_PRODUCTS";
        List<ProductRecommendationResponse.RecommendedProduct> recommendedProducts = 
                Arrays.asList(createSampleRecommendedProduct());
        String appliedRules = "PURCHASE_HISTORY_RULE,AGE_BASED_RULE";

        // When
        ProductRecommendationResponse response = productMapper.createResponse(
                customer, currentProduct, recommendationType, recommendedProducts, appliedRules);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(customer.getId().toString());
        assertThat(response.getCustomerName()).isEqualTo(customer.getFullName());
        assertThat(response.getCurrentProductId()).isEqualTo(currentProduct.getId().toString());
        assertThat(response.getCurrentProductName()).isEqualTo(currentProduct.getName());
        assertThat(response.getRecommendationType()).isEqualTo(recommendationType);
        assertThat(response.getRecommendations()).isEqualTo(recommendedProducts);
        assertThat(response.getAppliedRules()).isEqualTo(appliedRules);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should update RecommendationCustomer correctly")
    void shouldUpdateCustomer() {
        // Given
        ProductRecommendationRequest request = createSampleRequest();
        RecommendationCustomer existingCustomer = createSampleCustomer();

        // When
        RecommendationCustomer updatedCustomer = productMapper.updateCustomer(request, existingCustomer);

        // Then
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getId()).isEqualTo(request.getCustomerId());
        assertThat(updatedCustomer.getFirstName()).isEqualTo(request.getFirstName());
        assertThat(updatedCustomer.getLastName()).isEqualTo(request.getLastName());
        assertThat(updatedCustomer.getGender()).isEqualTo(request.getGender());
        assertThat(updatedCustomer.getEmail()).isEqualTo(request.getEmail());
        assertThat(updatedCustomer.getLastLoginDate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle edge cases with empty collections")
    void shouldHandleEmptyCollections() {
        // Given
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(1L);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(30);
        request.setGender("MALE");
        request.setPreferredCategories(new HashSet<>());
        request.setPreferredBrands(new HashSet<>());

        // When
        RecommendationCustomer customer = productMapper.requestToCustomer(request);

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getPreferredCategories()).isEmpty();
        assertThat(customer.getPreferredBrands()).isEmpty();
    }

    private ProductRecommendationRequest createSampleRequest() {
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(1001L);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(30);
        request.setGender("MALE");
        request.setLoyaltyTier("GOLD");
        
        Set<String> categories = new HashSet<>();
        categories.add("Electronics");
        categories.add("Books");
        request.setPreferredCategories(categories);
        
        Set<String> brands = new HashSet<>();
        brands.add("Apple");
        brands.add("Samsung");
        request.setPreferredBrands(brands);
        
        Set<String> recentlyViewed = new HashSet<>();
        recentlyViewed.add("PROD001");
        recentlyViewed.add("PROD002");
        request.setRecentlyViewedProducts(recentlyViewed);
        
        Set<String> recentlyPurchased = new HashSet<>();
        recentlyPurchased.add("PROD003");
        request.setRecentlyPurchasedProducts(recentlyPurchased);
        
        Set<String> wishList = new HashSet<>();
        wishList.add("PROD004");
        request.setWishListItems(wishList);
        
        return request;
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("PROD001");
        product.setName("Sample Product");
        product.setDescription("A sample product for testing");
        product.setCategory("Electronics");
        product.setSubcategory("Smartphones");
        product.setBrand("Apple");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setSalePrice(BigDecimal.valueOf(899.99));
        product.setInStock(true);
        product.setAverageRating(4.5);
        product.setRatingCount(150);
        return product;
    }

    private RecommendationCustomer createSampleCustomer() {
        RecommendationCustomer customer = new RecommendationCustomer();
        customer.setId(1001L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@example.com");
        customer.setGender("MALE");
        customer.setDateOfBirth(LocalDate.of(1993, 5, 15));
        customer.setLastLoginDate(LocalDateTime.now());
        return customer;
    }

    private ProductRecommendationResponse.RecommendedProduct createSampleRecommendedProduct() {
        ProductRecommendationResponse.RecommendedProduct recommendedProduct = 
                new ProductRecommendationResponse.RecommendedProduct();
        recommendedProduct.setProductId("1");
        recommendedProduct.setSku("PROD001");
        recommendedProduct.setName("Sample Product");
        recommendedProduct.setCategory("Electronics");
        recommendedProduct.setBrand("Apple");
        recommendedProduct.setPrice(BigDecimal.valueOf(999.99));
        recommendedProduct.setScore(0.85);
        recommendedProduct.setReason("Based on purchase history");
        recommendedProduct.setRule("PURCHASE_HISTORY_RULE");
        recommendedProduct.setType("SIMILAR_PRODUCTS");
        return recommendedProduct;
    }
}