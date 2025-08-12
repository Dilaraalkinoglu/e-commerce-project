package com.dilaraalk.product.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.service.IProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final IProductService productService;
	
	 public ProductController(IProductService productService) {
		this.productService = productService;
	}
	 
	 @GetMapping("/paginated")
	 public ResponseEntity<Page<ProductResponseDto>> getAllProductsPaginated(
	         @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
	     
	     Page<ProductResponseDto> productPage = productService.getAllProductsPaginated(pageable);
	     return ResponseEntity.ok(productPage);
	 }
	 
	
	 
	 @PostMapping
	 public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto){
		 ProductResponseDto createdProduct = productService.createProduct(productRequestDto);
		 return new ResponseEntity<>(createdProduct,HttpStatus.CREATED);
		 
	 }
	 
	 @PutMapping("/{id}")
	 public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequestDto productRequestDto){
		  ProductResponseDto updatedProduct = productService.updateProduct(id, productRequestDto);
		 return ResponseEntity.ok(updatedProduct);
	 }
	
	 @DeleteMapping("/{id}")
	 public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
		 productService.deleteProduct(id);
		 return ResponseEntity.noContent().build();
	 }
	 
	 @GetMapping("/{id}")
	 public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id){
		 ProductResponseDto product = productService.getProductById(id);
		 return ResponseEntity.ok(product);
		 
	 }
	 
	 @GetMapping
	 public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
		 List<ProductResponseDto> productList = productService.getAllProducts();
		 return ResponseEntity.ok(productList);
	 }
	 
	 
	 
}
