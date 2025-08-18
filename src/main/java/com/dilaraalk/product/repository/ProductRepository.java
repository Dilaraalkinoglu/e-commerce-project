package com.dilaraalk.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	boolean existsBySlug(String slug);

}