package com.dilaraalk.product.specification;

import com.dilaraalk.product.dto.ProductSearchRequest;
import com.dilaraalk.product.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;


public class ProductSpecification {

    public static Specification<Product> getProducts(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {

            // Başlangıçta null predicate (tüm sonuçları getir)
            jakarta.persistence.criteria.Predicate predicate = criteriaBuilder.conjunction();

           
            if (request.getName() != null && !request.getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
            }

            
            if (request.getSlug() != null && !request.getSlug().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("slug")), "%" + request.getSlug().toLowerCase() + "%"));
            }

     
            if (request.getMinPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.ge(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.le(root.get("price"), request.getMaxPrice()));
            }

 
            if (request.getInStock() != null) {
                if (request.getInStock()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThan(root.get("stock"), 0));
                } else {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(root.get("stock"), 0));
                }
            }

          
            if (request.getCategoryId() != null) {
                Join<Object, Object> categories = root.join("categories", JoinType.INNER);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(categories.get("id"), request.getCategoryId()));
            }

            return predicate;
        };
    }

}
