package com.example.springdroolsintegration.mapper;

import com.example.springdroolsintegration.model.dto.CustomerDiscountResponse;
import com.example.springdroolsintegration.model.entity.Customer;
import com.example.springdroolsintegration.model.entity.Order;
import com.example.springdroolsintegration.model.request.CustomerDiscountRequest;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * MapStruct mapper for converting between Customer entity and related DTOs.
 * This interface defines mapping methods for Customer, Order, and related objects.
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    
    /**
     * Converts a CustomerDiscountRequest to a Customer entity.
     * 
     * @param request The CustomerDiscountRequest DTO
     * @return The Customer entity
     */
    @Mapping(target = "id", source = "customerId")
    @Mapping(target = "name", source = "customerName")
    @Mapping(target = "age", source = "customerAge")
    @Mapping(target = "email", source = "customerEmail")
    Customer requestToCustomer(CustomerDiscountRequest request);
    
    /**
     * Converts a Customer entity to a CustomerDiscountResponse DTO.
     * 
     * @param customer The Customer entity
     * @return The CustomerDiscountResponse DTO
     */
    @Mapping(target = "customerId", source = "id")
    @Mapping(target = "customerName", source = "name")
    @Mapping(target = "originalAmount", ignore = true)
    @Mapping(target = "discountPercentage", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "finalAmount", ignore = true)
    @Mapping(target = "appliedRules", ignore = true)
    @Mapping(target = "discounts", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "notes", ignore = true)
    CustomerDiscountResponse customerToResponse(Customer customer);
    
    /**
     * Converts a CustomerDiscountRequest to an Order entity.
     * 
     * @param request The CustomerDiscountRequest DTO
     * @return The Order entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "amount", source = "orderAmount")
    @Mapping(target = "volume", source = "orderQuantity")
    @Mapping(target = "orderDate", expression = "java(parseOrderDate(request.getOrderDate()))")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "shippingAddress", ignore = true)
    @Mapping(target = "paymentMethod", ignore = true)
    @Mapping(target = "items", expression = "java(mapOrderItems(request.getOrderItems()))")
    @Mapping(target = "discountPercentage", constant = "0")
    @Mapping(target = "finalAmount", source = "orderAmount")
    @Mapping(target = "notes", ignore = true)
    Order requestToOrder(CustomerDiscountRequest request);
    
    /**
     * Converts a list of OrderItemRequest objects to a list of Order.OrderItem objects.
     * 
     * @param orderItems The list of OrderItemRequest objects
     * @return The list of Order.OrderItem objects
     */
    @IterableMapping(elementTargetType = Order.OrderItem.class)
    List<Order.OrderItem> mapOrderItems(List<CustomerDiscountRequest.OrderItemRequest> orderItems);
    
    /**
     * Converts an OrderItemRequest to an Order.OrderItem.
     * 
     * @param orderItem The OrderItemRequest
     * @return The Order.OrderItem
     */
    Order.OrderItem mapOrderItem(CustomerDiscountRequest.OrderItemRequest orderItem);
    
    /**
     * Updates an Order entity with discount information.
     * 
     * @param order The Order entity to update
     * @param discountPercentage The discount percentage to apply
     * @param finalAmount The final amount after discount
     */
    @Named("updateOrderWithDiscount")
    default void updateOrderWithDiscount(Order order, double discountPercentage, double finalAmount) {
        if (order != null) {
            order.setDiscountPercentage(discountPercentage);
            order.setFinalAmount(finalAmount);
        }
    }
    
    /**
     * Updates a CustomerDiscountResponse with order information.
     * 
     * @param response The CustomerDiscountResponse to update
     * @param order The Order entity
     */
    @Named("updateResponseWithOrder")
    default void updateResponseWithOrder(CustomerDiscountResponse response, Order order) {
        if (response != null && order != null) {
            response.setOrderId(order.getId());
            response.setOriginalAmount(order.getAmount());
            response.setDiscountPercentage(order.getDiscountPercentage());
            response.setFinalAmount(order.getFinalAmount());
            response.setDiscountAmount(order.getAmount() - order.getFinalAmount());
        }
    }
    
    /**
     * Parses an order date string to a LocalDateTime.
     * 
     * @param orderDate The order date string in ISO format (yyyy-MM-dd)
     * @return The parsed LocalDateTime, or current time if parsing fails
     */
    default LocalDateTime parseOrderDate(String orderDate) {
        if (orderDate == null || orderDate.isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            LocalDate date = LocalDate.parse(orderDate, DateTimeFormatter.ISO_DATE);
            return date.atStartOfDay();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    /**
     * Creates a CustomerDiscountResponse from a Customer and Order.
     * 
     * @param customer The Customer entity
     * @param order The Order entity
     * @param appliedRules The names of the applied rules
     * @return The CustomerDiscountResponse DTO
     */
    default CustomerDiscountResponse createResponse(Customer customer, Order order, String appliedRules) {
        CustomerDiscountResponse response = customerToResponse(customer);
        
        if (order != null) {
            response.setOrderId(order.getId());
            response.setOriginalAmount(order.getAmount());
            response.setDiscountPercentage(order.getDiscountPercentage());
            response.setFinalAmount(order.getFinalAmount());
            response.setDiscountAmount(order.getAmount() - order.getFinalAmount());
        }
        
        response.setAppliedRules(appliedRules);
        return response;
    }
}