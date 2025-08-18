package com.dilaraalk.cart.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CartItemResponseDto {

	private Long id;
	
	private Long productId;
	
	private String productName;
	
	private BigDecimal unitPrice;
	
	private int quantity;
	
	private BigDecimal subTotal;
}
