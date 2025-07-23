package com.example.springdroolsintegration.service.impl;

import com.example.springdroolsintegration.exception.RuleExecutionException;
import com.example.springdroolsintegration.mapper.CustomerMapper;
import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.DiscountRule;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import com.example.springdroolsintegration.service.RuleExecutionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for CustomerDiscountServiceImpl.
 * Tests cover age-based discounts, loyalty tiers, order amounts, edge cases, and error handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerDiscountService Tests")
class CustomerDiscountServiceImplTest {

    @Mock
    private RuleExecutionService ruleExecutionService;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerDiscountServiceImpl customerDiscountService;

    private CustomerDiscountRequest baseRequest;
    private Customer baseCustomer;
    private Order baseOrder;
    private DiscountRule baseDiscountRule;

    @BeforeEach
    void setUp() {
        // Set up base test data
        baseRequest = new CustomerDiscountRequest();
        baseRequest.setCustomerId(1L);
        baseRequest.setCustomerName("John Doe");
        baseRequest.setCustomerAge(30);
        baseRequest.setLoyaltyTier("SILVER");
        baseRequest.setOrderAmount(200.0);
        baseRequest.setOrderQuantity(2);

        baseCustomer = new Customer();
        baseCustomer.setId(1L);
        baseCustomer.setName("John Doe");
        baseCustomer.setAge(30);
        baseCustomer.setLoyaltyTier("SILVER");

        baseOrder = new Order();
        baseOrder.setId("ORDER-001");
        baseOrder.setAmount(200.0);
        baseOrder.setVolume(2);
        baseOrder.setCustomer(baseCustomer);

        baseDiscountRule = new DiscountRule();
        baseDiscountRule.setId(1L);
        baseDiscountRule.setName("Silver Loyalty Discount");
        baseDiscountRule.setDiscountPercentage(10.0);
        baseDiscountRule.setPriority(1);
    }

    @Nested
    @DisplayName("Age-based Discount Rules Tests")
    class AgeBasedDiscountTests {

        @Nested
        @DisplayName("Child Discount Tests (age < 18)")
        class ChildDiscountTests {

            @Test
            @DisplayName("Should apply child discount for age 5")
            void shouldApplyChildDiscountForAge5() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(5);
                Customer customer = createCustomerWithAge(5);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Child Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getCustomerName()).isEqualTo("Child Customer");
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(15.0);
                assertThat(response.getFinalAmount()).isEqualTo(85.0);
                assertThat(response.getAppliedRules()).isEqualTo("Child Discount");

                verify(ruleExecutionService, times(3)).executeRules(any());
            }

            @Test
            @DisplayName("Should apply child discount for age 10")
            void shouldApplyChildDiscountForAge10() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(10);
                Customer customer = createCustomerWithAge(10);
                Order order = createOrderWithAmount(150.0);
                DiscountRule discountRule = createDiscountRule("Child Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(22.5);
                assertThat(response.getFinalAmount()).isEqualTo(127.5);
            }

            @Test
            @DisplayName("Should apply child discount for age 17")
            void shouldApplyChildDiscountForAge17() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(17);
                Customer customer = createCustomerWithAge(17);
                Order order = createOrderWithAmount(200.0);
                DiscountRule discountRule = createDiscountRule("Child Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(30.0);
                assertThat(response.getFinalAmount()).isEqualTo(170.0);
            }
        }

        @Nested
        @DisplayName("Adult Discount Tests (age 18-64)")
        class AdultDiscountTests {

            @Test
            @DisplayName("Should apply adult discount for age 18")
            void shouldApplyAdultDiscountForAge18() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(18);
                Customer customer = createCustomerWithAge(18);
                Order order = createOrderWithAmount(300.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(30.0);
                assertThat(response.getFinalAmount()).isEqualTo(270.0);
            }

            @Test
            @DisplayName("Should apply adult discount for age 25")
            void shouldApplyAdultDiscountForAge25() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(25);
                Customer customer = createCustomerWithAge(25);
                Order order = createOrderWithAmount(250.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(25.0);
                assertThat(response.getFinalAmount()).isEqualTo(225.0);
            }

            @Test
            @DisplayName("Should apply adult discount for age 35")
            void shouldApplyAdultDiscountForAge35() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(35);
                Customer customer = createCustomerWithAge(35);
                Order order = createOrderWithAmount(400.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(40.0);
                assertThat(response.getFinalAmount()).isEqualTo(360.0);
            }

            @Test
            @DisplayName("Should apply adult discount for age 50")
            void shouldApplyAdultDiscountForAge50() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(50);
                Customer customer = createCustomerWithAge(50);
                Order order = createOrderWithAmount(500.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(50.0);
                assertThat(response.getFinalAmount()).isEqualTo(450.0);
            }

            @Test
            @DisplayName("Should apply adult discount for age 64")
            void shouldApplyAdultDiscountForAge64() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(64);
                Customer customer = createCustomerWithAge(64);
                Order order = createOrderWithAmount(350.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(35.0);
                assertThat(response.getFinalAmount()).isEqualTo(315.0);
            }
        }

        @Nested
        @DisplayName("Senior Discount Tests (age >= 65)")
        class SeniorDiscountTests {

            @Test
            @DisplayName("Should apply senior discount for age 65")
            void shouldApplySeniorDiscountForAge65() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(65);
                Customer customer = createCustomerWithAge(65);
                Order order = createOrderWithAmount(300.0);
                DiscountRule discountRule = createDiscountRule("Senior Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getDiscountAmount()).isEqualTo(60.0);
                assertThat(response.getFinalAmount()).isEqualTo(240.0);
            }

            @Test
            @DisplayName("Should apply senior discount for age 70")
            void shouldApplySeniorDiscountForAge70() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(70);
                Customer customer = createCustomerWithAge(70);
                Order order = createOrderWithAmount(400.0);
                DiscountRule discountRule = createDiscountRule("Senior Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getDiscountAmount()).isEqualTo(80.0);
                assertThat(response.getFinalAmount()).isEqualTo(320.0);
            }

            @Test
            @DisplayName("Should apply senior discount for age 80")
            void shouldApplySeniorDiscountForAge80() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(80);
                Customer customer = createCustomerWithAge(80);
                Order order = createOrderWithAmount(250.0);
                DiscountRule discountRule = createDiscountRule("Senior Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getDiscountAmount()).isEqualTo(50.0);
                assertThat(response.getFinalAmount()).isEqualTo(200.0);
            }
        }

        @Nested
        @DisplayName("Boundary Condition Tests")
        class BoundaryConditionTests {

            @Test
            @DisplayName("Should apply child discount for boundary age 17")
            void shouldApplyChildDiscountForBoundaryAge17() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(17);
                Customer customer = createCustomerWithAge(17);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Child Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getAppliedRules()).isEqualTo("Child Discount");
            }

            @Test
            @DisplayName("Should apply adult discount for boundary age 18")
            void shouldApplyAdultDiscountForBoundaryAge18() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(18);
                Customer customer = createCustomerWithAge(18);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getAppliedRules()).isEqualTo("Adult Discount");
            }

            @Test
            @DisplayName("Should apply adult discount for boundary age 64")
            void shouldApplyAdultDiscountForBoundaryAge64() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(64);
                Customer customer = createCustomerWithAge(64);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Adult Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getAppliedRules()).isEqualTo("Adult Discount");
            }

            @Test
            @DisplayName("Should apply senior discount for boundary age 65")
            void shouldApplySeniorDiscountForBoundaryAge65() {
                // Given
                CustomerDiscountRequest request = createRequestWithAge(65);
                Customer customer = createCustomerWithAge(65);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Senior Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getAppliedRules()).isEqualTo("Senior Discount");
            }
        }
    }

    @Nested
    @DisplayName("Loyalty Tier Benefits Tests")
    class LoyaltyTierBenefitsTests {

        @Nested
        @DisplayName("BRONZE Tier Tests")
        class BronzeTierTests {

            @Test
            @DisplayName("Should apply BRONZE tier discount calculations")
            void shouldApplyBronzeTierDiscount() {
                // Given
                CustomerDiscountRequest request = createRequestWithLoyaltyTier("BRONZE", 200.0);
                Customer customer = createCustomerWithLoyaltyTier("BRONZE");
                Order order = createOrderWithAmount(200.0);
                DiscountRule discountRule = createDiscountRule("Bronze Loyalty Discount", 5.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getLoyaltyTier()).isEqualTo("BRONZE");
                assertThat(response.getDiscountPercentage()).isEqualTo(5.0);
                assertThat(response.getDiscountAmount()).isEqualTo(10.0);
                assertThat(response.getFinalAmount()).isEqualTo(190.0);
                assertThat(response.getAppliedRules()).isEqualTo("Bronze Loyalty Discount");

                verify(ruleExecutionService, times(3)).executeRules(any());
            }
        }

        @Nested
        @DisplayName("SILVER Tier Tests")
        class SilverTierTests {

            @Test
            @DisplayName("Should apply SILVER tier discount calculations")
            void shouldApplySilverTierDiscount() {
                // Given
                CustomerDiscountRequest request = createRequestWithLoyaltyTier("SILVER", 300.0);
                Customer customer = createCustomerWithLoyaltyTier("SILVER");
                Order order = createOrderWithAmount(300.0);
                DiscountRule discountRule = createDiscountRule("Silver Loyalty Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getLoyaltyTier()).isEqualTo("SILVER");
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(30.0);
                assertThat(response.getFinalAmount()).isEqualTo(270.0);
                assertThat(response.getAppliedRules()).isEqualTo("Silver Loyalty Discount");
            }
        }

        @Nested
        @DisplayName("GOLD Tier Tests")
        class GoldTierTests {

            @Test
            @DisplayName("Should apply GOLD tier discount calculations")
            void shouldApplyGoldTierDiscount() {
                // Given
                CustomerDiscountRequest request = createRequestWithLoyaltyTier("GOLD", 400.0);
                Customer customer = createCustomerWithLoyaltyTier("GOLD");
                Order order = createOrderWithAmount(400.0);
                DiscountRule discountRule = createDiscountRule("Gold Loyalty Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getLoyaltyTier()).isEqualTo("GOLD");
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(60.0);
                assertThat(response.getFinalAmount()).isEqualTo(340.0);
                assertThat(response.getAppliedRules()).isEqualTo("Gold Loyalty Discount");
            }
        }

        @Nested
        @DisplayName("PLATINUM Tier Tests")
        class PlatinumTierTests {

            @Test
            @DisplayName("Should apply PLATINUM tier discount calculations")
            void shouldApplyPlatinumTierDiscount() {
                // Given
                CustomerDiscountRequest request = createRequestWithLoyaltyTier("PLATINUM", 500.0);
                Customer customer = createCustomerWithLoyaltyTier("PLATINUM");
                Order order = createOrderWithAmount(500.0);
                DiscountRule discountRule = createDiscountRule("Platinum Loyalty Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getLoyaltyTier()).isEqualTo("PLATINUM");
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getDiscountAmount()).isEqualTo(100.0);
                assertThat(response.getFinalAmount()).isEqualTo(400.0);
                assertThat(response.getAppliedRules()).isEqualTo("Platinum Loyalty Discount");
            }
        }
    }

    @Nested
    @DisplayName("Order Amount Thresholds and Bulk Discounts Tests")
    class OrderAmountThresholdTests {

        @Nested
        @DisplayName("Small Order Tests (<$100)")
        class SmallOrderTests {

            @Test
            @DisplayName("Should handle small order amounts correctly")
            void shouldHandleSmallOrderAmounts() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(50.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(50.0);
                DiscountRule discountRule = createDiscountRule("Small Order Discount", 5.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(50.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(5.0);
                assertThat(response.getDiscountAmount()).isEqualTo(2.5);
                assertThat(response.getFinalAmount()).isEqualTo(47.5);
            }

            @Test
            @DisplayName("Should handle order amount of $99")
            void shouldHandleOrderAmountOf99() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(99.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(99.0);
                DiscountRule discountRule = createDiscountRule("Small Order Discount", 5.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(99.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(5.0);
                assertThat(response.getDiscountAmount()).isEqualTo(4.95);
                assertThat(response.getFinalAmount()).isEqualTo(94.05);
            }
        }

        @Nested
        @DisplayName("Medium Order Tests ($100-$500)")
        class MediumOrderTests {

            @Test
            @DisplayName("Should handle medium order amount of $100")
            void shouldHandleMediumOrderAmountOf100() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(100.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(100.0);
                DiscountRule discountRule = createDiscountRule("Medium Order Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(100.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(10.0);
                assertThat(response.getFinalAmount()).isEqualTo(90.0);
            }

            @Test
            @DisplayName("Should handle medium order amount of $300")
            void shouldHandleMediumOrderAmountOf300() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(300.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(300.0);
                DiscountRule discountRule = createDiscountRule("Medium Order Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(300.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(30.0);
                assertThat(response.getFinalAmount()).isEqualTo(270.0);
            }

            @Test
            @DisplayName("Should handle medium order amount of $500")
            void shouldHandleMediumOrderAmountOf500() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(500.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(500.0);
                DiscountRule discountRule = createDiscountRule("Medium Order Discount", 10.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(500.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(50.0);
                assertThat(response.getFinalAmount()).isEqualTo(450.0);
            }
        }

        @Nested
        @DisplayName("Large Order Tests (>$500)")
        class LargeOrderTests {

            @Test
            @DisplayName("Should handle large order amount of $501")
            void shouldHandleLargeOrderAmountOf501() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(501.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(501.0);
                DiscountRule discountRule = createDiscountRule("Large Order Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(501.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(75.15);
                assertThat(response.getFinalAmount()).isEqualTo(425.85);
            }

            @Test
            @DisplayName("Should handle large order amount of $1000")
            void shouldHandleLargeOrderAmountOf1000() {
                // Given
                CustomerDiscountRequest request = createRequestWithOrderAmount(1000.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithAmount(1000.0);
                DiscountRule discountRule = createDiscountRule("Large Order Discount", 15.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(1000.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(15.0);
                assertThat(response.getDiscountAmount()).isEqualTo(150.0);
                assertThat(response.getFinalAmount()).isEqualTo(850.0);
            }
        }

        @Nested
        @DisplayName("Bulk Discount Threshold Tests")
        class BulkDiscountTests {

            @Test
            @DisplayName("Should apply bulk discount for high volume orders")
            void shouldApplyBulkDiscountForHighVolumeOrders() {
                // Given
                CustomerDiscountRequest request = createRequestWithVolumeAndAmount(20, 1000.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithVolumeAndAmount(20, 1000.0);
                DiscountRule discountRule = createDiscountRule("Bulk Order Discount", 25.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(1000.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(25.0);
                assertThat(response.getDiscountAmount()).isEqualTo(250.0);
                assertThat(response.getFinalAmount()).isEqualTo(750.0);
                assertThat(response.getAppliedRules()).isEqualTo("Bulk Order Discount");
            }

            @Test
            @DisplayName("Should apply bulk discount threshold correctly")
            void shouldApplyBulkDiscountThresholdCorrectly() {
                // Given
                CustomerDiscountRequest request = createRequestWithVolumeAndAmount(10, 500.0);
                Customer customer = createCustomerWithAge(30);
                Order order = createOrderWithVolumeAndAmount(10, 500.0);
                DiscountRule discountRule = createDiscountRule("Bulk Threshold Discount", 20.0);

                when(customerMapper.requestToCustomer(request)).thenReturn(customer);
                when(customerMapper.requestToOrder(request)).thenReturn(order);

                // When
                CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(500.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
                assertThat(response.getDiscountAmount()).isEqualTo(100.0);
                assertThat(response.getFinalAmount()).isEqualTo(400.0);
            }
        }
    }

    // Helper methods for creating test data
    private CustomerDiscountRequest createRequestWithAge(int age) {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerId(1L);
        request.setCustomerName(getCustomerNameByAge(age));
        request.setCustomerAge(age);
        request.setLoyaltyTier("BRONZE");
        request.setOrderAmount(100.0);
        request.setOrderQuantity(1);
        return request;
    }

    private Customer createCustomerWithAge(int age) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName(getCustomerNameByAge(age));
        customer.setAge(age);
        customer.setLoyaltyTier("BRONZE");
        return customer;
    }

    private Order createOrderWithAmount(double amount) {
        Order order = new Order();
        order.setId("ORDER-001");
        order.setAmount(amount);
        order.setVolume(1);
        return order;
    }

    private DiscountRule createDiscountRule(String name, double percentage) {
        DiscountRule rule = new DiscountRule();
        rule.setId(1L);
        rule.setName(name);
        rule.setDiscountPercentage(percentage);
        rule.setPriority(1);
        return rule;
    }

    private CustomerDiscountRequest createRequestWithLoyaltyTier(String loyaltyTier, double orderAmount) {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerId(1L);
        request.setCustomerName("Loyalty Customer");
        request.setCustomerAge(30);
        request.setLoyaltyTier(loyaltyTier);
        request.setOrderAmount(orderAmount);
        request.setOrderQuantity(1);
        return request;
    }

    private Customer createCustomerWithLoyaltyTier(String loyaltyTier) {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Loyalty Customer");
        customer.setAge(30);
        customer.setLoyaltyTier(loyaltyTier);
        return customer;
    }

    private CustomerDiscountRequest createRequestWithOrderAmount(double orderAmount) {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerId(1L);
        request.setCustomerName("Order Customer");
        request.setCustomerAge(30);
        request.setLoyaltyTier("BRONZE");
        request.setOrderAmount(orderAmount);
        request.setOrderQuantity(1);
        return request;
    }

    private CustomerDiscountRequest createRequestWithVolumeAndAmount(int volume, double orderAmount) {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerId(1L);
        request.setCustomerName("Bulk Customer");
        request.setCustomerAge(30);
        request.setLoyaltyTier("BRONZE");
        request.setOrderAmount(orderAmount);
        request.setOrderQuantity(volume);
        return request;
    }

    private Order createOrderWithVolumeAndAmount(int volume, double amount) {
        Order order = new Order();
        order.setId("ORDER-001");
        order.setAmount(amount);
        order.setVolume(volume);
        return order;
    }

    private String getCustomerNameByAge(int age) {
        if (age < 18) return "Child Customer";
        if (age >= 65) return "Senior Customer";
        return "Adult Customer";
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions Tests")
    class EdgeCasesAndBoundaryConditionsTests {

        @Test
        @DisplayName("Should handle null customer input")
        void shouldHandleNullCustomerInput() {
            // Given
            CustomerDiscountRequest request = null;

            // When & Then
            assertThatThrownBy(() -> customerDiscountService.calculateDiscount(request))
                    .isInstanceOf(RuleExecutionException.class)
                    .hasMessage("Cannot calculate discount for null request");

            verify(ruleExecutionService, never()).executeRules(any());
        }

        @Test
        @DisplayName("Should handle invalid age values - negative age")
        void shouldHandleInvalidNegativeAge() {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(-5);
            Customer customer = createCustomerWithAge(-5);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("No Discount", 0.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // When
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getDiscountPercentage()).isEqualTo(0.0);
            assertThat(response.getDiscountAmount()).isEqualTo(0.0);
            assertThat(response.getFinalAmount()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("Should handle invalid age values - extremely high age")
        void shouldHandleInvalidExtremelyHighAge() {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(150);
            Customer customer = createCustomerWithAge(150);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Senior Discount", 20.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // When
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getDiscountPercentage()).isEqualTo(20.0);
            assertThat(response.getDiscountAmount()).isEqualTo(20.0);
            assertThat(response.getFinalAmount()).isEqualTo(80.0);
        }

        @Test
        @DisplayName("Should handle zero order amounts")
        void shouldHandleZeroOrderAmounts() {
            // Given
            CustomerDiscountRequest request = createRequestWithOrderAmount(0.0);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(0.0);
            DiscountRule discountRule = createDiscountRule("No Discount", 0.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // When
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getOriginalAmount()).isEqualTo(0.0);
            assertThat(response.getDiscountAmount()).isEqualTo(0.0);
            assertThat(response.getFinalAmount()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Should handle negative order amounts")
        void shouldHandleNegativeOrderAmounts() {
            // Given
            CustomerDiscountRequest request = createRequestWithOrderAmount(-100.0);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(-100.0);
            DiscountRule discountRule = createDiscountRule("No Discount", 0.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // When
            CustomerDiscountResponse response = customerDiscountService.calculateDiscount(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getOriginalAmount()).isEqualTo(-100.0);
            assertThat(response.getDiscountAmount()).isEqualTo(0.0);
            assertThat(response.getFinalAmount()).isEqualTo(-100.0);
        }
    }

    @Nested
    @DisplayName("Error Handling for Invalid Inputs Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle exception from rule execution service")
        void shouldHandleExceptionFromRuleExecutionService() {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(30);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);
            when(ruleExecutionService.executeRules(any())).thenThrow(new RuntimeException("Rule execution failed"));

            // When & Then
            assertThatThrownBy(() -> customerDiscountService.calculateDiscount(request))
                    .isInstanceOf(RuleExecutionException.class)
                    .hasMessage("Error calculating discount: Rule execution failed")
                    .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle exception from customer mapper")
        void shouldHandleExceptionFromCustomerMapper() {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(30);

            when(customerMapper.requestToCustomer(request)).thenThrow(new RuntimeException("Mapping failed"));

            // When & Then
            assertThatThrownBy(() -> customerDiscountService.calculateDiscount(request))
                    .isInstanceOf(RuleExecutionException.class)
                    .hasMessage("Error calculating discount: Mapping failed")
                    .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should handle null or empty requests list for batch processing")
        void shouldHandleNullOrEmptyRequestsListForBatchProcessing() {
            // When & Then - null list
            assertThatThrownBy(() -> customerDiscountService.calculateDiscountBatch(null))
                    .isInstanceOf(RuleExecutionException.class)
                    .hasMessage("Cannot calculate discounts for null or empty requests list");

            // When & Then - empty list
            assertThatThrownBy(() -> customerDiscountService.calculateDiscountBatch(Arrays.asList()))
                    .isInstanceOf(RuleExecutionException.class)
                    .hasMessage("Cannot calculate discounts for null or empty requests list");
        }

        @Test
        @DisplayName("Should handle validation error responses")
        void shouldHandleValidationErrorResponses() {
            // Given
            CustomerDiscountRequest request = new CustomerDiscountRequest();
            // Intentionally leave required fields null to trigger validation

            // When & Then
            assertThatThrownBy(() -> customerDiscountService.calculateDiscount(request))
                    .isInstanceOf(RuleExecutionException.class);
        }
    }

    @Nested
    @DisplayName("Concurrent Execution Scenarios Tests")
    class ConcurrentExecutionTests {

        @Test
        @DisplayName("Should handle thread safety with concurrent requests")
        void shouldHandleThreadSafetyWithConcurrentRequests() throws Exception {
            // Given
            int numberOfThreads = 10;
            int requestsPerThread = 5;
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            List<CompletableFuture<CustomerDiscountResponse>> futures = new ArrayList<>();

            CustomerDiscountRequest request = createRequestWithAge(30);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Concurrent Test Discount", 10.0);

            when(customerMapper.requestToCustomer(any())).thenReturn(customer);
            when(customerMapper.requestToOrder(any())).thenReturn(order);

            // When
            for (int i = 0; i < numberOfThreads * requestsPerThread; i++) {
                CompletableFuture<CustomerDiscountResponse> future = CompletableFuture
                        .supplyAsync(() -> customerDiscountService.calculateDiscount(request), executorService);
                futures.add(future);
            }

            // Wait for all futures to complete
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));
            allFutures.get(10, TimeUnit.SECONDS);

            // Then
            for (CompletableFuture<CustomerDiscountResponse> future : futures) {
                CustomerDiscountResponse response = future.get();
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(100.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(10.0);
                assertThat(response.getFinalAmount()).isEqualTo(90.0);
            }

            executorService.shutdown();
            assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }

        @Test
        @DisplayName("Should handle performance under load")
        void shouldHandlePerformanceUnderLoad() {
            // Given
            int numberOfRequests = 100;
            List<CustomerDiscountRequest> requests = new ArrayList<>();
            
            for (int i = 0; i < numberOfRequests; i++) {
                requests.add(createRequestWithAge(25 + (i % 40))); // Ages 25-64
            }

            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Performance Test Discount", 10.0);

            when(customerMapper.requestToCustomer(any())).thenReturn(customer);
            when(customerMapper.requestToOrder(any())).thenReturn(order);

            // When
            long startTime = System.currentTimeMillis();
            List<CustomerDiscountResponse> responses = customerDiscountService.calculateDiscountBatch(requests);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Then
            assertThat(responses).hasSize(numberOfRequests);
            assertThat(executionTime).isLessThan(5000); // Should complete within 5 seconds
            
            for (CustomerDiscountResponse response : responses) {
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(100.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(10.0);
                assertThat(response.getFinalAmount()).isEqualTo(90.0);
            }

            System.out.println("[DEBUG_LOG] Performance test completed in " + executionTime + "ms for " + numberOfRequests + " requests");
        }
    }

    @Nested
    @DisplayName("Additional Service Method Tests")
    class AdditionalServiceMethodTests {

        @Test
        @DisplayName("Should handle async discount calculation")
        void shouldHandleAsyncDiscountCalculation() throws Exception {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(30);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Async Test Discount", 10.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // When
            CompletableFuture<CustomerDiscountResponse> future = customerDiscountService.calculateDiscountAsync(request);
            CustomerDiscountResponse response = future.get(5, TimeUnit.SECONDS);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getOriginalAmount()).isEqualTo(100.0);
            assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
            assertThat(response.getDiscountAmount()).isEqualTo(10.0);
            assertThat(response.getFinalAmount()).isEqualTo(90.0);
        }

        @Test
        @DisplayName("Should return discount statistics")
        void shouldReturnDiscountStatistics() {
            // Given
            CustomerDiscountRequest request = createRequestWithAge(30);
            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Statistics Test Discount", 10.0);

            when(customerMapper.requestToCustomer(request)).thenReturn(customer);
            when(customerMapper.requestToOrder(request)).thenReturn(order);

            // Execute some discount calculations to generate statistics
            customerDiscountService.calculateDiscount(request);
            customerDiscountService.calculateDiscount(request);

            // When
            Map<String, Object> statistics = customerDiscountService.getDiscountStatistics();

            // Then
            assertThat(statistics).isNotNull();
            assertThat(statistics).containsKey("totalDiscountCalculations");
            assertThat(statistics).containsKey("totalBatchCalculations");
            assertThat(statistics).containsKey("discountRuleCounts");
            
            assertThat((Long) statistics.get("totalDiscountCalculations")).isGreaterThanOrEqualTo(2L);
            assertThat(statistics.get("discountRuleCounts")).isInstanceOf(Map.class);
        }

        @Test
        @DisplayName("Should handle batch discount calculation")
        void shouldHandleBatchDiscountCalculation() {
            // Given
            List<CustomerDiscountRequest> requests = Arrays.asList(
                    createRequestWithAge(25),
                    createRequestWithAge(35),
                    createRequestWithAge(70)
            );

            Customer customer = createCustomerWithAge(30);
            Order order = createOrderWithAmount(100.0);
            DiscountRule discountRule = createDiscountRule("Batch Test Discount", 10.0);

            when(customerMapper.requestToCustomer(any())).thenReturn(customer);
            when(customerMapper.requestToOrder(any())).thenReturn(order);

            // When
            List<CustomerDiscountResponse> responses = customerDiscountService.calculateDiscountBatch(requests);

            // Then
            assertThat(responses).hasSize(3);
            for (CustomerDiscountResponse response : responses) {
                assertThat(response).isNotNull();
                assertThat(response.getOriginalAmount()).isEqualTo(100.0);
                assertThat(response.getDiscountPercentage()).isEqualTo(10.0);
                assertThat(response.getDiscountAmount()).isEqualTo(10.0);
                assertThat(response.getFinalAmount()).isEqualTo(90.0);
            }
        }
    }
}