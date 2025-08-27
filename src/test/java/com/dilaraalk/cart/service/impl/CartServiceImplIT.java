package com.dilaraalk.cart.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Collections;

@Transactional
class CartServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private CartRepository cartRepository;


    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        // Test DB temizliği
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Test kullanıcı oluştur
        user = new User();
        user.setUserName("testuser");
        user.setEmail("testuser@test.com");
        user.setPassword("password");
        user.setRoles(Collections.singletonList("ROLE_USER"));
        user = userRepository.save(user);

        // Test ürün oluştur
        product = Product.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .slug("test-product")
                .build();
        product = productRepository.save(product);
    }

    @Test
    void testAddItemToCart() {
        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(product.getId());
        request.setQuantity(2);

        CartResponseDto response = cartService.addItem(user, request);

        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductId()).isEqualTo(product.getId());
        assertThat(response.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void testRemoveItemFromCart() {
        // Önce sepete ekle
        CartItemRequestDto addRequest = new CartItemRequestDto();
        addRequest.setProductId(product.getId());
        addRequest.setQuantity(2);
        cartService.addItem(user, addRequest);

        cartService.removeItem(user, product.getId());
        
        entityManager.flush();
        entityManager.clear();
        
        CartResponseDto afterRemove = cartService.getCartForUser(user);

        assertThat(afterRemove.getItems()).isEmpty();
    }

    @Test
    void testClearCart() {
        // Sepete ekle
        CartItemRequestDto addRequest = new CartItemRequestDto();
        addRequest.setProductId(product.getId());
        addRequest.setQuantity(2);
        cartService.addItem(user, addRequest);

        CartResponseDto cleared = cartService.clearCart(user);
        assertThat(cleared.getItems()).isEmpty();
        assertThat(cleared.getTotal()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void testGetCartForUser() {
        CartResponseDto response = cartService.getCartForUser(user);
        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void testAddItem_InsufficientStock() {
        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(product.getId());
        request.setQuantity(20); // stoktan fazla

        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(user, request));
    }
}
