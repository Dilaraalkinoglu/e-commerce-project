package com.dilaraalk.coupon.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.coupon.dto.CouponResponseDto;
import com.dilaraalk.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController extends BaseController {

    private final CouponService couponService;

    @GetMapping("/active")
    public ResponseEntity<List<CouponResponseDto>> getActiveCoupons() {
        return ok(couponService.getActiveCoupons());
    }
}
