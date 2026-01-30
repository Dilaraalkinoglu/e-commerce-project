package com.dilaraalk.coupon.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.dilaraalk.coupon.DiscountType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCouponRequest {

    @NotBlank(message = "Kupon kodu boş olamaz")
    private String code;

    @NotNull(message = "İndirim tipi seçilmelidir")
    private DiscountType discountType;

    @NotNull(message = "İndirim değeri boş olamaz")
    @Min(value = 0, message = "İndirim değeri negatif olamaz")
    private BigDecimal discountValue;

    @NotNull(message = "Kullanım limiti boş olamaz")
    @Min(value = 1, message = "Kullanım limiti en az 1 olmalıdır")
    private Integer usageLimit;

    @NotNull(message = "Başlangıç tarihi boş olamaz")
    private Instant validFrom;

    @NotNull(message = "Bitiş tarihi boş olamaz")
    @Future(message = "Bitiş tarihi gelecekte olmalıdır")
    private Instant validTo;

    private List<Long> applicableCategoryIds;
    private List<Long> applicableProductIds;
}
