package com.dilaraalk.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

	
}
