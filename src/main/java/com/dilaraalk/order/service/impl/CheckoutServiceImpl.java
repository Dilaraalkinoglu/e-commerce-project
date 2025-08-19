package com.dilaraalk.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.order.dto.AddressDto;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.dto.OrderItemDto;
import com.dilaraalk.order.entity.Address;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderItem;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.AddressRepository;
import com.dilaraalk.order.repository.OrderItemRepository;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.order.service.ICheckoutService;
import com.dilaraalk.user.entity.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements ICheckoutService{
	
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
	
	@Override
	public CheckoutResponseDto checkout(User user, CheckoutRequestDto request) {
		
		// sepetten siparişi al
		Cart cart = cartRepository.findByUser(user)
				.orElseThrow(() -> new IllegalStateException("Sepet boş"));
		
		if (cart.getItems().isEmpty()) {
			throw new IllegalStateException("Sepet boş");
		}
		 
		
		// adresi baglama
		Address address = addressRepository.findById(request.getAddressId())
				.orElseThrow(() -> new IllegalStateException("Adres bulunamadı"));
		
		
		// order olusturma 
		Order order = new Order();
		order.setUser(user);
		order.setStatus(OrderStatus.PENDING);
		order.setAddress(address);
		order.setCreatedAt(LocalDateTime.now());
		
		orderRepository.save(order);
		
		// CartItem - OrderItem
		List<OrderItem> orderItems = cart.getItems().stream().map(ci-> {
			
			//stok kontrol
			if (ci.getQuantity() > ci.getProduct().getStock()) {
				throw new IllegalArgumentException("Yeterli stok yok: " + ci.getProduct().getName());
			}
			
			OrderItem oi = new OrderItem();
			oi.setOrder(order);
			oi.setProduct(ci.getProduct());
			oi.setQuantity(ci.getQuantity());
			oi.setUnitPriceSnapshot(ci.getProduct().getPrice());
			
			// stok düşümü
			ci.getProduct().setStock(ci.getProduct().getStock() - ci.getQuantity());
			
			return oi;
			
		}).collect(Collectors.toList());
		
		orderItemRepository.saveAll(orderItems);
		
		// sepeti temizle
		cart.getItems().clear();
		cartRepository.save(cart);
		
		// response dto hazırlama
		CheckoutResponseDto response = CheckoutResponseDto.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus().name())
                .totalPrice(orderItems.stream()
                        .map(oi -> oi.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(oi.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .items(orderItems.stream().map(oi -> OrderItemDto.builder()
                        .productId(oi.getProduct().getId())
                        .productName(oi.getProduct().getName())
                        .quantity(oi.getQuantity())
                        .unitPriceSnapshot(oi.getUnitPriceSnapshot())
                        .build()).collect(Collectors.toList()))
                .address(AddressDto.builder()
                        .id(address.getId())
                        .street(address.getStreet())
                        .city(address.getCity())
                        .country(address.getCountry())
                        .zipCode(address.getZipCode())
                        .build())
                .build();

        return response;
    }

}
