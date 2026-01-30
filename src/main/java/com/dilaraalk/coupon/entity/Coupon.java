package com.dilaraalk.coupon.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.dilaraalk.category.entity.Category;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.coupon.DiscountType;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotNull
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue; // indirim miktarı

    @Column(nullable = false)
    private Instant validFrom; // başlangıç tarihi

    @Column(nullable = false)
    private Instant validTo; // bitiş tarihi

    @Column(nullable = false)
    private Integer usageLimit; // toplam kullanım hakkı

    @Builder.Default
    @Column(nullable = false)
    private Integer usageCount = 0; // şu ana kadar kaç kez kullandığı

    @Version
    private Long version;

    @ManyToMany
    @JoinTable(name = "coupon_categories", joinColumns = @JoinColumn(name = "coupon_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private List<Category> applicableCategories = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "coupon_products", joinColumns = @JoinColumn(name = "coupon_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @Builder.Default
    private List<Product> applicableProducts = new ArrayList<>();

}
