package com.dilaraalk.cart.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class CartResponseDto {
	
	private Long cartId;
	
	private List<CartItemResponseDto> items;
	
	private BigDecimal total;

	

}
