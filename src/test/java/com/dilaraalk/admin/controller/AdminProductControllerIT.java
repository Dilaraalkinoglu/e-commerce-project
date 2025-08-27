package com.dilaraalk.admin.controller;

import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.product.dto.ProductRequestDto;
import com.dilaraalk.product.dto.ProductResponseDto;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminProductControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User adminUser;
    private Category category;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // Admin user
        adminUser = new User();
        adminUser.setUserName("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("adminpass");
        adminUser = userRepository.save(adminUser);

        // Category
        category = new Category();
        category.setName("Elektronik");
        category.setSlug("elektronik"); // slug zorunlu
        category = categoryRepository.save(category);
    }

    @Test
    void testCreateUpdateGetDeleteProduct() throws Exception {
        // --- CREATE PRODUCT ---
        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName("Test Ürün");
        requestDto.setPrice(BigDecimal.valueOf(100));
        requestDto.setStock(10);
        requestDto.setCategoryIds(Collections.singletonList(category.getId()));

        MvcResult createResult = mockMvc.perform(post("/api/admin/products")
                        .with(mockCustomUser(adminUser, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        ProductResponseDto createdProduct = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                ProductResponseDto.class
        );

        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Test Ürün");

        Long productId = createdProduct.getId();

        // --- UPDATE PRODUCT ---
        requestDto.setName("Güncellenmiş Ürün");
        requestDto.setPrice(BigDecimal.valueOf(120));

        MvcResult updateResult = mockMvc.perform(put("/api/admin/products/" + productId)
                        .with(mockCustomUser(adminUser, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        ProductResponseDto updatedProduct = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(),
                ProductResponseDto.class
        );

        assertThat(updatedProduct.getName()).isEqualTo("Güncellenmiş Ürün");
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(120));

        // --- GET PRODUCT BY ID ---
        MvcResult getResult = mockMvc.perform(get("/api/admin/products/" + productId)
                        .with(mockCustomUser(adminUser, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        ProductResponseDto fetchedProduct = objectMapper.readValue(
                getResult.getResponse().getContentAsString(),
                ProductResponseDto.class
        );

        assertThat(fetchedProduct.getName()).isEqualTo("Güncellenmiş Ürün");

        // --- DELETE PRODUCT ---
        mockMvc.perform(delete("/api/admin/products/" + productId)
                        .with(mockCustomUser(adminUser, "ADMIN")))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(productId)).isEmpty();
    }
}
