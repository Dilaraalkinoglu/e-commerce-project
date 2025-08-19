package com.dilaraalk.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponseDto {
	
	private Long orderId;
	
	private LocalDateTime createdAt;
	
	private String status;
	
	private BigDecimal totalPrice;
	
	private List<OrderItemDto> items;
	
	private AddressDto address;

}
