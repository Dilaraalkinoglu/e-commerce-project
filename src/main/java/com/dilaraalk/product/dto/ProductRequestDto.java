package com.dilaraalk.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductRequestDto {
	
	@NotBlank(message = "Ürün adı boş olamaz!")
	private String name;
	
	@PositiveOrZero(message = "Fiyat negatif olamaz!")
	private double price;
	
	@PositiveOrZero(message = "Stok negatif olamaz!")
	private int stock;

}
