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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/me/address")
@RequiredArgsConstructor
public class AddressController extends BaseController{

    private final IAddressService addressService;

    // tüm kullanıcı adreslerini listeler
    @Operation(summary = "Kullanıcının adreslerini getir", description = "Giriş yapmış kullanıcının kayıtlı adreslerini listeler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Adresler başarıyla getirildi"),
        @ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli")
    })
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ok(addressService.getAddresses(userDetails.getId()));
    }

    // yeni adres ekler
    @Operation(summary = "Yeni adres ekle", description = "Giriş yapmış kullanıcıya yeni bir adres ekler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Adres başarıyla eklendi"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
        @ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli")
    })
    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @Valid @RequestBody AddressRequestDto request) {
        return ok(addressService.addAddress(userDetails.getId(), request));
    }

    // mevcut adresi günceller
    @Operation(summary = "Adres güncelle", description = "Belirtilen ID'ye sahip adresi günceller.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Adres başarıyla güncellendi"),
        @ApiResponse(responseCode = "400", description = "Geçersiz istek"),
        @ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli"),
        @ApiResponse(responseCode = "404", description = "Adres bulunamadı")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody AddressRequestDto request) {
        return ok(addressService.updateAddress(userDetails.getId(), id, request));
    }

    // adresi siler
    @Operation(summary = "Adres sil", description = "Belirtilen ID'ye sahip adresi siler.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Adres başarıyla silindi"),
        @ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli"),
        @ApiResponse(responseCode = "404", description = "Adres bulunamadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @PathVariable Long id) {
        addressService.deleteAddress(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
	
    
    
}
