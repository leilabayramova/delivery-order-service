package com.example.orderservice.service;

import com.example.orderservice.client.CourierClient;
import com.example.orderservice.client.dto.CourierResponse;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.OrderEntity;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.event.OrderAssignedEvent;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.event.OrderDeliveredEvent;
import com.example.orderservice.exception.CourierUnavailableException;
import com.example.orderservice.exception.InvalidOrderStatusException;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.producer.OrderEventProducer;
import com.example.orderservice.repository.OrderRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal DEFAULT_DELIVERY_PRICE = BigDecimal.valueOf(5);

    private final OrderRepository orderRepository;
    private final CourierClient courierClient;
    private final OrderEventProducer orderEventProducer;

    public OrderResponse createOrder(CreateOrderRequest request) {
        BigDecimal deliveryPrice = calculateDeliveryPrice();

        OrderEntity createdOrder = createInitialOrder(request, deliveryPrice);

        try {
            CourierResponse courier = courierClient.getAvailableCourier();

            publishOrderCreatedEvent(createdOrder);

            OrderEntity assignedOrder = assignCourierToOrder(createdOrder, courier);

            publishOrderAssignedEvent(assignedOrder);

            return OrderMapper.toResponse(assignedOrder, courier);
        } catch (FeignException exception) {
            rejectOrder(createdOrder);

            throw new CourierUnavailableException("No available courier found");
        }
    }
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> OrderMapper.toResponse(order, getCourierIfAssigned(order)))
                .toList();
    }


    public OrderResponse getOrderById(Long id) {
        OrderEntity order = findOrderById(id);

        return OrderMapper.toResponse(order, getCourierIfAssigned(order));
    }


    public OrderResponse markAsPickedUp(Long id) {
        OrderEntity order = findOrderById(id);

        validateOrderCanBePickedUp(order);

        order.setStatus(OrderStatus.PICKED_UP);

        OrderEntity updatedOrder = orderRepository.save(order);

        return OrderMapper.toResponse(updatedOrder, getCourierIfAssigned(updatedOrder));
    }

    public OrderResponse markAsDelivered(Long id) {
        OrderEntity order = findOrderById(id);

        validateOrderCanBeDelivered(order);

        order.setStatus(OrderStatus.DELIVERED);

        OrderEntity updatedOrder = orderRepository.save(order);

        publishOrderDeliveredEvent(updatedOrder);

        return OrderMapper.toResponse(updatedOrder, getCourierIfAssigned(updatedOrder));
    }

    private BigDecimal calculateDeliveryPrice() {
        return DEFAULT_DELIVERY_PRICE;
    }

    private OrderEntity createInitialOrder(CreateOrderRequest request, BigDecimal deliveryPrice) {
        OrderEntity order = OrderMapper.toEntity(request, deliveryPrice);

        return orderRepository.save(order);
    }

    private OrderEntity assignCourierToOrder(OrderEntity order, CourierResponse courier) {
        order.setCourierId(courier.getId());
        order.setStatus(OrderStatus.ASSIGNED);

        return orderRepository.save(order);
    }

    private void rejectOrder(OrderEntity order) {
        order.setStatus(OrderStatus.REJECTED);

        orderRepository.save(order);
    }

    private OrderEntity findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
    }

    private void validateOrderCanBePickedUp(OrderEntity order) {
        validateOrderIsNotFinal(order);

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new InvalidOrderStatusException(
                    "Order can be picked up only from ASSIGNED status"
            );
        }
    }

    private void validateOrderCanBeDelivered(OrderEntity order) {
        validateOrderIsNotFinal(order);

        if (order.getStatus() != OrderStatus.PICKED_UP) {
            throw new InvalidOrderStatusException(
                    "Order can be delivered only from PICKED_UP status"
            );
        }
    }

    private void validateOrderIsNotFinal(OrderEntity order) {
        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.REJECTED) {
            throw new InvalidOrderStatusException(
                    "Order cannot be updated after final status: " + order.getStatus()
            );
        }
    }

    private CourierResponse getCourierIfAssigned(OrderEntity order) {
        if (order.getCourierId() == null) {
            return null;
        }

        try {
            return courierClient.getCourierById(order.getCourierId());
        } catch (FeignException.NotFound exception) {
            return null;
        }
    }

    private void publishOrderCreatedEvent(OrderEntity order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getDeliveryPrice()
        );

        orderEventProducer.sendOrderCreatedEvent(event);
    }

    private void publishOrderAssignedEvent(OrderEntity order) {
        OrderAssignedEvent event = new OrderAssignedEvent(
                order.getId(),
                order.getCourierId(),
                order.getDeliveryPrice()
        );

        orderEventProducer.sendOrderAssignedEvent(event);
    }

    private void publishOrderDeliveredEvent(OrderEntity order) {
        OrderDeliveredEvent event = new OrderDeliveredEvent(
                order.getId(),
                order.getCourierId(),
                order.getDeliveryPrice()
        );

        orderEventProducer.sendOrderDeliveredEvent(event);
    }
}