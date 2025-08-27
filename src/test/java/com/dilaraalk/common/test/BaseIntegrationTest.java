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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import com.dilaraalk.ECommerceProjectApplication;
import com.dilaraalk.common.metrics.MetricFilter;
import com.dilaraalk.common.metrics.MetricService;
import com.dilaraalk.email.service.EmailService;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.service.impl.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;

@SpringBootTest(classes = {ECommerceProjectApplication.class, TestDataSourceConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

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
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            return request;
        };
    }



}
