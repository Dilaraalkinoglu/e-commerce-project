package com.dilaraalk.category.controller;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.service.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private ICategoryService categoryService;
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryService = mock(ICategoryService.class);
        categoryController = new CategoryController(categoryService);
    }

    @Test
    void getAllCategories_shouldReturnList() {
        CategoryDto cat1 = CategoryDto.builder().id(1L).name("Cat1").build();
        CategoryDto cat2 = CategoryDto.builder().id(2L).name("Cat2").build();

        when(categoryService.getAllCategories()).thenReturn(List.of(cat1, cat2));

        ResponseEntity<List<CategoryDto>> response = categoryController.getAllCategories();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(categoryService).getAllCategories();
    }

    @Test
    void getCategoryById_shouldReturnDto() {
        CategoryDto cat = CategoryDto.builder().id(1L).name("Cat1").build();

        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        ResponseEntity<CategoryDto> response = categoryController.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cat1", response.getBody().getName());
        verify(categoryService).getCategoryById(1L);
    }
}
