package com.dilaraalk.product.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ProductResponseDto {

	private Long id;

	private String name;

	private BigDecimal price;

	private int stock;

	private String description;

	// ürünle ilişkili kategorilerin isimleri
	private List<String> categories;

	private List<Long> categoryIds;

	private List<ProductImageDto> images;

}
