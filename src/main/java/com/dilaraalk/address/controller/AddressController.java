package com.dilaraalk.address.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.service.IAddressService;
import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/me/address")
@RequiredArgsConstructor
public class AddressController extends BaseController{

    private final IAddressService addressService;

    // tüm kullanıcı adreslerini listeler
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ok(addressService.getAddresses(userDetails.getId()));
    }

    // yeni adres ekler
    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @Valid @RequestBody AddressRequestDto request) {
        return ok(addressService.addAddress(userDetails.getId(), request));
    }

    // mevcut adresi günceller
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody AddressRequestDto request) {
        return ok(addressService.updateAddress(userDetails.getId(), id, request));
    }

    // adresi siler
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
	
}
