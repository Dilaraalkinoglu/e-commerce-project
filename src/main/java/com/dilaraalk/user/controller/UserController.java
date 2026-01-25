package com.dilaraalk.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

	private final IUserService userService;

	@Operation(summary = "Kullanıcı profilini getir", description = "Giriş yapmış kullanıcının profil bilgilerini döner.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profil başarıyla getirildi"),
			@ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli")
	})
	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return ok(userService.getProfile(userDetails.getId()));
	}

	@Operation(summary = "Profil güncelle", description = "Giriş yapmış kullanıcının profil bilgilerini günceller.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profil başarıyla güncellendi"),
			@ApiResponse(responseCode = "400", description = "Geçersiz istek"),
			@ApiResponse(responseCode = "401", description = "Yetkisiz erişim, JWT gerekli")
	})
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponseDto> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UserProfileUpdateRequestDto request) {
		return ok(userService.updateProfile(userDetails.getId(), request));
	}

	@org.springframework.web.bind.annotation.PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody com.dilaraalk.user.dto.UserPasswordUpdateRequestDto request) {
		userService.updatePassword(userDetails.getId(), request);
		return ResponseEntity.ok().build();
	}

}
