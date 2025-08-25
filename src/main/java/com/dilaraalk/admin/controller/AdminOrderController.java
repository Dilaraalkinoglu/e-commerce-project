package com.dilaraalk.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.service.IOrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController extends BaseController{
	
	private final IOrderService orderService;
	
	//tüm siparişleri listeler
	@GetMapping
	public ResponseEntity<List<CheckoutResponseDto>> getAllOrders(){
		List<CheckoutResponseDto> orders = orderService.getAllOrders();
		return ok(orders);
	}
	
	//tek siparişi görüntüler
	@GetMapping("/{id}")
	public ResponseEntity<CheckoutResponseDto> getOrderById(@PathVariable Long id){
		CheckoutResponseDto order = orderService.getOrderById(id);
		return ok(order);
	}
	
	//sipariş durumunu günceller 
	@PutMapping("/{id}/status")
	public ResponseEntity<CheckoutResponseDto> updateOrderStatus(
			@PathVariable Long id,
			@RequestParam String status){
		CheckoutResponseDto updatedOrder = orderService.updateOrderStatus(id, status);
		return ok(updatedOrder);
	}
	
	
	
	
	
	
	
	
	
	

}
