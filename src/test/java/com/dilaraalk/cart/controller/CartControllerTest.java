package com.dilaraalk.cart.controller;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.service.ICartService;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    private ICartService cartService;
    private IUserService userService;
    private CartController cartController;

    @BeforeEach
    void setUp() {
        cartService = mock(ICartService.class);
        userService = mock(IUserService.class);
        cartController = new CartController(cartService, userService);
    }

    @Test
    void getCart_shouldReturnCart() {
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        CartResponseDto dto = new CartResponseDto();
        when(userService.findById(1L)).thenReturn(user);
        when(cartService.getCartForUser(user)).thenReturn(dto);

        ResponseEntity<CartResponseDto> response = cartController.getCart(userDetails);

        assertNotNull(response);
        assertEquals(dto, response.getBody());
    }

    @Test
    void addItem_shouldCallService() {
        User user = new User();
        user.setId(1L);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        CartItemRequestDto requestDto = new CartItemRequestDto();
        CartResponseDto dto = new CartResponseDto();

        when(userService.findById(1L)).thenReturn(user);
        when(cartService.addItem(user, requestDto)).thenReturn(dto);

        ResponseEntity<CartResponseDto> response = cartController.addItem(userDetails, requestDto);

        assertNotNull(response);
        assertEquals(dto, response.getBody());
        verify(cartService).addItem(user, requestDto);
    }
}
