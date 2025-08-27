package com.dilaraalk.user.service.impl;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

class UserServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // DB temizleniyor

        testUser = new User();
        testUser.setUserName("yaman1");
        testUser.setEmail("yaman1@gmail.com");
        testUser.setPassword("123456");
        userRepository.save(testUser);
    }

    @Test
    void getProfile_shouldReturnCorrectUser() {
        UserProfileResponseDto profile = userService.getProfile(testUser.getId());

        assertEquals(testUser.getUserName(), profile.getUserName());
        assertEquals(testUser.getEmail(), profile.getEmail());
    }

    @Test
    void updateProfile_shouldUpdateUser() {
        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto();
        request.setUserName("yamanUpdated");
        request.setEmail("yamanUpdated@gmail.com");

        UserProfileResponseDto updated = userService.updateProfile(testUser.getId(), request);

        assertEquals("yamanUpdated", updated.getUserName());
        assertEquals("yamanUpdated@gmail.com", updated.getEmail());
    }

    @Test
    void findByUserName_shouldReturnUser() {
        User user = userService.findByUserName(testUser.getUserName()).orElse(null);

        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getEmail());
    }
}



