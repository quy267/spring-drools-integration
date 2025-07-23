package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CustomerMapper.
 * Tests all mapping scenarios and edge cases for customer discount mappings.
 */
@DisplayName("CustomerMapper Tests")
class CustomerMapperTest {

    private CustomerMapper customerMapper;

    @BeforeEach
    void setUp() {
        customerMapper = Mappers.getMapper(CustomerMapper.class);
    }

    @Test
    @DisplayName("Should map CustomerDiscountRequest to Customer correctly")
    void shouldMapRequestToCustomer() {
        // Given
        CustomerDiscountRequest request = createSampleCustomerDiscountRequest();

        // When
        Customer customer = customerMapper.requestToCustomer(request);

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(request.getCustomerId());
        assertThat(customer.getName()).isEqualTo(request.getCustomerName());
        assertThat(customer.getAge()).isEqualTo(request.getCustomerAge());
        assertThat(customer.getEmail()).isEqualTo(request.getCustomerEmail());
    }

    @Test
    @DisplayName("Should map Customer to CustomerDiscountResponse correctly")
    void shouldMapCustomerToResponse() {
        // Given
        Customer customer = createSampleCustomer();

        // When
        CustomerDiscountResponse response = customerMapper.customerToResponse(customer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(customer.getId());
        assertThat(response.getCustomerName()).isEqualTo(customer.getName());
        assertThat(response.getTimestamp()).isNotNull();
        
        // Verify ignored properties are null/default
        assertThat(response.getOriginalAmount()).isEqualTo(0.0);
        assertThat(response.getDiscountPercentage()).isEqualTo(0.0);
        assertThat(response.getDiscountAmount()).isEqualTo(0.0);
        assertThat(response.getFinalAmount()).isEqualTo(0.0);
        assertThat(response.getAppliedRules()).isNull();
        assertThat(response.getOrderId()).isNull();
        assertThat(response.getNotes()).isNull();
    }

    @Test
    @DisplayName("Should map CustomerDiscountRequest to Order correctly")
    void shouldMapRequestToOrder() {
        // Given
        CustomerDiscountRequest request = createSampleCustomerDiscountRequest();

        // When
        Order order = customerMapper.requestToOrder(request);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNull(); // Should be ignored
        assertThat(order.getCustomer()).isNull(); // Should be ignored
        assertThat(order.getAmount()).isEqualTo(request.getOrderAmount());
        assertThat(order.getVolume()).isEqualTo(request.getOrderQuantity());
        assertThat(order.getOrderDate()).isNotNull();
        assertThat(order.getStatus()).isEqualTo("PENDING");
        assertThat(order.getDiscountPercentage()).isEqualTo(0);
        assertThat(order.getFinalAmount()).isEqualTo(request.getOrderAmount());
        assertThat(order.getItems()).hasSize(request.getOrderItems().size());
        
        // Verify ignored properties
        assertThat(order.getShippingAddress()).isNull();
        assertThat(order.getPaymentMethod()).isNull();
        assertThat(order.getNotes()).isNull();
    }

    @Test
    @DisplayName("Should map order items correctly")
    void shouldMapOrderItems() {
        // Given
        List<CustomerDiscountRequest.OrderItemRequest> orderItemRequests = Arrays.asList(
                createOrderItemRequest("ITEM001", "Product 1", 2, 25.99),
                createOrderItemRequest("ITEM002", "Product 2", 1, 49.99)
        );

        // When
        List<Order.OrderItem> orderItems = customerMapper.mapOrderItems(orderItemRequests);

        // Then
        assertThat(orderItems).hasSize(2);
        
        Order.OrderItem firstItem = orderItems.get(0);
        assertThat(firstItem.getProductId()).isEqualTo("ITEM001");
        assertThat(firstItem.getProductName()).isEqualTo("Product 1");
        assertThat(firstItem.getQuantity()).isEqualTo(2);
        assertThat(firstItem.getPrice()).isEqualTo(25.99);
        
        Order.OrderItem secondItem = orderItems.get(1);
        assertThat(secondItem.getProductId()).isEqualTo("ITEM002");
        assertThat(secondItem.getProductName()).isEqualTo("Product 2");
        assertThat(secondItem.getQuantity()).isEqualTo(1);
        assertThat(secondItem.getPrice()).isEqualTo(49.99);
    }

    @Test
    @DisplayName("Should map single order item correctly")
    void shouldMapOrderItem() {
        // Given
        CustomerDiscountRequest.OrderItemRequest orderItemRequest = 
                createOrderItemRequest("ITEM001", "Product 1", 3, 15.99);

        // When
        Order.OrderItem orderItem = customerMapper.mapOrderItem(orderItemRequest);

        // Then
        assertThat(orderItem).isNotNull();
        assertThat(orderItem.getProductId()).isEqualTo("ITEM001");
        assertThat(orderItem.getProductName()).isEqualTo("Product 1");
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        assertThat(orderItem.getPrice()).isEqualTo(15.99);
    }

    @Test
    @DisplayName("Should update order with discount correctly")
    void shouldUpdateOrderWithDiscount() {
        // Given
        Order order = createSampleOrder();
        double discountPercentage = 15.0;
        double finalAmount = 85.0;

        // When
        customerMapper.updateOrderWithDiscount(order, discountPercentage, finalAmount);

        // Then
        assertThat(order.getDiscountPercentage()).isEqualTo(discountPercentage);
        assertThat(order.getFinalAmount()).isEqualTo(finalAmount);
    }

    @Test
    @DisplayName("Should update response with order correctly")
    void shouldUpdateResponseWithOrder() {
        // Given
        CustomerDiscountResponse response = new CustomerDiscountResponse();
        Order order = createSampleOrder();
        order.setDiscountPercentage(10.0);
        order.setFinalAmount(90.0);

        // When
        customerMapper.updateResponseWithOrder(response, order);

        // Then
        assertThat(response.getOrderId()).isEqualTo(order.getId());
        assertThat(response.getOriginalAmount()).isEqualTo(order.getAmount());
        assertThat(response.getDiscountPercentage()).isEqualTo(order.getDiscountPercentage());
        assertThat(response.getFinalAmount()).isEqualTo(order.getFinalAmount());
        assertThat(response.getDiscountAmount()).isEqualTo(order.getAmount() - order.getFinalAmount());
    }

    @Test
    @DisplayName("Should parse order date correctly")
    void shouldParseOrderDate() {
        // Given
        String orderDateString = "2023-12-25";

        // When
        LocalDateTime parsedDate = customerMapper.parseOrderDate(orderDateString);

        // Then
        assertThat(parsedDate).isNotNull();
        assertThat(parsedDate.getYear()).isEqualTo(2023);
        assertThat(parsedDate.getMonthValue()).isEqualTo(12);
        assertThat(parsedDate.getDayOfMonth()).isEqualTo(25);
        assertThat(parsedDate.getHour()).isEqualTo(0);
        assertThat(parsedDate.getMinute()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle null order date gracefully")
    void shouldHandleNullOrderDate() {
        // When
        LocalDateTime parsedDate = customerMapper.parseOrderDate(null);

        // Then
        assertThat(parsedDate).isNotNull();
        assertThat(parsedDate).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle empty order date gracefully")
    void shouldHandleEmptyOrderDate() {
        // When
        LocalDateTime parsedDate = customerMapper.parseOrderDate("");

        // Then
        assertThat(parsedDate).isNotNull();
        assertThat(parsedDate).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should handle invalid order date gracefully")
    void shouldHandleInvalidOrderDate() {
        // When
        LocalDateTime parsedDate = customerMapper.parseOrderDate("invalid-date");

        // Then
        assertThat(parsedDate).isNotNull();
        assertThat(parsedDate).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create response from customer and order correctly")
    void shouldCreateResponse() {
        // Given
        Customer customer = createSampleCustomer();
        Order order = createSampleOrder();
        order.setDiscountPercentage(20.0);
        order.setFinalAmount(80.0);
        String appliedRules = "AGE_DISCOUNT,LOYALTY_DISCOUNT";

        // When
        CustomerDiscountResponse response = customerMapper.createResponse(customer, order, appliedRules);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(customer.getId());
        assertThat(response.getCustomerName()).isEqualTo(customer.getName());
        assertThat(response.getOrderId()).isEqualTo(order.getId());
        assertThat(response.getOriginalAmount()).isEqualTo(order.getAmount());
        assertThat(response.getDiscountPercentage()).isEqualTo(order.getDiscountPercentage());
        assertThat(response.getFinalAmount()).isEqualTo(order.getFinalAmount());
        assertThat(response.getDiscountAmount()).isEqualTo(order.getAmount() - order.getFinalAmount());
        assertThat(response.getAppliedRules()).isEqualTo(appliedRules);
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void shouldHandleNullRequest() {
        // When & Then
        Customer customer = customerMapper.requestToCustomer(null);
        assertThat(customer).isNull();
        
        Order order = customerMapper.requestToOrder(null);
        assertThat(order).isNull();
    }

    @Test
    @DisplayName("Should handle null order items gracefully")
    void shouldHandleNullOrderItems() {
        // When
        List<Order.OrderItem> orderItems = customerMapper.mapOrderItems(null);

        // Then
        assertThat(orderItems).isNull();
    }

    @Test
    @DisplayName("Should handle null parameters in utility methods gracefully")
    void shouldHandleNullParametersInUtilityMethods() {
        // Given
        CustomerDiscountResponse response = new CustomerDiscountResponse();
        Order order = createSampleOrder();

        // When & Then - should not throw exceptions
        customerMapper.updateOrderWithDiscount(null, 10.0, 90.0);
        customerMapper.updateResponseWithOrder(null, order);
        customerMapper.updateResponseWithOrder(response, null);
        
        // Response should remain unchanged when order is null
        assertThat(response.getOrderId()).isNull();
        assertThat(response.getOriginalAmount()).isEqualTo(0.0);
    }

    private CustomerDiscountRequest createSampleCustomerDiscountRequest() {
        CustomerDiscountRequest request = new CustomerDiscountRequest();
        request.setCustomerId(1001L);
        request.setCustomerName("John Doe");
        request.setCustomerAge(30);
        request.setCustomerEmail("john.doe@example.com");
        request.setOrderAmount(100.0);
        request.setOrderQuantity(2);
        request.setOrderDate("2023-12-25");
        
        List<CustomerDiscountRequest.OrderItemRequest> orderItems = Arrays.asList(
                createOrderItemRequest("ITEM001", "Product 1", 1, 50.0),
                createOrderItemRequest("ITEM002", "Product 2", 1, 50.0)
        );
        request.setOrderItems(orderItems);
        
        return request;
    }

    private CustomerDiscountRequest.OrderItemRequest createOrderItemRequest(
            String productId, String productName, int quantity, double price) {
        CustomerDiscountRequest.OrderItemRequest orderItem = new CustomerDiscountRequest.OrderItemRequest();
        orderItem.setProductId(productId);
        orderItem.setProductName(productName);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        return orderItem;
    }

    private Customer createSampleCustomer() {
        Customer customer = new Customer();
        customer.setId(1001L);
        customer.setName("John Doe");
        customer.setAge(30);
        customer.setEmail("john.doe@example.com");
        return customer;
    }

    private Order createSampleOrder() {
        Order order = new Order();
        order.setId("ORDER-2001");
        order.setAmount(100.0);
        order.setVolume(2);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setDiscountPercentage(0);
        order.setFinalAmount(100.0);
        return order;
    }
}