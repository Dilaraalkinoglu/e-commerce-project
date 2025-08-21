package com.dilaraalk.cart.controller;

import org.springframework.http.ResponseEntity;
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
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController extends BaseController {

    private final ICartService cartService;
    private final IUserService userService;

    // GET /api/carts → Kullanıcının sepetini getir
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        return ok(cartService.getCartForUser(user));
    }

    // POST /api/carts/items → Sepete ürün ekle
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemRequestDto requestDto) {

        User user = userService.findById(userDetails.getId());
        return created(cartService.addItem(user, requestDto));
    }

    // DELETE /api/carts/items/{productId} → Sepetten ürün çıkar
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponseDto> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId) {

        User user = userService.findById(userDetails.getId());
        return ok(cartService.removeItem(user, productId));
    }

    // DELETE /api/carts/clear → Sepeti tamamen boşalt
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponseDto> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        return ok(cartService.clearCart(user));
    }

    // GET /api/carts/checkout-preview → Ödeme öncesi özet
    @GetMapping("/checkout-preview")
    public ResponseEntity<CartResponseDto> checkoutPreview(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.findById(userDetails.getId());
        return ok(cartService.getCartForUser(user));
    }
}
