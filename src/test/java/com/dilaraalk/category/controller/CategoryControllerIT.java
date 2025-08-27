package com.dilaraalk.category.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

class CategoryControllerIT extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        parentCategory = categoryRepository.save(
                Category.builder().name("Electronics").slug("electronics").build()
        );

        childCategory = categoryRepository.save(
                Category.builder().name("Mobile Phones").slug("mobile-phones").parent(parentCategory).build()
        );
    }

    @Test
    void testGetAllCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<CategoryDto> categories = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting("name")
                .containsExactlyInAnyOrder("Electronics", "Mobile Phones");
    }

    @Test
    void testGetCategoryById() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", parentCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto categoryDto = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(categoryDto.getId()).isEqualTo(parentCategory.getId());
        assertThat(categoryDto.getName()).isEqualTo("Electronics");
        assertThat(categoryDto.getParentId()).isNull();
    }

    @Test
    void testGetCategoryById_WithChild() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/{id}", childCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto categoryDto = objectMapper.readValue(result.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(categoryDto.getParentId()).isEqualTo(parentCategory.getId());
        assertThat(categoryDto.getParentName()).isEqualTo("Electronics");
    }

    @Test
    void testGetCategoryById_NotFound() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
