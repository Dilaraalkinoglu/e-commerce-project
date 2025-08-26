package com.dilaraalk.order.service.impl;

import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.OrderItemRepository;
import com.dilaraalk.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        orderService = new OrderService(orderRepository, orderItemRepository);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var dto = orderService.updateOrderStatus(1L, "PAID");

        assertEquals("PAID", dto.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_shouldThrowIfInvalidStatus() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(1L, "INVALID"));
    }
}

