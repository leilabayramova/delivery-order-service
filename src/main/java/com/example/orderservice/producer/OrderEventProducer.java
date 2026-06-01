package com.example.orderservice.producer;

import com.example.orderservice.config.RabbitMQConfig;
import com.example.orderservice.event.OrderAssignedEvent;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.event.OrderDeliveredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );
    }

    public void sendOrderAssignedEvent(OrderAssignedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ASSIGNED_ROUTING_KEY,
                event
        );
    }

    public void sendOrderDeliveredEvent(OrderDeliveredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_DELIVERED_ROUTING_KEY,
                event
        );
    }
}