package com.dilaraalk.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dilaraalk.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	boolean existsBySlug(String slug);

	@org.springframework.data.jpa.repository.EntityGraph(attributePaths = { "categories", "images" })
	@org.springframework.data.jpa.repository.Query("SELECT p FROM Product p")
	java.util.List<Product> findAllWithRelations();

}