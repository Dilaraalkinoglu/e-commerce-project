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
}
