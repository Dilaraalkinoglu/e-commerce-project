package com.dilaraalk.order.service;

import java.math.BigDecimal;

import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.user.entity.User;

public interface ICheckoutService {

	CheckoutResponseDto checkout(User user, CheckoutRequestDto request, String idempotencyKey);

	BigDecimal validateCoupon(User user, String code);

}
