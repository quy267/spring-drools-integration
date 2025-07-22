package com.example.springdroolsintegration.service;

import com.example.springdroolsintegration.model.dto.ProductRecommendationResponse;
import com.example.springdroolsintegration.model.request.ProductRecommendationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional integration tests for ProductRecommendationService focusing on:
 * 1. Customer age boundaries
 * 2. Customer gender variations
 * 3. Customer category distinctions
 * 4. Purchase history boundaries
 * 5. Seasonal variations
 * 6. Inventory level boundaries
 * 7. Combinations of multiple factors
 * 8. Error scenarios
 */
@SpringBootTest
@ActiveProfiles("test")
public class ProductRecommendationServiceAdditionalIntegrationTest {

    @Autowired
    private ProductRecommendationService productRecommendationService;

    @ParameterizedTest
    @DisplayName("Test recommendations at customer age boundaries")
    @CsvSource({
        "18, TRENDING, Trending Items", // Young adult lower bound
        "25, TRENDING, Trending Items", // Young adult upper bound
        "30, ELECTRONICS, Smart Speaker", // Above young adult range
        "65, SPECIAL_OFFERS, Senior Discount Items", // Senior lower bound
        "70, SPECIAL_OFFERS, Senior Discount Items" // Well into senior range
    })
    void testCustomerAgeBoundaries(int age, String expectedCategory, String expectedProduct) {
        // Arrange
        ProductRecommendationRequest request = createBasicRequest();
        request.setAge(age);

        // Act
        ProductRecommendationResponse response = productRecommendationService.getRecommendations(request);

        // Assert
        assertNotNull(response);
        assertFalse(response.getRecommendations().isEmpty());
        assertEquals(expectedCategory, response.getRecommendations().get(0).getCategory());
        assertEquals(expectedProduct, response.getRecommendations().get(0).getName());
    }

    @Test
    @DisplayName("Test gender-specific recommendations")
    void testGenderSpecificRecommendations() {
        // Arrange - Male customer in summer
        ProductRecommendationRequest maleRequest = createBasicRequest();
        maleRequest.setGender("M");
        maleRequest.setCurrentSeason("SUMMER");

        // Arrange - Female customer in summer
        ProductRecommendationRequest femaleRequest = createBasicRequest();
        femaleRequest.setGender("F");
        femaleRequest.setCurrentSeason("SUMMER");

        // Arrange - Male customer in winter
        ProductRecommendationRequest maleWinterRequest = createBasicRequest();
        maleWinterRequest.setGender("M");
        maleWinterRequest.setCurrentSeason("WINTER");

        // Arrange - Female customer in winter
        ProductRecommendationRequest femaleWinterRequest = createBasicRequest();
        femaleWinterRequest.setGender("F");
        femaleWinterRequest.setCurrentSeason("WINTER");

        // Act
        ProductRecommendationResponse maleResponse = productRecommendationService.getRecommendations(maleRequest);
        ProductRecommendationResponse femaleResponse = productRecommendationService.getRecommendations(femaleRequest);
        ProductRecommendationResponse maleWinterResponse = productRecommendationService.getRecommendations(maleWinterRequest);
        ProductRecommendationResponse femaleWinterResponse = productRecommendationService.getRecommendations(femaleWinterRequest);

        // Assert
        assertNotNull(maleResponse);
        assertFalse(maleResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", maleResponse.getRecommendations().get(0).getCategory());
        assertEquals("Men's Summer Collection", maleResponse.getRecommendations().get(0).getName());

        assertNotNull(femaleResponse);
        assertFalse(femaleResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", femaleResponse.getRecommendations().get(0).getCategory());
        assertEquals("Women's Summer Collection", femaleResponse.getRecommendations().get(0).getName());

        assertNotNull(maleWinterResponse);
        assertFalse(maleWinterResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", maleWinterResponse.getRecommendations().get(0).getCategory());
        assertEquals("Men's Winter Collection", maleWinterResponse.getRecommendations().get(0).getName());

        assertNotNull(femaleWinterResponse);
        assertFalse(femaleWinterResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", femaleWinterResponse.getRecommendations().get(0).getCategory());
        assertEquals("Women's Winter Collection", femaleWinterResponse.getRecommendations().get(0).getName());
    }

    @Test
    @DisplayName("Test customer category distinctions")
    void testCustomerCategoryDistinctions() {
        // Arrange - New young male customer
        ProductRecommendationRequest newYoungMaleRequest = createBasicRequest();
        newYoungMaleRequest.setAge(25);
        newYoungMaleRequest.setGender("M");
        // No purchase history implies new customer

        // Arrange - New young female customer
        ProductRecommendationRequest newYoungFemaleRequest = createBasicRequest();
        newYoungFemaleRequest.setAge(25);
        newYoungFemaleRequest.setGender("F");
        // No purchase history implies new customer

        // Arrange - New general customer
        ProductRecommendationRequest newGeneralRequest = createBasicRequest();
        newGeneralRequest.setAge(40);
        // No purchase history implies new customer

        // Arrange - Returning electronics customer
        ProductRecommendationRequest returningElectronicsRequest = createBasicRequest();
        Set<String> electronicsPurchases = new HashSet<>();
        electronicsPurchases.add("PROD-123");
        returningElectronicsRequest.setRecentlyPurchasedProducts(electronicsPurchases);
        returningElectronicsRequest.setCurrentCategory("ELECTRONICS");

        // Act
        ProductRecommendationResponse newYoungMaleResponse = productRecommendationService.getRecommendations(newYoungMaleRequest);
        ProductRecommendationResponse newYoungFemaleResponse = productRecommendationService.getRecommendations(newYoungFemaleRequest);
        ProductRecommendationResponse newGeneralResponse = productRecommendationService.getRecommendations(newGeneralRequest);
        ProductRecommendationResponse returningElectronicsResponse = productRecommendationService.getRecommendations(returningElectronicsRequest);

        // Assert
        assertNotNull(newYoungMaleResponse);
        assertFalse(newYoungMaleResponse.getRecommendations().isEmpty());
        assertEquals("ELECTRONICS", newYoungMaleResponse.getRecommendations().get(0).getCategory());
        assertEquals("Gaming Console", newYoungMaleResponse.getRecommendations().get(0).getName());
        assertTrue(newYoungMaleResponse.getAppliedRules().contains("New Young Male Tech"));

        assertNotNull(newYoungFemaleResponse);
        assertFalse(newYoungFemaleResponse.getRecommendations().isEmpty());
        assertEquals("ELECTRONICS", newYoungFemaleResponse.getRecommendations().get(0).getCategory());
        assertEquals("Smartphone", newYoungFemaleResponse.getRecommendations().get(0).getName());
        assertTrue(newYoungFemaleResponse.getAppliedRules().contains("New Young Female Tech"));

        assertNotNull(newGeneralResponse);
        assertFalse(newGeneralResponse.getRecommendations().isEmpty());
        assertEquals("ELECTRONICS", newGeneralResponse.getRecommendations().get(0).getCategory());
        assertEquals("Smart Speaker", newGeneralResponse.getRecommendations().get(0).getName());
        assertTrue(newGeneralResponse.getAppliedRules().contains("New Customer General"));

        assertNotNull(returningElectronicsResponse);
        assertFalse(returningElectronicsResponse.getRecommendations().isEmpty());
        assertEquals("ELECTRONICS", returningElectronicsResponse.getRecommendations().get(0).getCategory());
        assertEquals("Laptop", returningElectronicsResponse.getRecommendations().get(0).getName());
        assertTrue(returningElectronicsResponse.getAppliedRules().contains("Returning Electronics Customer"));
    }

    @Test
    @DisplayName("Test purchase history boundaries")
    void testPurchaseHistoryBoundaries() {
        // Arrange - First-time buyer
        ProductRecommendationRequest firstTimeBuyerRequest = createBasicRequest();
        // No purchase history implies first-time buyer

        // Arrange - Few purchases
        ProductRecommendationRequest fewPurchasesRequest = createBasicRequest();
        Set<String> fewPurchases = new HashSet<>();
        fewPurchases.add("PROD-123");
        fewPurchases.add("PROD-456");
        fewPurchasesRequest.setRecentlyPurchasedProducts(fewPurchases);

        // Arrange - Frequent buyer
        ProductRecommendationRequest frequentBuyerRequest = createBasicRequest();
        Set<String> manyPurchases = new HashSet<>();
        for (int i = 1; i <= 11; i++) {
            manyPurchases.add("PROD-" + i);
        }
        frequentBuyerRequest.setRecentlyPurchasedProducts(manyPurchases);

        // Act
        ProductRecommendationResponse firstTimeBuyerResponse = productRecommendationService.getRecommendations(firstTimeBuyerRequest);
        ProductRecommendationResponse fewPurchasesResponse = productRecommendationService.getRecommendations(fewPurchasesRequest);
        ProductRecommendationResponse frequentBuyerResponse = productRecommendationService.getRecommendations(frequentBuyerRequest);

        // Assert
        assertNotNull(firstTimeBuyerResponse);
        assertFalse(firstTimeBuyerResponse.getRecommendations().isEmpty());
        assertEquals("ELECTRONICS", firstTimeBuyerResponse.getRecommendations().get(0).getCategory());
        assertEquals("Smart Speaker", firstTimeBuyerResponse.getRecommendations().get(0).getName());
        assertTrue(firstTimeBuyerResponse.getAppliedRules().contains("New Customer General"));

        assertNotNull(fewPurchasesResponse);
        assertFalse(fewPurchasesResponse.getRecommendations().isEmpty());
        // The exact recommendation depends on the category of the purchased products
        // but it should not be the "Loyalty Rewards" for frequent buyers

        assertNotNull(frequentBuyerResponse);
        assertFalse(frequentBuyerResponse.getRecommendations().isEmpty());
        assertEquals("PREMIUM", frequentBuyerResponse.getRecommendations().get(0).getCategory());
        assertEquals("Loyalty Rewards", frequentBuyerResponse.getRecommendations().get(0).getName());
        assertTrue(frequentBuyerResponse.getAppliedRules().contains("Frequent Buyer"));
    }

    @Test
    @DisplayName("Test seasonal variations")
    void testSeasonalVariations() {
        // Arrange - Summer season
        ProductRecommendationRequest summerRequest = createBasicRequest();
        summerRequest.setCurrentSeason("SUMMER");
        summerRequest.setGender("M");

        // Arrange - Winter season
        ProductRecommendationRequest winterRequest = createBasicRequest();
        winterRequest.setCurrentSeason("WINTER");
        winterRequest.setGender("M");

        // Act
        ProductRecommendationResponse summerResponse = productRecommendationService.getRecommendations(summerRequest);
        ProductRecommendationResponse winterResponse = productRecommendationService.getRecommendations(winterRequest);

        // Assert
        assertNotNull(summerResponse);
        assertFalse(summerResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", summerResponse.getRecommendations().get(0).getCategory());
        assertEquals("Men's Summer Collection", summerResponse.getRecommendations().get(0).getName());
        assertTrue(summerResponse.getAppliedRules().contains("Summer Clothing Male"));

        assertNotNull(winterResponse);
        assertFalse(winterResponse.getRecommendations().isEmpty());
        assertEquals("CLOTHING", winterResponse.getRecommendations().get(0).getCategory());
        assertEquals("Men's Winter Collection", winterResponse.getRecommendations().get(0).getName());
        assertTrue(winterResponse.getAppliedRules().contains("Winter Clothing Male"));
    }

    @Test
    @DisplayName("Test inventory level boundaries")
    void testInventoryLevelBoundaries() {
        // Note: Inventory levels might be handled internally by the service
        // and not directly exposed in the request. This test assumes the service
        // has access to inventory data and applies the rules accordingly.

        // Arrange - High inventory (clearance)
        ProductRecommendationRequest highInventoryRequest = createBasicRequest();
        highInventoryRequest.setRecommendationType("CLEARANCE");

        // Arrange - Low inventory (premium)
        ProductRecommendationRequest lowInventoryRequest = createBasicRequest();
        lowInventoryRequest.setRecommendationType("LIMITED_STOCK");

        // Act
        ProductRecommendationResponse highInventoryResponse = productRecommendationService.getRecommendations(highInventoryRequest);
        ProductRecommendationResponse lowInventoryResponse = productRecommendationService.getRecommendations(lowInventoryRequest);

        // Assert
        assertNotNull(highInventoryResponse);
        assertFalse(highInventoryResponse.getRecommendations().isEmpty());
        assertEquals("CLEARANCE", highInventoryResponse.getRecommendations().get(0).getCategory());
        assertEquals("Discounted Items", highInventoryResponse.getRecommendations().get(0).getName());
        assertTrue(highInventoryResponse.getAppliedRules().contains("High Inventory Clearance"));

        assertNotNull(lowInventoryResponse);
        assertFalse(lowInventoryResponse.getRecommendations().isEmpty());
        assertEquals("PREMIUM", lowInventoryResponse.getRecommendations().get(0).getCategory());
        assertEquals("Limited Stock Items", lowInventoryResponse.getRecommendations().get(0).getName());
        assertTrue(lowInventoryResponse.getAppliedRules().contains("Low Inventory Premium"));
    }

    @Test
    @DisplayName("Test combinations of multiple factors")
    void testCombinationsOfMultipleFactors() {
        // Arrange - Young male, new customer, summer season
        ProductRecommendationRequest youngMaleNewSummerRequest = createBasicRequest();
        youngMaleNewSummerRequest.setAge(25);
        youngMaleNewSummerRequest.setGender("M");
        youngMaleNewSummerRequest.setCurrentSeason("SUMMER");
        // No purchase history implies new customer

        // Arrange - Senior female, returning customer, winter season
        ProductRecommendationRequest seniorFemaleReturningWinterRequest = createBasicRequest();
        seniorFemaleReturningWinterRequest.setAge(70);
        seniorFemaleReturningWinterRequest.setGender("F");
        seniorFemaleReturningWinterRequest.setCurrentSeason("WINTER");
        Set<String> purchases = new HashSet<>();
        purchases.add("PROD-123");
        seniorFemaleReturningWinterRequest.setRecentlyPurchasedProducts(purchases);
        seniorFemaleReturningWinterRequest.setCurrentCategory("CLOTHING");

        // Act
        ProductRecommendationResponse youngMaleNewSummerResponse = productRecommendationService.getRecommendations(youngMaleNewSummerRequest);
        ProductRecommendationResponse seniorFemaleReturningWinterResponse = productRecommendationService.getRecommendations(seniorFemaleReturningWinterRequest);

        // Assert
        assertNotNull(youngMaleNewSummerResponse);
        assertFalse(youngMaleNewSummerResponse.getRecommendations().isEmpty());
        // The exact recommendation depends on the priority of the rules
        // but it should be one of the relevant rules for young males, new customers, or summer season

        assertNotNull(seniorFemaleReturningWinterResponse);
        assertFalse(seniorFemaleReturningWinterResponse.getRecommendations().isEmpty());
        // The exact recommendation depends on the priority of the rules
        // but it should be one of the relevant rules for seniors, returning customers, or winter season
    }

    @Test
    @DisplayName("Test error scenarios")
    void testErrorScenarios() {
        // Test null request
        Exception nullRequestException = assertThrows(IllegalArgumentException.class, () -> {
            productRecommendationService.getRecommendations(null);
        });
        assertTrue(nullRequestException.getMessage().contains("Request cannot be null"));

        // Test negative age
        ProductRecommendationRequest negativeAgeRequest = createBasicRequest();
        negativeAgeRequest.setAge(-25);
        Exception negativeAgeException = assertThrows(IllegalArgumentException.class, () -> {
            productRecommendationService.getRecommendations(negativeAgeRequest);
        });
        assertTrue(negativeAgeException.getMessage().contains("Age must be positive"));

        // Test invalid gender
        ProductRecommendationRequest invalidGenderRequest = createBasicRequest();
        invalidGenderRequest.setGender("X");
        Exception invalidGenderException = assertThrows(IllegalArgumentException.class, () -> {
            productRecommendationService.getRecommendations(invalidGenderRequest);
        });
        assertTrue(invalidGenderException.getMessage().contains("Gender must be 'M' or 'F'"));

        // Test invalid season
        ProductRecommendationRequest invalidSeasonRequest = createBasicRequest();
        invalidSeasonRequest.setCurrentSeason("SPRING");
        Exception invalidSeasonException = assertThrows(IllegalArgumentException.class, () -> {
            productRecommendationService.getRecommendations(invalidSeasonRequest);
        });
        assertTrue(invalidSeasonException.getMessage().contains("Season must be 'SUMMER' or 'WINTER'"));
    }

    /**
     * Helper method to create a basic recommendation request with default values
     */
    private ProductRecommendationRequest createBasicRequest() {
        ProductRecommendationRequest request = new ProductRecommendationRequest();
        request.setCustomerId(123L);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(35);
        request.setGender("M");
        request.setMaxRecommendations(5);
        request.setRecommendationType("PERSONALIZED");
        return request;
    }
}