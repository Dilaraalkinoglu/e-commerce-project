package com.dilaraalk.cart.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dilaraalk.cart.dto.CartItemRequestDto;
import com.dilaraalk.cart.dto.CartItemResponseDto;
import com.dilaraalk.cart.dto.CartResponseDto;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.repository.CartItemRepository;
import com.dilaraalk.cart.repository.CartRepository;
import com.dilaraalk.cart.service.ICartService;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponseDto getCartForUser(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        cart.getItems().size();
        return mapToCartResponseDto(cart);
    }

    @Override
    public CartResponseDto addItem(User user, CartItemRequestDto dto) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < dto.getQuantity()) {
            throw new IllegalArgumentException("Yeterli stok yok");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setUnitPriceSnapshot(product.getPrice());
                    newItem.setQuantity(0);
                    cart.addItem(newItem);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + dto.getQuantity());
        cartItemRepository.save(cartItem);

        return mapToCartResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto removeItem(User user, Long productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not in cart"));

        cart.getItems().remove(itemToRemove);
        itemToRemove.setCart(null); 
        cartRepository.save(cart); 
        cartRepository.flush(); 


        return mapToCartResponseDto(cart);
    }

    @Override
    public CartResponseDto clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems()); // tüm itemleri DB’den sil
        cart.getItems().clear();

        return mapToCartResponseDto(cartRepository.save(cart));
    }

    // Mapper
    private CartResponseDto mapToCartResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setCartId(cart.getId());

        dto.setItems(cart.getItems().stream().map(item -> {
            CartItemResponseDto itemDto = new CartItemResponseDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setUnitPrice(item.getUnitPriceSnapshot());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setSubTotal(item.getSubTotal());
            return itemDto;
        }).toList());

        BigDecimal total = dto.getItems().stream()
                .map(CartItemResponseDto::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setTotal(total);

        return dto;
    }
}
