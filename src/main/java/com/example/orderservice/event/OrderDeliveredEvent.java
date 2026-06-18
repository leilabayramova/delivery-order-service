package com.example.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveredEvent {

    private Long orderId;

    private Long courierId;

    private BigDecimal deliveryPrice;
}