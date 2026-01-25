package com.dilaraalk.order.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.dto.OrderItemDto;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderItem;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.OrderItemRepository;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.order.service.IOrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements IOrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	@Override
	public List<CheckoutResponseDto> getAllOrders() {
		return orderRepository.findAll().stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	@Override
	public CheckoutResponseDto getOrderById(Long id) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Sipariş bulunamadı!"));
		return mapToDto(order);
	}

	@Override
	public CheckoutResponseDto updateOrderStatus(Long id, String status) {
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new IllegalStateException("Sipariş bulunamadı!"));

		OrderStatus newStatus;
		try {
			newStatus = OrderStatus.valueOf(status.toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Geçersiz durum: " + status);
		}

		order.setStatus(newStatus);
		orderRepository.save(order);

		return mapToDto(order);
	}

	@Override
	public List<CheckoutResponseDto> getUserOrders(Long userId) {
		// Note: This is an in-memory filter. For large datasets, use
		// orderRepository.findByUser(user)
		// but requires User entity reference.
		return orderRepository.findAll().stream()
				.filter(order -> order.getUser().getId().equals(userId))
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}

	private CheckoutResponseDto mapToDto(Order order) {
		List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
		List<OrderItemDto> itemDtos = items.stream().map(oi -> OrderItemDto.builder()
				.productId(oi.getId())
				.productName(oi.getProduct().getName())
				.quantity(oi.getQuantity())
				.unitPriceSnapshot(oi.getUnitPriceSnapshot())
				.build()).collect(Collectors.toList());

		AddressResponseDto addressDto = null;
		if (order.getAddress() != null) {
			addressDto = AddressResponseDto.builder()
					.id(order.getAddress().getId())
					.title(order.getAddress().getTitle())
					.addressLine(order.getAddress().getAddressLine())
					.city(order.getAddress().getCity())
					.state(order.getAddress().getState())
					.postalCode(order.getAddress().getPostalCode())
					.country(order.getAddress().getCountry())
					.build();
		}

		return CheckoutResponseDto.builder()
				.orderId(order.getId())
				.userName(order.getUser().getUserName())
				.email(order.getUser().getEmail())
				.createdAt(order.getCreatedAt())
				.status(order.getStatus().name())
				.totalPrice(order.getTotalPrice())
				.items(itemDtos)
				.address(addressDto)
				.build();
	}

}
