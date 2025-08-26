package com.dilaraalk.order.service.impl;

import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.*;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.user.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckoutServiceImplTest {

    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private AddressRepository addressRepository;
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;
    private ApplicationEventPublisher eventPublisher;

    private CheckoutServiceImpl checkoutService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        addressRepository = mock(AddressRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentService = mock(PaymentService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        checkoutService = new CheckoutServiceImpl(
                cartRepository, orderRepository, orderItemRepository,
                addressRepository, paymentRepository, paymentService, eventPublisher
        );
    }

    @Test
    void checkout_shouldCreateOrderAndReturnResponse() {
        // Kullanıcı oluştur
        User user = new User();
        user.setId(1L);
        user.setUserName("dilara");
        user.setEmail("test@test.com");

        // Sepet ve ürün oluştur
        Cart cart = new Cart();
        cart.setUser(user);

        // Product stub
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(50));
        product.setStock(10);

        // CartItem ekle
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.setItems(new HashSet<>(List.of(cartItem)));
        // Mock cartRepository
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        // OrderRepository.save mock
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            o.setCreatedAt(LocalDateTime.now());
            o.setStatus(OrderStatus.PAID);
            return o;
        });

        // OrderItemRepository.saveAll mock
        when(orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // AddressRepository mock
        Address address = new Address();
        address.setId(1L);
        address.setTitle("Ev");
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        // Checkout request
        CheckoutRequestDto req = new CheckoutRequestDto();
        req.setAddressId(1L);
        req.setPaymentMethod("CARD");

        // Metodu çağır
        CheckoutResponseDto resp = checkoutService.checkout(user, req, null);

        // Doğrulamalar
        assertNotNull(resp);
        assertEquals(100L, resp.getOrderId());
        assertEquals(OrderStatus.PAID.name(), resp.getStatus());
        assertEquals(1, resp.getItems().size());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(paymentService).processPayment(eq(100L), eq("CARD"));
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void checkout_shouldThrowIfCartEmpty() {
        User user = new User();
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () ->
                checkoutService.checkout(user, new CheckoutRequestDto(), null));
    }
}
