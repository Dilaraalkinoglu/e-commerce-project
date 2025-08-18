package com.dilaraalk.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.product.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	//Sepette aynı ürün varsa bulup quantity artırmak için 
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

}
