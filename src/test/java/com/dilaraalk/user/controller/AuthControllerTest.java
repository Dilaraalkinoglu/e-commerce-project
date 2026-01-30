package com.dilaraalk.user.controller;

import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.service.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.dilaraalk.user.dto.JwtResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreatedMessage() {
        // arrange
        DtoUserRegisterRequest request = new DtoUserRegisterRequest();

        // act
        ResponseEntity<String> response = authController.register(request);

        // assert
        verify(authService).register(request);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Kayıt başarılı", response.getBody());
    }

    @Test
    void login_ShouldReturnTokenInResponse() {
        // arrange
        DtoLoginRequest request = new DtoLoginRequest();
        JwtResponse mockResponse = new JwtResponse();
        mockResponse.setAccessToken("jwt-token");

        when(authService.login(request)).thenReturn(mockResponse);

        // act
        ResponseEntity<JwtResponse> response = authController.login(request);

        // assert
        verify(authService).login(request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("jwt-token", response.getBody().getAccessToken());
    }
}
