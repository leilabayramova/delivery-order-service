package com.example.orderservice.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourierResponse {

    private Long id;

    private String fullName;

    private String phoneNumber;

    private String status;
}