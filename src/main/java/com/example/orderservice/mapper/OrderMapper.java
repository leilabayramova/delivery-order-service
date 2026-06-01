package com.example.orderservice.mapper;

import com.example.orderservice.client.dto.CourierResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    public OrderEntity toEntity(CreateOrderRequest request, BigDecimal deliveryPrice) {
        return OrderEntity.builder()
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryPrice(deliveryPrice)
                .status(OrderStatus.CREATED)
                .build();
    }

    public OrderResponse toResponse(OrderEntity order, CourierResponse courier) {
        return OrderResponse.builder()
                .id(order.getId())
                .pickupAddress(order.getPickupAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPrice(order.getDeliveryPrice())
                .courierId(order.getCourierId())
                .courierFullName(courier != null ? courier.getFullName() : null)
                .courierPhoneNumber(courier != null ? courier.getPhoneNumber() : null)
                .status(order.getStatus())
                .build();
    }
}