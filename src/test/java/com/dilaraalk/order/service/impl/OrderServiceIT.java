package com.dilaraalk.order.service.impl;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.order.service.IOrderService;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderServiceIT extends BaseIntegrationTest {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Order savedOrder;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUserName("orderuser");
        user.setEmail("order@example.com");
        user.setPassword("pass");
        User savedUser = userRepository.save(user);

        Order order = new Order();
        order.setUser(savedUser);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.valueOf(50));
        savedOrder = orderRepository.save(order);
    }

    @Test
    void testGetAllOrders_returnsOrders() {
        List<CheckoutResponseDto> orders = orderService.getAllOrders();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testGetOrderById_returnsCorrectOrder() {
        CheckoutResponseDto dto = orderService.getOrderById(savedOrder.getId());
        assertThat(dto.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(dto.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testUpdateOrderStatus_updatesStatusSuccessfully() {
        CheckoutResponseDto dto = orderService.updateOrderStatus(savedOrder.getId(), "PAID");
        assertThat(dto.getStatus()).isEqualTo("PAID");

        Order updatedOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }
}

