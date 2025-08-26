package com.dilaraalk.order.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.email.event.OrderConfirmedEvent;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.dto.OrderItemDto;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderItem;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.entity.Payment;
import com.dilaraalk.order.repository.OrderItemRepository;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.order.repository.PaymentRepository;
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
    private final PaymentRepository paymentRepository;
	private final PaymentService paymentService;
	private final ApplicationEventPublisher eventPublisher;
    
	//uygulama restartında sıfırlanır 
	private final ConcurrentHashMap<String, Long> idempotencyStore = new ConcurrentHashMap<>();
	
	@Override
	public CheckoutResponseDto checkout(User user, CheckoutRequestDto request, String idempotencyKey) {
		
		// Idempotency kontrol: aynı key geldiyse önceki order'ı döndür
		if (idempotencyKey != null && idempotencyStore.containsKey(idempotencyKey)) {
			Long existingOrderId = idempotencyStore.get(idempotencyKey);
			Order existingOrder = orderRepository.findById(existingOrderId)
					.orElseThrow(() -> new IllegalStateException("Sipariş bulunamadı"));
			return buildResponse(existingOrder, orderItemRepository.findByOrderId(existingOrderId));
		}
		
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
		
		// CartItem - OrderItem + stok dogrulama ve düşüm 
		List<OrderItem> orderItems = cart.getItems().stream().map(ci-> {
			
			//stok kontrol
			if (ci.getQuantity() > ci.getProduct().getStock()) {
				throw new IllegalStateException("Yeterli stok yok: " + ci.getProduct().getName());
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
		
		// toplam fiyatı hesapla ve order'a yaz
		BigDecimal total = orderItems.stream()
				.map(oi -> oi.getUnitPriceSnapshot().multiply(BigDecimal.valueOf(oi.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalPrice(total);
		orderRepository.save(order);
		
		//fake ödeme
		paymentService.processPayment(order.getId(), request.getPaymentMethod());
		Payment payment = Payment.builder()
				.order(order)
				.amount(total)
				.paymentMethod(request.getPaymentMethod())
				.createdAt(LocalDateTime.now())
				.build();
		paymentRepository.save(payment);
		
		// ödeme başarılı (PAID)
		order.setStatus(OrderStatus.PAID);
		orderRepository.save(order);
		
		// event fırlat
		eventPublisher.publishEvent(new OrderConfirmedEvent(
		        this, // source
		        user.getEmail(), // müşteri e-mail
		        user.getUserName(),  // müşteri adı
		        order.getId().toString(), // sipariş numarası
		        order.getCreatedAt().toLocalDate().toString(), // sipariş tarihi
		        order.getTotalPrice().toString() // toplam tutar
		));



		
		// Idempotency kaydet
		if (idempotencyKey != null) {
			idempotencyStore.put(idempotencyKey, order.getId());
		}
		
		// sepeti temizle
		cart.getItems().clear();
		cartRepository.save(cart);
		

        return buildResponse(order, orderItems);
    }
	
	private CheckoutResponseDto buildResponse(Order order, List<OrderItem> orderItems) {
		List<OrderItemDto> items = orderItems.stream()
				.map(oi -> OrderItemDto.builder()
						.productId(oi.getProduct().getId())
						.productName(oi.getProduct().getName())
						.quantity(oi.getQuantity())
						.unitPriceSnapshot(oi.getUnitPriceSnapshot())
						.build())
				.collect(Collectors.toList());
		
		Address address = order.getAddress();
		AddressResponseDto addressDto = AddressResponseDto.builder()
		        .id(address.getId())
		        .title(address.getTitle())
		        .addressLine(address.getAddressLine())
		        .city(address.getCity())
		        .state(address.getState())
		        .postalCode(address.getPostalCode())
		        .country(address.getCountry())
		        .build();

		
		return CheckoutResponseDto.builder()
				.orderId(order.getId())
				.createdAt(order.getCreatedAt())
				.status(order.getStatus().name())
				.totalPrice(order.getTotalPrice())
				.items(items)
				.address(addressDto)
				.build();
		
		
	}



}
