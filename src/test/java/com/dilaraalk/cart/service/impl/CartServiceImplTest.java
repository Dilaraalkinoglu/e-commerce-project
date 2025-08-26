package com.dilaraalk.cart.service.impl;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.repository.CartItemRepository;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        cartItemRepository = mock(CartItemRepository.class);
        productRepository = mock(ProductRepository.class);

        cartService = new CartServiceImpl(cartRepository, cartItemRepository, productRepository);
    }

    @Test
    void getCartForUser_shouldReturnCart() {
        User user = new User();
        user.setId(1L);
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        CartResponseDto response = cartService.getCartForUser(user);

        assertNotNull(response);
        assertEquals(10L, response.getCartId());
    }

    @Test
    void addItem_shouldAddNewItem() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        Product product = new Product();
        product.setId(100L);
        product.setName("Prod");
        product.setPrice(BigDecimal.TEN);
        product.setStock(10);

        CartItemRequestDto dto = new CartItemRequestDto();
        dto.setProductId(100L);
        dto.setQuantity(2);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartResponseDto response = cartService.addItem(user, dto);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getItems().get(0).getQuantity());
    }

    @Test
    void clearCart_shouldEmptyItems() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(10L);

        CartItem item = new CartItem();
        item.setId(5L);
        item.setCart(cart);
        
        Set<CartItem> items = new HashSet<>();
        items.add(item);
        cart.setItems(items);

        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        
        CartResponseDto response = cartService.clearCart(user);

        assertNotNull(response);
        assertEquals(0, response.getItems().size());
        verify(cartItemRepository).deleteAll(cart.getItems());
    }
}
