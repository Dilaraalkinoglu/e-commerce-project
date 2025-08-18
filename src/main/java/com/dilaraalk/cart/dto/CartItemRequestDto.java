package com.dilaraalk.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDto {
	
	@NotNull
	private Long productId;
	
	@Min(1)
	private int quantity;

}
