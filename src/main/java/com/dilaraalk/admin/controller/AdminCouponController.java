package com.dilaraalk.admin.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.coupon.dto.CouponResponseDto;
import com.dilaraalk.coupon.dto.CreateCouponRequest;
import com.dilaraalk.coupon.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCouponController extends BaseController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return created(couponService.createCoupon(request));
    }

    @GetMapping
    public ResponseEntity<List<CouponResponseDto>> getAllCoupons() {
        return ok(couponService.getAllCoupons());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return noContent();
    }
}
