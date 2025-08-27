package com.dilaraalk.product.service.impl;

import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.dto.ProductSearchRequest;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class ProductServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product savedProduct;
    private Category savedCategory;

    @BeforeEach
    void setup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Electronics");
        category.setSlug("electronics-" + System.currentTimeMillis());
        savedCategory = categoryRepository.saveAndFlush(category);

        Product product = new Product();
        product.setName("Test Product");
        product.setSlug("test-product-" + System.currentTimeMillis());
        product.setPrice(BigDecimal.valueOf(100.0));
        product.setStock(10);
        product.setCategories(Set.of(savedCategory));

        savedProduct = productRepository.saveAndFlush(product);
    }

    @Test
    void testCreateProduct() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("New Product");
        request.setPrice(BigDecimal.valueOf(200.0));
        request.setStock(5);
        request.setCategoryIds(List.of(savedCategory.getId()));

        ProductResponseDto response = productService.createProduct(request);
        assertNotNull(response.getId());
        assertEquals("New Product", response.getName());
        assertEquals(BigDecimal.valueOf(200.0), response.getPrice());
        assertEquals(5, response.getStock());
        assertTrue(response.getCategories().contains("Electronics"));
    }

    @Test
    void testUpdateProduct() {
        ProductRequestDto request = new ProductRequestDto();
        request.setName("Updated Product");
        request.setPrice(BigDecimal.valueOf(150.0));
        request.setStock(20);
        request.setCategoryIds(List.of(savedCategory.getId()));

        ProductResponseDto response = productService.updateProduct(savedProduct.getId(), request);
        assertEquals("Updated Product", response.getName());
        assertEquals(BigDecimal.valueOf(150.0), response.getPrice());
        assertEquals(20, response.getStock());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(savedProduct.getId());
        assertFalse(productRepository.existsById(savedProduct.getId()));
    }

    @Test
    void testGetProductById() {
        ProductResponseDto response = productService.getProductById(savedProduct.getId());
        assertEquals(savedProduct.getName(), response.getName());
    }

    @Test
    void testGetAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
    }

    @Test
    void testSearchProducts() {
        ProductSearchRequest searchRequest = new ProductSearchRequest();
        searchRequest.setName("Test");
        searchRequest.setMinPrice(50.0);
        searchRequest.setMaxPrice(200.0);
        searchRequest.setInStock(true);
        searchRequest.setPage(0);
        searchRequest.setSize(5);

        var page = productService.searchProducts(searchRequest);
        assertTrue(page.getTotalElements() > 0);
    }

    

    



}
