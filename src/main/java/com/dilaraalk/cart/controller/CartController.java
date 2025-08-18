package com.dilaraalk.cart.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.service.ICartService;
import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.entity.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController extends BaseController{
	
	private final ICartService cartService;
	
	//sepeti getir
	@GetMapping  
	public CartResponseDto getCart(@AuthenticationPrincipal User user) {
		return ok(cartService.getCartForUser(user)).getBody();
	}
	
	//sepete ürün ekleme
	@PostMapping("/items")
	public CartResponseDto addItem(
			@AuthenticationPrincipal User user,
			@RequestBody @Valid CartItemRequestDto requestDto) {
		return created(cartService.addItem(user, requestDto)).getBody();
	}
	
	//sepetten ürün çıkarma
	@DeleteMapping("/items/{productId}")
	public CartResponseDto removeItem(
			@AuthenticationPrincipal User user,
			@PathVariable Long productId) {
		return ok(cartService.removeItem(user, productId)).getBody();
	}
	
	//sepeti temizleme
	@DeleteMapping("/clear")
	public CartResponseDto clearCart(@AuthenticationPrincipal User user) {
		return ok(cartService.clearCart(user)).getBody();
	}
	
	//checkout preview: toplam fiyat ve detay
	@GetMapping("/checkout-preview")
	public CartResponseDto checkoutPreview(@AuthenticationPrincipal User user) {
		return ok(cartService.getCartForUser(user)).getBody();
	}
	
	
	
	
	
	
	
	
	
	
	

}
