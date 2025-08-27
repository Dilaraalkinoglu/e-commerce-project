package com.dilaraalk.user.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

class UserDetailsServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUserName("yamanUser");
        testUser.setPassword("123456");
        testUser.setEmail("yamanUser@gmail.com");
        testUser.setRoles(List.of("ROLE_USER"));
        userRepository.save(testUser);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        CustomUserDetails details = (CustomUserDetails) userDetailsService.loadUserByUsername("yamanUser");

        assertEquals(testUser.getUserName(), details.getUsername());
        assertEquals(testUser.getEmail(), details.getEmail());
        assertEquals(testUser.getRoles().size(), details.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionForNonExistingUser() {
        assertThrows(Exception.class, () -> userDetailsService.loadUserByUsername("nonExistingUser"));
    }
}
