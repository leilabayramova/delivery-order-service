package com.example.orderservice.mapper;

import com.example.orderservice.client.dto.CourierResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStatus;

import java.math.BigDecimal;

public interface OrderMapper {

    static OrderEntity toEntity(CreateOrderRequest request, BigDecimal deliveryPrice) {
        return OrderEntity.builder()
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryPrice(deliveryPrice)
                .status(OrderStatus.CREATED)
                .build();
    }

    static OrderResponse toResponse(OrderEntity order, CourierResponse courier) {
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