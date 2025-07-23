package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.entity.Product;
import com.example.springdroolsintegration.model.entity.RecommendationCustomer;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MapStruct mapper for Product Recommendation System entities and DTOs.
 * This interface defines mapping methods between Product entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    /**
     * Converts a ProductRecommendationRequest to a RecommendationCustomer entity.
     * 
     * @param request The product recommendation request
     * @return The recommendation customer entity
     */
    @Mapping(target = "id", source = "customerId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dateOfBirth", expression = "java(request.getAge() != null ? java.time.LocalDate.now().minusYears(request.getAge()) : null)")
    @Mapping(target = "preferredCategories", source = "preferredCategories")
    @Mapping(target = "preferredBrands", source = "preferredBrands")
    @Mapping(target = "recentlyViewedProducts", source = "recentlyViewedProducts")
    @Mapping(target = "recentlyPurchasedProducts", source = "recentlyPurchasedProducts")
    @Mapping(target = "wishListItems", source = "wishListItems")
    @Mapping(target = "lastLoginDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "zipCode", source = "zipCode")
    @Mapping(target = "city", source = "city")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "country", source = "country")
    @Mapping(target = "loyaltyTier", source = "loyaltyTier")
    @Mapping(target = "abandonedCartItems", source = "abandonedCartItems")
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastPurchaseDate", ignore = true)
    @Mapping(target = "orderCount", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    @Mapping(target = "marketingOptIn", ignore = true)
    @Mapping(target = "hasAppInstalled", ignore = true)
    @Mapping(target = "preferredChannels", ignore = true)
    @Mapping(target = "preferredPaymentMethods", ignore = true)
    @Mapping(target = "preferredDeliveryMethods", ignore = true)
    @Mapping(target = "browsingHistory", ignore = true)
    @Mapping(target = "satisfactionScore", ignore = true)
    @Mapping(target = "netPromoterScore", ignore = true)
    @Mapping(target = "hasReturns", ignore = true)
    @Mapping(target = "returnCount", ignore = true)
    RecommendationCustomer requestToCustomer(ProductRecommendationRequest request);
    
    /**
     * Converts a Product entity to a RecommendedProduct DTO.
     * 
     * @param product The product entity
     * @param score The recommendation score
     * @param reason The recommendation reason
     * @param rule The rule that generated the recommendation
     * @param type The recommendation type
     * @return The recommended product DTO
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "description", source = "product.description")
    @Mapping(target = "category", source = "product.category")
    @Mapping(target = "subcategory", source = "product.subcategory")
    @Mapping(target = "brand", source = "product.brand")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "salePrice", source = "product.salePrice")
    @Mapping(target = "inStock", source = "product.inStock")
    @Mapping(target = "averageRating", source = "product.averageRating")
    @Mapping(target = "ratingCount", source = "product.ratingCount")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "rule", source = "rule")
    @Mapping(target = "type", source = "type")
    ProductRecommendationResponse.RecommendedProduct productToRecommendedProduct(
            Product product, double score, String reason, String rule, String type);
    
    /**
     * Creates a ProductRecommendationResponse from a RecommendationCustomer and a list of recommended products.
     * 
     * @param customer The recommendation customer entity
     * @param currentProduct The current product (can be null)
     * @param recommendationType The recommendation type
     * @param recommendedProducts The list of recommended products
     * @param appliedRules The rules that were applied
     * @return The product recommendation response DTO
     */
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(customer.getFullName())")
    @Mapping(target = "currentProductId", expression = "java(currentProduct != null ? String.valueOf(currentProduct.getId()) : null)")
    @Mapping(target = "currentProductName", source = "currentProduct.name")
    @Mapping(target = "recommendationType", source = "recommendationType")
    @Mapping(target = "recommendations", source = "recommendedProducts")
    @Mapping(target = "categories", ignore = true) // Will be populated from recommendations
    @Mapping(target = "brands", ignore = true) // Will be populated from recommendations
    @Mapping(target = "appliedRules", source = "appliedRules")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "notes", ignore = true)
    ProductRecommendationResponse createResponse(
            RecommendationCustomer customer, 
            Product currentProduct,
            String recommendationType,
            List<ProductRecommendationResponse.RecommendedProduct> recommendedProducts,
            String appliedRules);
    
    /**
     * Updates a RecommendationCustomer entity with data from a ProductRecommendationRequest.
     * 
     * @param request The product recommendation request
     * @param customer The recommendation customer entity to update
     * @return The updated recommendation customer entity
     */
    @Mapping(target = "id", source = "customerId")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "gender", source = "gender")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dateOfBirth", expression = "java(java.time.LocalDate.now().minusYears(request.getAge() != null ? request.getAge() : 0))")
    @Mapping(target = "preferredCategories", source = "preferredCategories")
    @Mapping(target = "preferredBrands", source = "preferredBrands")
    @Mapping(target = "recentlyViewedProducts", source = "recentlyViewedProducts")
    @Mapping(target = "recentlyPurchasedProducts", source = "recentlyPurchasedProducts")
    @Mapping(target = "wishListItems", source = "wishListItems")
    @Mapping(target = "lastLoginDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "lastPurchaseDate", ignore = true)
    @Mapping(target = "orderCount", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "loyaltyPoints", ignore = true)
    @Mapping(target = "marketingOptIn", ignore = true)
    @Mapping(target = "hasAppInstalled", ignore = true)
    @Mapping(target = "preferredChannels", ignore = true)
    @Mapping(target = "preferredPaymentMethods", ignore = true)
    @Mapping(target = "preferredDeliveryMethods", ignore = true)
    @Mapping(target = "browsingHistory", ignore = true)
    @Mapping(target = "satisfactionScore", ignore = true)
    @Mapping(target = "netPromoterScore", ignore = true)
    @Mapping(target = "hasReturns", ignore = true)
    @Mapping(target = "returnCount", ignore = true)
    RecommendationCustomer updateCustomer(ProductRecommendationRequest request, @MappingTarget RecommendationCustomer customer);
    
    /**
     * Adds a product to a recommendation response.
     * 
     * @param response The product recommendation response to update
     * @param product The product to add
     * @param score The recommendation score
     * @param reason The recommendation reason
     * @param rule The rule that generated the recommendation
     * @param type The recommendation type
     */
    @Named("addProductToResponse")
    default void addProductToResponse(
            ProductRecommendationResponse response, 
            Product product, 
            double score, 
            String reason, 
            String rule, 
            String type) {
        
        if (response != null && product != null) {
            ProductRecommendationResponse.RecommendedProduct recommendedProduct = 
                    productToRecommendedProduct(product, score, reason, rule, type);
            response.addRecommendation(recommendedProduct);
        }
    }
    
    /**
     * Updates a ProductRecommendationResponse with applied rules.
     * 
     * @param response The product recommendation response to update
     * @param appliedRules The rules that were applied
     */
    @Named("updateResponseWithRules")
    default void updateResponseWithRules(
            ProductRecommendationResponse response, 
            String appliedRules) {
        
        if (response != null) {
            response.setAppliedRules(appliedRules);
        }
    }
    
    /**
     * Updates a ProductRecommendationResponse with notes.
     * 
     * @param response The product recommendation response to update
     * @param notes The notes to add
     */
    @Named("updateResponseWithNotes")
    default void updateResponseWithNotes(
            ProductRecommendationResponse response, 
            String notes) {
        
        if (response != null) {
            response.setNotes(notes);
        }
    }
}