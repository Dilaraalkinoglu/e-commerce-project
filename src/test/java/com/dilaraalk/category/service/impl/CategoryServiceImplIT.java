package com.dilaraalk.category.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.dto.UpdateCategoryRequest;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Transactional
class CategoryServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category parentCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        categoryRepository.flush(); // DB temizleme sonrası flush

        parentCategory = categoryRepository.save(
                Category.builder().name("Electronics").slug("electronics").build()
        );
        categoryRepository.flush(); // parentCategory kaydını garantile
    }

    @Test
    void testCreateCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Mobile Phones");
        request.setParentId(parentCategory.getId());

        CategoryDto dto = categoryService.createCategory(request);
        categoryRepository.flush(); // kaydı garantile

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo("Mobile Phones");
        assertThat(dto.getParentId()).isEqualTo(parentCategory.getId());
    }

    @Test
    void testUpdateCategory() {
        CategoryDto created = categoryService.createCategory(
                new CreateCategoryRequest("Laptops", parentCategory.getId())
        );
        categoryRepository.flush();

        UpdateCategoryRequest updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Gaming Laptops");
        updateRequest.setParentId(null);

        CategoryDto updated = categoryService.updateCategory(created.getId(), updateRequest);
        categoryRepository.flush();

        assertThat(updated.getName()).isEqualTo("Gaming Laptops");
        assertThat(updated.getParentId()).isNull();
    }

    @Test
    void testDeleteCategory() {
        CategoryDto created = categoryService.createCategory(
                new CreateCategoryRequest("Tablets", parentCategory.getId())
        );
        categoryRepository.flush();

        categoryService.deleteCategory(created.getId());
        categoryRepository.flush();

        assertThat(categoryRepository.existsById(created.getId())).isFalse();
    }

    @Test
    void testGetCategoryById() {
        CategoryDto created = categoryService.createCategory(
                new CreateCategoryRequest("Cameras", parentCategory.getId())
        );
        categoryRepository.flush();

        CategoryDto dto = categoryService.getCategoryById(created.getId());

        assertThat(dto.getName()).isEqualTo("Cameras");
        assertThat(dto.getParentId()).isEqualTo(parentCategory.getId());
    }

    @Test
    void testGetAllCategories() {
        categoryService.createCategory(new CreateCategoryRequest("TVs", null));
        categoryService.createCategory(new CreateCategoryRequest("Headphones", null));
        categoryRepository.flush();

        List<CategoryDto> allCategories = categoryService.getAllCategories();

        assertThat(allCategories).hasSize(3); // parentCategory + 2 yeni kategori
    }

    @Test
    void testGetCategoryById_NotFound() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(999L);
        });

        assertThat(exception.getMessage()).contains("Category not found");
    }
}
