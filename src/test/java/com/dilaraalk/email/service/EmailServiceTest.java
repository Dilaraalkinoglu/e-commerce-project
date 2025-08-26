package com.dilaraalk.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
        emailService.from = "test@example.com"; // set @Value manually
    }

    @Test
    void sendText_shouldCallMailSender() {
        emailService.sendText("to@test.com", "Subject", "Body");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtmlMail_shouldCallMailSender() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlMail("to@test.com", "Subject", "<p>HTML</p>");
        verify(mailSender, times(1)).send(mimeMessage);
    }
}
