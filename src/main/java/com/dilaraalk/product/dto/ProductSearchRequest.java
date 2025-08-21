package com.dilaraalk.product.dto;

import lombok.Data;

@Data
public class ProductSearchRequest {
	
	private String name;
	
	private String slug;
	
	private Long categoryId;
	
	private Double minPrice;
	
	private Double maxPrice;
	
	private Boolean inStock;
	
	private String sortBy = "createdAt";  
	
	private String direction = "ASC";     

	private Integer page = 0;  
	
	private Integer size = 10; 

}
