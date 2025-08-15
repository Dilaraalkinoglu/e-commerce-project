package com.dilaraalk.product.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductResponseDto {
	
	private Long id;
	
	private String name;
	
	private double price;
	
	private int stock;
	
	//ürünle ilişkili kategorilerin isimleri
	private List<String> categories;

}
