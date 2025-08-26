package com.dilaraalk.user.controller;

import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.service.IUserService;
import com.dilaraalk.user.service.impl.CustomUserDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class UserControllerTest {

    private IUserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(IUserService.class); // sahte servis
        userController = new UserController(userService); // controller'ı kendimiz new'liyoruz
    }

    @Test
    void testGetProfile() {
        // Hazırlık
        UserProfileResponseDto fakeResponse = new UserProfileResponseDto();
        fakeResponse.setEmail("test@example.com");
        fakeResponse.setUserName("Test User");

        Mockito.when(userService.getProfile(anyLong())).thenReturn(fakeResponse);

        // Sahte kullanıcı
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@example.com", "password", null, null);

        // Çağır
        var response = userController.getProfile(userDetails);

        // Doğrula
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("Test User", response.getBody().getUserName());
    }

    @Test
    void testUpdateProfile() {
        // Hazırlık
        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto();
        request.setUserName("Updated User");
        request.setEmail("updated@example.com");

        UserProfileResponseDto fakeResponse = new UserProfileResponseDto();
        fakeResponse.setEmail("updated@example.com");
        fakeResponse.setUserName("Updated User");

        Mockito.when(userService.updateProfile(anyLong(), any(UserProfileUpdateRequestDto.class)))
               .thenReturn(fakeResponse);

        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@example.com", "password", null, null);

        var response = userController.updateProfile(userDetails, request);

        assertEquals("updated@example.com", response.getBody().getEmail());
        assertEquals("Updated User", response.getBody().getUserName());
    }
}
