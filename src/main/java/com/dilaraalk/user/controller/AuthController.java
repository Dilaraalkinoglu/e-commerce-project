package com.dilaraalk.user.controller;

import java.util.Map;
import com.dilaraalk.user.dto.JwtResponse;
import com.dilaraalk.user.dto.TokenRefreshRequest;
import com.dilaraalk.user.dto.TokenRefreshResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.service.IAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final IAuthService authService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody DtoUserRegisterRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        authService.register(request);
        eventPublisher.publishEvent(new com.dilaraalk.logging.event.AuditEvent(this, request.getUserName(),
                "REGISTER_SUCCESS", "Kayıt başarılı", httpRequest.getRemoteAddr()));
        return created("Kayıt başarılı");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody DtoLoginRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            JwtResponse response = authService.login(request);
            eventPublisher.publishEvent(new com.dilaraalk.logging.event.AuditEvent(this, request.getUserName(),
                    "LOGIN_SUCCESS", "Giriş başarılı", httpRequest.getRemoteAddr()));
            return ok(response);
        } catch (Exception e) {
            eventPublisher.publishEvent(new com.dilaraalk.logging.event.AuditEvent(this, request.getUserName(),
                    "LOGIN_FAILURE", e.getMessage(), httpRequest.getRemoteAddr()));
            throw e;
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ok(authService.refreshToken(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.forgotPassword(email);
        return ok("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.");
    }
}
