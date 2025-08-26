package com.dilaraalk.product.service.impl;

import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productService = new ProductServiceImpl(productRepository, categoryRepository);
    }

    @Test
    void createProduct_shouldSaveProductAndReturnDto() {
        ProductRequestDto dto = new ProductRequestDto();
        dto.setName("Test Product");
        dto.setPrice(BigDecimal.valueOf(100));
        dto.setStock(10);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Test Product");
        saved.setPrice(BigDecimal.valueOf(100));
        saved.setStock(10);

        when(productRepository.existsBySlug(any())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponseDto result = productService.createProduct(dto);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnDto() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test");
        product.setPrice(BigDecimal.TEN);
        product.setStock(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto dto = productService.getProductById(1L);

        assertEquals("Test", dto.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_shouldThrowIfNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> productService.getProductById(1L));
    }

    @Test
    void deleteProduct_shouldCallDelete() {
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        verify(productRepository).delete(product);
    }
}
