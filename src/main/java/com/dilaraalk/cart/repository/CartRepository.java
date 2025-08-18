package com.dilaraalk.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long>{

	//kullanıcının aktif sepetini almak için 
	Optional<Cart> findByUser(User user);
}
