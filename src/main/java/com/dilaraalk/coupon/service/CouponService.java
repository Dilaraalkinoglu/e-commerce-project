package com.dilaraalk.coupon.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dilaraalk.cart.entity.Cart;
import com.dilaraalk.cart.entity.CartItem;
import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.coupon.DiscountType;
import com.dilaraalk.coupon.dto.CouponResponseDto;
import com.dilaraalk.coupon.dto.CreateCouponRequest;
import com.dilaraalk.coupon.entity.Coupon;
import com.dilaraalk.coupon.repository.CouponRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CouponResponseDto createCoupon(CreateCouponRequest request) {
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Bu kupon kodu zaten kullanımda: " + request.getCode());
        }

        List<Category> categories = new ArrayList<>();
        if (request.getApplicableCategoryIds() != null && !request.getApplicableCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getApplicableCategoryIds());
        }

        List<Product> products = new ArrayList<>();
        if (request.getApplicableProductIds() != null && !request.getApplicableProductIds().isEmpty()) {
            products = productRepository.findAllById(request.getApplicableProductIds());
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .usageLimit(request.getUsageLimit())
                .usageCount(0)
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .applicableCategories(categories)
                .applicableProducts(products)
                .build();

        return CouponResponseDto.fromEntity(couponRepository.save(coupon));
    }

    public List<CouponResponseDto> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CouponResponseDto> getActiveCoupons() {
        Instant now = Instant.now();
        return couponRepository.findByValidToAfter(now).stream()
                .filter(coupon -> coupon.getUsageCount() < coupon.getUsageLimit())
                .filter(coupon -> coupon.getValidFrom().isBefore(now))
                .map(CouponResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public BigDecimal calculateDiscount(String code, Cart cart) {
        Coupon coupon = validateCoupon(code);

        BigDecimal applicableAmount = BigDecimal.ZERO;

        // Eğer kategori ve ürün kısıtlaması yoksa tüm sepete uygula (Global)
        boolean isGlobal = (coupon.getApplicableCategories() == null || coupon.getApplicableCategories().isEmpty()) &&
                (coupon.getApplicableProducts() == null || coupon.getApplicableProducts().isEmpty());

        if (isGlobal) {
            applicableAmount = cart.getItems().stream()
                    .map(CartItem::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            // Sadece uygun ürünlerin tutarını topla
            for (CartItem item : cart.getItems()) {
                boolean isApplicable = false;

                // Kategori kontrolü
                if (coupon.getApplicableCategories() != null && !coupon.getApplicableCategories().isEmpty()) {
                    // Ürünün herhangi bir kategorisi, kuponun herhangi bir kategorisiyle eşleşiyor
                    // mu?
                    boolean categoryMatch = item.getProduct().getCategories().stream()
                            .anyMatch(prodCat -> coupon.getApplicableCategories().stream()
                                    .anyMatch(couponCat -> couponCat.getId().equals(prodCat.getId())));

                    if (categoryMatch) {
                        isApplicable = true;
                    }
                }

                // Ürün kontrolü (Kategori eşleşmese bile ürün eşleşiyorsa kabul et)
                if (!isApplicable && coupon.getApplicableProducts() != null &&
                        coupon.getApplicableProducts().stream()
                                .anyMatch(p -> p.getId().equals(item.getProduct().getId()))) {
                    isApplicable = true;
                }

                if (isApplicable) {
                    applicableAmount = applicableAmount.add(item.getSubTotal());
                }
            }
        }

        if (applicableAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO; // İndirim uygulanacak ürün yok (veya toplam tutar 0)
        }

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            // Yüzde hesaplama
            return applicableAmount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            // Sabit tutar (Maksimum uygulanabilir tutar kadar)
            return coupon.getDiscountValue().min(applicableAmount);
        }
    }

    @Transactional
    public void useCoupon(String code) {
        Coupon coupon = validateCoupon(code);
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);
    }

    private Coupon validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Kupon bulunamadı: " + code));

        if (coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new RuntimeException("Kupon kullanım limiti doldu!");
        }

        Instant now = Instant.now();
        if (now.isBefore(coupon.getValidFrom())) {
            throw new RuntimeException("Kupon henüz başlamadı!");
        }

        if (now.isAfter(coupon.getValidTo())) {
            throw new RuntimeException("Kupon süresi doldu!");
        }

        return coupon;
    }
}
