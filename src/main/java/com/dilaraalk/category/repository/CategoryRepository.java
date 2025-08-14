package com.dilaraalk.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

	Optional<Category> findBySlug(String slug);
	
	Optional<Category> findByName(String name);
	
	
	//Parent kategorisine göre alt kategorileri getir
	List<Category> findByParent(Category parent);
	
	//ParendId'e göre alt kategorileri getir
	List<Category> findByParentId(Long parentId);
	
	//Benzersizlik için slug'un var olup olmadığını kontrol et 
	boolean existsBySlug(String slug);
	
}
