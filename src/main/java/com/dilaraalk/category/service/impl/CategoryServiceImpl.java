package com.dilaraalk.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.dto.UpdateCategoryRequest;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.category.service.ICategoryService;
import com.dilaraalk.common.util.SlugUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(CreateCategoryRequest request) {
        String slug = SlugUtil.generateUniqueSlug(request.getName(), categoryRepository::existsBySlug);

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .parent(parent)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToDto(saved);
    }

    @Override
    public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        String slug = SlugUtil.generateUniqueSlug(request.getName(), categoryRepository::existsBySlug);

        category.setName(request.getName());
        category.setSlug(slug);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updated = categoryRepository.save(category);
        return mapToDto(updated);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return mapToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }
}
