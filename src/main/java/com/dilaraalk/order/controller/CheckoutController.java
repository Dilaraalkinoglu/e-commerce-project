package com.dilaraalk.order.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.service.ICheckoutService;
import com.dilaraalk.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController extends BaseController{
	
	private final ICheckoutService checkoutService;
	
	@PostMapping
	public ResponseEntity<CheckoutResponseDto> checkout(@AuthenticationPrincipal User user,
			@RequestBody CheckoutRequestDto request,
			@RequestHeader(value = "Idempotency-Key" , required = false) String idempotencyKey){
		CheckoutResponseDto resp = checkoutService.checkout(user, request, idempotencyKey);
		return created(resp);
	}

}
