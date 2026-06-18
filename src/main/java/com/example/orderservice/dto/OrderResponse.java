package com.example.orderservice.dto;

import com.example.orderservice.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Long id;

    private String pickupAddress;

    private String deliveryAddress;

    private BigDecimal deliveryPrice;

    private Long courierId;

    private String courierFullName;

    private String courierPhoneNumber;

    private OrderStatus status;
}