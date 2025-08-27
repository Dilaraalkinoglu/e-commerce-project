package com.dilaraalk.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.user.util.JwtUtil;

@SpringBootTest
public class AdminControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void getAdminDashboard_withAdminToken_shouldReturn200() throws Exception {
        // Test admin kullanıcısını oluştur
        User admin = new User();
        admin.setUserName("adminTest");
        admin.setPassword("password"); // encode etmeye gerek yok testte
        admin.setEmail("admin@test.com");
        admin.setRoles(List.of("ROLE_ADMIN"));
        userRepository.save(admin);

        // JWT token oluştur
        String token = jwtUtil.generateToken(admin.getUserName(), admin.getRoles());

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Sadece ADMIN rolü bu mesajı görebilir."));
    }
}
