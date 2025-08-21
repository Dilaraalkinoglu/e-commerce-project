package com.dilaraalk.product.controller;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.dto.ProductSearchRequest;
import com.dilaraalk.product.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final IProductService productService;
    
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDto>> searchProducts(ProductSearchRequest request){
    	return ok(productService.searchProducts(request));
    }

    @GetMapping("/paginated")
    public Page<ProductResponseDto> getAllProductsPaginated(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return productService.getAllProductsPaginated(pageable);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        return created(productService.createProduct(productRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto productRequestDto) {
        return ok(productService.updateProduct(id, productRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return noContent();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ok(productService.getAllProducts());
    }
    
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductResponseDto> uploadProductImages(
    		@PathVariable Long id,
    		@RequestParam("files") MultipartFile[] files) {
    	return ok(productService.uploadProductImages(id,files));
    }
}
