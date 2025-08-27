package com.dilaraalk.order.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.entity.OrderStatus;
import com.dilaraalk.order.repository.OrderItemRepository;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.order.repository.PaymentRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

class CheckoutServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private CheckoutServiceImpl checkoutService;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    private User user;
    private Address address;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        // Test ortamını temizle
        orderItemRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Kullanıcı oluştur
        user = new User();
        user.setEmail("test@example.com");
        user.setUserName("testuser");
        user.setPassword("password");
        userRepository.save(user);

        // Adres oluştur
        address = new Address();
        address.setTitle("Ev Adresi");
        address.setAddressLine("123 Test Street");
        address.setCity("TestCity");
        address.setState("TestState");
        address.setPostalCode("12345");
        address.setCountry("TestCountry");
        address.setUser(user);
        addressRepository.save(address);

        // Ürün oluştur (benzersiz slug)
        product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);
        product.setCreatedAt(LocalDateTime.now());
        product.setSlug("test-product-" + System.currentTimeMillis());
        productRepository.save(product);

        // Sepet oluştur
        cart = new Cart();
        cart.setUser(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // Sepete ürün ekle
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(1);
        item.setUnitPriceSnapshot(product.getPrice());
        cart.getItems().add(item);
        cartRepository.save(cart);
        
    }

    @Test
    void testCheckout_Success() {
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setAddressId(address.getId());
        request.setPaymentMethod("CREDIT_CARD");

        CheckoutResponseDto response = checkoutService.checkout(user, request, "idem-key-1");

        assertNotNull(response);
        assertEquals(OrderStatus.PAID.name(), response.getStatus());
        assertEquals(1, response.getItems().size());
        assertEquals(product.getId(), response.getItems().get(0).getProductId());
        assertTrue(
                product.getPrice().compareTo(response.getItems().get(0).getUnitPriceSnapshot()) == 0,
                "Ürün fiyatı snapshot ile eşleşmiyor"
            );
        }

    @Test
    void testCheckout_Idempotency() {
        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setAddressId(address.getId());
        request.setPaymentMethod("CREDIT_CARD");

        CheckoutResponseDto first = checkoutService.checkout(user, request, "idem-key-2");
        CheckoutResponseDto second = checkoutService.checkout(user, request, "idem-key-2");

        assertEquals(first.getOrderId(), second.getOrderId(), "Idempotent key should return same order");
        
        assertTrue(
                first.getItems().get(0).getUnitPriceSnapshot()
                    .compareTo(second.getItems().get(0).getUnitPriceSnapshot()) == 0,
                "Idempotent siparişlerde fiyatlar eşleşmeli"
            );
    }

    @Test
    void testCheckout_InsufficientStock() {
        // Ürün stoğunu sıfırla
        product.setStock(0);
        productRepository.save(product);

        CheckoutRequestDto request = new CheckoutRequestDto();
        request.setAddressId(address.getId());
        request.setPaymentMethod("CREDIT_CARD");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> checkoutService.checkout(user, request, "idem-key-3"));

        assertTrue(exception.getMessage().contains("Yeterli stok yok"));
    }
}
