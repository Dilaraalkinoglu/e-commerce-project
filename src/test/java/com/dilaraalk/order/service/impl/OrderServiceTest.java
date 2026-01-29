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
    private org.springframework.context.ApplicationEventPublisher eventPublisher;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        eventPublisher = mock(org.springframework.context.ApplicationEventPublisher.class);
        orderService = new OrderService(orderRepository, orderItemRepository, eventPublisher);
    }

    @Test
    void updateOrderStatus_shouldUpdateStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        com.dilaraalk.user.entity.User user = new com.dilaraalk.user.entity.User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail("test@test.com");
        order.setUser(user);

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
