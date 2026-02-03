package com.dilaraalk.admin.controller;

import com.dilaraalk.category.dto.CategoryDto;
import com.dilaraalk.category.dto.CreateCategoryRequest;
import com.dilaraalk.category.service.ICategoryService;
import com.dilaraalk.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCategoryController.class)
@ActiveProfiles("test")
class AdminCategoryControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICategoryService categoryService;

    // --- Security & Filter Dependencies (Mocked) ---
    @MockBean
    private com.dilaraalk.common.metrics.MetricService metricService;
    @MockBean
    private com.dilaraalk.common.rateLimiting.RateLimitingService rateLimitingService;
    @MockBean
    private com.dilaraalk.user.util.JwtUtil jwtUtil;
    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockBean
    private UserRepository userRepository;
    // ------------------------------------------------

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void createCategory_ShouldReturnCreated_WhenAdminUserAndValidRequest() throws Exception {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("New Category");

        CategoryDto responseDto = CategoryDto.builder()
                .id(1L)
                .name("New Category")
                .slug("new-category")
                .build();

        given(categoryService.createCategory(any(CreateCategoryRequest.class))).willReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                .with(csrf()) // POST isteklerinde CSRF koruması olabilir, testte bypass etmek için
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Controller ResponseEntity.ok() dönüyor
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    @WithMockUser(username = "user", roles = { "USER" }) // Yetkisiz kullanıcı
    void createCategory_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {
        // Arrange
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Hacker Category");

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // 403 Bekliyoruz
    }
}
