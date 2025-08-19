package com.dilaraalk.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.order.entity.Order;
import com.dilaraalk.user.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long>{

	List<Order> findByUser(User user);
	
}
