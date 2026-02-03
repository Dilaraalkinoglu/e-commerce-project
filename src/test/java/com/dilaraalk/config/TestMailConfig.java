package com.dilaraalk.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class TestMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    public org.thymeleaf.spring6.SpringTemplateEngine springTemplateEngine() {
        return Mockito.mock(org.thymeleaf.spring6.SpringTemplateEngine.class);
    }
}
