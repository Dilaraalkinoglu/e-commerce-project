package com.dilaraalk.product.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ProductRequestDto {
	
	@NotBlank(message = "Ürün adı boş olamaz!")
	private String name;
	
	@PositiveOrZero(message = "Fiyat negatif olamaz!")
	private BigDecimal price;
	
	@PositiveOrZero(message = "Stok negatif olamaz!")
	private int stock;
	
	//ürün eklerken hangi kategorilere ait olacak 
	@NotEmpty(message = "En az bir kategori seçmelisiniz")
	private List<Long> categoryIds;

}
