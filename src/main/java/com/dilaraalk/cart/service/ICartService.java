package com.dilaraalk.cart.service;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.user.entity.User;

public interface ICartService {

	CartResponseDto getCartForUser(User user);

	CartResponseDto addItem(User user, CartItemRequestDto dto);

	CartResponseDto removeItem(User user, Long productId);

	CartResponseDto updateItemQuantity(User user, Long productId, int quantity);

	CartResponseDto clearCart(User user);

}
