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
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController extends BaseController{
	
	private final ICheckoutService checkoutService;
	private final IUserService userService;
	
	@PostMapping
	public ResponseEntity<CheckoutResponseDto> checkout(@AuthenticationPrincipal CustomUserDetails userDetails,
	        @RequestBody CheckoutRequestDto request,
	        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
	    
	    User user = userService.findById(userDetails.getId());
	    CheckoutResponseDto resp = checkoutService.checkout(user, request, idempotencyKey);
	    return created(resp);
	}


}
