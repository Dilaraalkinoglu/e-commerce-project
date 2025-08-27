package com.dilaraalk.user.controller;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User u = new User();
        u.setUserName("yaman");
        u.setEmail("yaman1@gmail.com");
        u.setPassword("{noop}123456");
        savedUser = userRepository.save(u);
    }

    @Test
    @DisplayName("GET /api/user/me -> 200 ve profil DTO'su döner")
    @WithMockUser(username = "yaman")
    void getProfile_ok() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    @DisplayName("PUT /api/user/me -> 200 ve güncellenmiş DTO döner; DB de güncellenir")
    @WithMockUser(username = "yaman")
    void updateProfile_ok() throws Exception {
        UserProfileUpdateRequestDto req = new UserProfileUpdateRequestDto();
        req.setUserName("dilara_updated");
        req.setEmail("newmail@ex.com");

        mockMvc.perform(
                put("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userName").value("dilara_updated"))
        .andExpect(jsonPath("$.email").value("newmail@ex.com"));

        User fromDb = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(fromDb.getUserName()).isEqualTo("dilara_updated");
        assertThat(fromDb.getEmail()).isEqualTo("newmail@ex.com");
    }

    @Test
    @DisplayName("GET /api/user/me -> auth yoksa 401 döner")
    void getProfile_unauthorized() throws Exception {
        mockMvc.perform(get("/api/user/me"))
               .andExpect(status().isUnauthorized());
    }
}
