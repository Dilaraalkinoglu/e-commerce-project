package com.dilaraalk.product.controller;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.product.dto.ProductSearchRequest;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductControllerIT extends BaseIntegrationTest {

    @Autowired
    protected ProductRepository productRepository;

    @BeforeEach
    public void setupTestData() {
        productRepository.deleteAll();

        Product product = new Product();
        product.setName("Test Product");
        product.setSlug("test-product");
        product.setPrice(BigDecimal.valueOf(100.0));        product.setStock(10);
        product.setCreatedAt(LocalDateTime.now());

        productRepository.save(product);
    }
	
    @Test
    void getAllProducts_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProductsPaginated_ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/paginated")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById_ok() throws Exception {
        // Test DB’de en az 1 ürün olması gerekiyor
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchProducts_ok() throws Exception {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setName("Test");        // DTO’ya uygun setter
        request.setMinPrice(0.0);
        request.setMaxPrice(1000.0);
        request.setInStock(true);
        request.setPage(0);
        request.setSize(5);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("name", request.getName())
                        .param("minPrice", String.valueOf(request.getMinPrice()))
                        .param("maxPrice", String.valueOf(request.getMaxPrice()))
                        .param("inStock", String.valueOf(request.getInStock()))
                        .param("page", String.valueOf(request.getPage()))
                        .param("size", String.valueOf(request.getSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
