package com.dilaraalk.order.controller;

import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.repository.CartItemRepository;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.entity.Order;
import com.dilaraalk.order.repository.OrderRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CheckoutControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User savedUser;
    private Long checkoutAddressId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        addressRepository.deleteAll();

        // Kullanıcı
        User user = new User();
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("pass");
        savedUser = userRepository.save(user);

        // Kullanıcı adresi
        Address address = Address.builder()
                .user(savedUser)
                .title("Ev Adresi")
                .addressLine("123 Test Street")
                .city("TestCity")
                .state("TestState")
                .postalCode("12345")
                .country("TestCountry")
                .defaultAddress(true)
                .build();
        address = addressRepository.save(address);
        checkoutAddressId = address.getId();

        // Sepet
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart = cartRepository.save(cart);

        // Ürün
        Product product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .createdAt(LocalDateTime.now())
                .slug("test-product")
                .build();
        productRepository.save(product);

        // Sepet ürünü
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setUnitPriceSnapshot(product.getPrice());
        cartItemRepository.save(cartItem);
    }

    @Test
    void testCheckout_createsOrderSuccessfully() throws Exception {
        CheckoutRequestDto requestDto = new CheckoutRequestDto();
        requestDto.setAddressId(checkoutAddressId); 
        requestDto.setPaymentMethod("CREDIT_CARD");

        mockMvc.perform(post("/api/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .with(mockCustomUser(savedUser, "USER"))  // <-- değişiklik burada
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PAID"));


        assertThat(orderRepository.findAll()).hasSize(1);
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(order.getTotalPrice()).isNotNull();
    }
}
