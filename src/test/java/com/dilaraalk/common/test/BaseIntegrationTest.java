package com.dilaraalk.common.test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.dilaraalk.ECommerceProjectApplication;
import com.dilaraalk.common.metrics.MetricFilter;
import com.dilaraalk.common.metrics.MetricService;
import com.dilaraalk.email.service.EmailService;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.service.impl.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;

@SpringBootTest(classes = {
        ECommerceProjectApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Testcontainers
public abstract class BaseIntegrationTest {

    // Docker üzerinde bir PostgreSQL ayağa kaldırır
    @Container
    static PostgreSQLContainer<?> postgress = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // spring'e docker'daki DB'nin dinamik URL'ini bildirir.
        registry.add("spring.datasource.url", postgress::getJdbcUrl);
        registry.add("spring.datasource.username", postgress::getUsername);
        registry.add("spring.datasource.password", postgress::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        // Testlerde Redis'e bağlanmaya çalışmaması için Cache'i kapatıyoruz
        registry.add("spring.cache.type", () -> "none");
        // Redis host ayarını geçersiz bir yere yönlendirebiliriz veya mevcut
        // bırakabiliriz,
        // ama spring.data.redis.repositories.enabled=false yapmak işe yarayabilir.
        // Ancak en garantisi Testcontainers ile Redis kaldırmaktır.
        // Şimdilik sadece cache disable yaparak deneyelim.
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private MetricService metricService;

    @MockBean
    private MetricFilter metricFilter;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected JavaMailSender javaMailSender;

    @MockBean
    protected EmailService emailService;

    // Mail servislerini mockluyoruz ki test sırasında gerçek mail gitmesin
    @BeforeEach
    public void initMocks() throws MessagingException {
        Mockito.doAnswer(invocation -> null)
                .when(emailService)
                .sendHtmlMail(Mockito.any(), Mockito.any(), Mockito.any());
    }

    protected SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor mockUser(String username, String... roles) {
        return SecurityMockMvcRequestPostProcessors.user(username).roles(roles);
    }

    protected RequestPostProcessor mockCustomUser(User user, String... roles) {
        return request -> {
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            // rollerin GrantedAuthority olarak eklenmesi
            var authorities = java.util.Arrays.stream(roles)
                    .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r))
                    .toList();

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            return request;
        };
    }

}
