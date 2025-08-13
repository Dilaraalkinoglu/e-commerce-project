package com.dilaraalk.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.service.IAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final IAuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody DtoUserRegisterRequest request) {
        authService.register(request);
        return created("Kayıt başarılı");
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody DtoLoginRequest request) {
        String token = authService.login(request);
        return ok(Map.of("token", token));
    }
}
