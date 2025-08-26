package com.dilaraalk.admin.controller;

import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.service.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminOrderControllerTest {

    private IOrderService orderService;
    private AdminOrderController controller;

    @BeforeEach
    void setUp() {
        orderService = mock(IOrderService.class);
        controller = new AdminOrderController(orderService);
    }

    @Test
    void getAllOrders_shouldReturnList() {
        when(orderService.getAllOrders()).thenReturn(List.of(new CheckoutResponseDto()));
        ResponseEntity<List<CheckoutResponseDto>> response = controller.getAllOrders();

        assertNotNull(response);
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders();
    }

    @Test
    void updateOrderStatus_shouldCallService() {
        CheckoutResponseDto dto = new CheckoutResponseDto();
        when(orderService.updateOrderStatus(1L, "PAID")).thenReturn(dto);

        ResponseEntity<CheckoutResponseDto> response = controller.updateOrderStatus(1L, "PAID");

        assertNotNull(response);
        assertEquals(dto, response.getBody());
        verify(orderService).updateOrderStatus(1L, "PAID");
    }
}
