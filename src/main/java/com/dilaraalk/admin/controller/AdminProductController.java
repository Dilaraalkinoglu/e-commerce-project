package com.dilaraalk.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.service.IProductService;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController extends BaseController{
	
	private final IProductService productService;
	
	public AdminProductController(IProductService productService) {
		this.productService = productService;
	}
	
	@PostMapping
	public ResponseEntity<ProductResponseDto> create(@RequestBody ProductRequestDto dto){
		return created(productService.createProduct(dto));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ProductResponseDto> update(@PathVariable Long id,
			@RequestBody ProductRequestDto dto){
		return ok(productService.updateProduct(id, dto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		productService.deleteProduct(id);
		return noContent();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponseDto> getById(@PathVariable Long id){
		return ok(productService.getProductById(id));
	}
	
	@GetMapping("/paginated")
	public ResponseEntity<Page<ProductResponseDto>> getAllPaginated(Pageable pageable){
		return ok(productService.getAllProductsPaginated(pageable));
	}
	
	@PostMapping("/{id}/images")
	public ResponseEntity<ProductResponseDto> uploadImages(@PathVariable Long id,
			@RequestParam("files") MultipartFile[] files){
		return ok(productService.uploadProductImages(id, files));
	}
	
	
	
	
	
	
	
	
	

}
