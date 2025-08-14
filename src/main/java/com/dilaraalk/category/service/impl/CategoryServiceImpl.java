package com.dilaraalk.category.service.impl;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.dto.UpdateCategoryRequest;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.category.service.ICategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService{
	
	private final CategoryRepository categoryRepository;
	
	public CategoryDto createCategory(CreateCategoryRequest request) {
		if (categoryRepository.existsBySlug(generateSlug(request.getName()))) {
			throw new RuntimeException("Category with this name already exists");
		}
		
		Category parent = null;
		if (request.getParentId() != null) {
			parent = categoryRepository.findById(request.getParentId())
					.orElseThrow(() -> new RuntimeException("Parent category not found with id: "+ request.getParentId()));
		}
		
		Category category = Category.builder()
				.name(request.getName())
				.slug(generateUniqueSlug(request.getName()))
				.parent(parent)
				.build();
		
		Category saved = categoryRepository.save(category);
		return mapToDto(saved);
	}

	@Override
	public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(request.getName());
        category.setSlug(generateUniqueSlug(request.getName()));

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
	
	private String generateSlug(String input) {
		String nowhitespace = input.trim().replace("\\s+", "-");
		String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("").toLowerCase(Locale.ENGLISH);
        return slug;
	}
	
	 private String generateUniqueSlug(String name) {
	        String baseSlug = generateSlug(name);
	        String slug = baseSlug;
	        int counter = 1;
	        while (categoryRepository.existsBySlug(slug)) {
	            slug = baseSlug + "-" + counter++;
	        }
	        return slug;
	    }

	
	

}
