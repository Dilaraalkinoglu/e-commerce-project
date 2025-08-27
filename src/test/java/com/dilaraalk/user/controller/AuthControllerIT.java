package com.dilaraalk.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

public class AuthControllerIT extends BaseIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
    @Test
    void register_ok() throws Exception {
        Map<String, String> request = Map.of(
                "userName", "testUser",
                "password", "123456",
                "email", "testUser@gmail.com"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_ok() throws Exception {
        // DB’ye test kullanıcısı ekle
        User user = new User();
        user.setUserName("loginUser");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEmail("loginUser@gmail.com"); 
        user.setRoles(List.of("ROLE_USER"));
        userRepository.save(user);

        Map<String, String> loginRequest = Map.of(
                "userName", "loginUser",
                "password", "123456"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
