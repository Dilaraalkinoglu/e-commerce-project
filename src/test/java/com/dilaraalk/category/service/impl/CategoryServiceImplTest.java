package com.dilaraalk.category.service.impl;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.dto.UpdateCategoryRequest;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void createCategory_shouldReturnDto() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Electronics");

        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDto dto = categoryService.createCategory(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_shouldModifyAndReturnDto() {
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setName("NewName");

        Category existing = new Category();
        existing.setId(2L);
        existing.setName("OldName");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsBySlug(anyString())).thenReturn(false);
        when(categoryRepository.save(existing)).thenReturn(existing);

        CategoryDto dto = categoryService.updateCategory(2L, request);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        verify(categoryRepository).save(existing);
    }

    @Test
    void deleteCategory_shouldCallDelete() {
        Category existing = new Category();
        existing.setId(3L);

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(existing));

        categoryService.deleteCategory(3L);

        verify(categoryRepository).delete(existing);
    }

    @Test
    void getCategoryById_shouldReturnDto() {
        Category existing = new Category();
        existing.setId(4L);
        existing.setName("Books");

        when(categoryRepository.findById(4L)).thenReturn(Optional.of(existing));

        CategoryDto dto = categoryService.getCategoryById(4L);

        assertNotNull(dto);
        assertEquals("Books", dto.getName());
    }

    @Test
    void getAllCategories_shouldReturnList() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("C1");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("C2");

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<CategoryDto> categories = categoryService.getAllCategories();

        assertEquals(2, categories.size());
    }
}
