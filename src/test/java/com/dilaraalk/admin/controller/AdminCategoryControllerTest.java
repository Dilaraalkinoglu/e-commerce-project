package com.dilaraalk.admin.controller;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.service.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCategoryControllerTest {

    private ICategoryService categoryService;
    private AdminCategoryController controller;

    @BeforeEach
    void setUp() {
        categoryService = mock(ICategoryService.class);
        controller = new AdminCategoryController(categoryService);
    }

    @Test
    void getAllCategories_shouldReturnList() {
        when(categoryService.getAllCategories()).thenReturn(List.of(new CategoryDto(), new CategoryDto()));
        ResponseEntity<List<CategoryDto>> response = controller.getAllCategories();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        verify(categoryService).getAllCategories();
    }

    @Test
    void createCategory_shouldCallServiceAndReturnDto() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        CategoryDto dto = new CategoryDto();
        when(categoryService.createCategory(request)).thenReturn(dto);

        ResponseEntity<CategoryDto> response = controller.createCategory(request);

        assertNotNull(response);
        assertEquals(dto, response.getBody());
        verify(categoryService).createCategory(request);
    }
}
