package com.dilaraalk.product.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.dilaraalk.category.entity.Category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "product_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "product_stock", nullable = false)
    private int stock;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_categories",
               joinColumns = @JoinColumn(name = "product_id"),
               inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ProductImage> images = new HashSet<>();

    public void addImage(ProductImage img) {
        images.add(img);
        img.setProduct(this);
    }

    public void removeImage(ProductImage img) {
        images.remove(img);
        img.setProduct(null);
    }

    @Column(nullable = false, unique = true)
    private String slug;
}
