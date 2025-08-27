package com.dilaraalk.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.entity.User;


class UserRepositoryIT extends BaseIntegrationTest {

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
    void findByUserName_shouldReturnUser() {
        User user = userRepository.findByUserName("yaman1").orElse(null);

        assertNotNull(user);
        assertEquals("yaman1@gmail.com", user.getEmail());
    }

    @Test
    void save_shouldPersistUser() {
        User newUser = new User();
        newUser.setUserName("yeniUser");
        newUser.setEmail("yeni@gmail.com");
        newUser.setPassword("password");
        userRepository.save(newUser);

        assertNotNull(userRepository.findByUserName("yeniUser").orElse(null));
    }
}
