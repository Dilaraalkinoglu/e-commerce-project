package com.dilaraalk.category.service;

import java.util.List;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.dto.UpdateCategoryRequest;


public interface ICategoryService {
	
	CategoryDto createCategory(CreateCategoryRequest request);
	
	CategoryDto updateCategory(Long id, UpdateCategoryRequest request);
	
	void deleteCategory(Long id);
	
	CategoryDto getCategoryById(Long id);
	
	List<CategoryDto> getAllCategories();

}
