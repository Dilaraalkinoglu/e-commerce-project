package com.dilaraalk.category.controller;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.service.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class, excludeAutoConfiguration = { SecurityAutoConfiguration.class })
@ActiveProfiles("test")
class CategoryControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICategoryService categoryService;

    @Test
    void getAllCategories_ShouldReturnJsonList_WhenCategoriesExist() throws Exception {
        // Arrange
        CategoryDto cat1 = CategoryDto.builder().id(1L).name("Electronics").slug("electronics").build();
        CategoryDto cat2 = CategoryDto.builder().id(2L).name("Books").slug("books").build();

        given(categoryService.getAllCategories()).willReturn(List.of(cat1, cat2));

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Books"));
    }

    @Test
    void getCategoryById_ShouldReturnJson_WhenCategoryExists() throws Exception {
        // Arrange
        CategoryDto cat = CategoryDto.builder().id(10L).name("Furniture").slug("furniture").build();

        given(categoryService.getCategoryById(10L)).willReturn(cat);

        // Act & Assert
        mockMvc.perform(get("/api/categories/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Furniture"));
    }
}
