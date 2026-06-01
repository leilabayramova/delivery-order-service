package com.example.orderservice.exception;

public class CourierUnavailableException extends RuntimeException {

    public CourierUnavailableException(String message) {
        super(message);
    }
}