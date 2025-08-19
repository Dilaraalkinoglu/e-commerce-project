package com.dilaraalk.order.service;

import com.dilaraalk.order.dto.CheckoutRequestDto;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.user.entity.User;

public interface ICheckoutService {
	
	CheckoutResponseDto checkout(User user, CheckoutRequestDto request);

}
