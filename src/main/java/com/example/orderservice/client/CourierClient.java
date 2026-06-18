package com.example.orderservice.client;

import com.example.orderservice.client.dto.CourierResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "courier-service")
public interface CourierClient {

    @GetMapping("/couriers/available")
    CourierResponse getAvailableCourier();

    @GetMapping("/couriers/{id}")
    CourierResponse getCourierById(@PathVariable Long id);
}