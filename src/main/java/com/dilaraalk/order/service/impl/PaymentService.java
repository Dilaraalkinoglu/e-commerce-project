package com.dilaraalk.order.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	
	public String processPayment(Long orderId, String paymentMethod) {
		return UUID.randomUUID().toString();
	}

}
