package com.dilaraalk.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDto {
	
	@NotNull(message = "Adres seçimi zorunludur")
	private Long addressId;
	
	@NotBlank(message = "Ödeme yöntemi boş olamaz")
	private String paymentMethod;

}
