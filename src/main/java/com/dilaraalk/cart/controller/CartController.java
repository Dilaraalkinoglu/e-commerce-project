package com.dilaraalk.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.service.ICartService;
import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Sepete ürün ekle", description = "Kullanıcının sepetine belirtilen ürünü ekler.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla sepete eklendi"),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek veya stok yetersiz"),
            @ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli")
    })
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

    // PUT /api/carts/items/{productId} → Ürün adedini güncelle
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponseDto> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam int quantity) {

        User user = userService.findById(userDetails.getId());
        return ok(cartService.updateItemQuantity(user, productId, quantity));
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
