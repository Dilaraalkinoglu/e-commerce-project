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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController{      
	
	private final IUserService userService;

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
	    return ok(userService.getProfile(userDetails.getId()));
	}
	
	
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponseDto> updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody UserProfileUpdateRequestDto request){
		return ok(userService.updateProfile(userDetails.getId(), request));
	}
	
}
