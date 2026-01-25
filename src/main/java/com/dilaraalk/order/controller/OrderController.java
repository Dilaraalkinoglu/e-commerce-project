package com.dilaraalk.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.order.dto.CheckoutResponseDto;
import com.dilaraalk.order.service.IOrderService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final IOrderService orderService;

    @Operation(summary = "Get My Orders", description = "Retrieves the list of orders for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/my-orders")
    public ResponseEntity<List<CheckoutResponseDto>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ok(orderService.getUserOrders(userDetails.getId()));
    }
}
