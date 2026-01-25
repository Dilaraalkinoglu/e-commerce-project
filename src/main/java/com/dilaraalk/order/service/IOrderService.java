package com.dilaraalk.order.service;

import java.util.List;

import com.dilaraalk.order.dto.CheckoutResponseDto;

public interface IOrderService {

	List<CheckoutResponseDto> getAllOrders();

	CheckoutResponseDto getOrderById(Long id);

	CheckoutResponseDto updateOrderStatus(Long id, String status);

	List<CheckoutResponseDto> getUserOrders(Long userId);

}
