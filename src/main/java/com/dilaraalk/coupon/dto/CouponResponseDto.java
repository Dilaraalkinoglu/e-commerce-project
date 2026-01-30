package com.dilaraalk.coupon.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.dilaraalk.coupon.DiscountType;
import com.dilaraalk.coupon.entity.Coupon;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.product.entity.Product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponResponseDto {

    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private Integer usageLimit;
    private Integer usageCount;
    private Instant validFrom;
    private Instant validTo;
    private List<Long> applicableCategoryIds;
    private List<Long> applicableProductIds;

    public static CouponResponseDto fromEntity(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .validFrom(coupon.getValidFrom())
                .validTo(coupon.getValidTo())
                .applicableCategoryIds(coupon.getApplicableCategories() != null
                        ? coupon.getApplicableCategories().stream().map(Category::getId).collect(Collectors.toList())
                        : List.of())
                .applicableProductIds(coupon.getApplicableProducts() != null
                        ? coupon.getApplicableProducts().stream().map(Product::getId).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
