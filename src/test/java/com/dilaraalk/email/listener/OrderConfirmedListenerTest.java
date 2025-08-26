package com.dilaraalk.email.listener;

import com.dilaraalk.email.event.OrderConfirmedEvent;
import com.dilaraalk.email.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderConfirmedListenerTest {

    private EmailService emailService;
    private SpringTemplateEngine templateEngine;
    private OrderConfirmedListener listener;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        templateEngine = mock(SpringTemplateEngine.class);
        listener = new OrderConfirmedListener(emailService, templateEngine);
    }

    @Test
    void handleOrderConfirmed_shouldCallSendHtmlMail() throws MessagingException {
        OrderConfirmedEvent event = new OrderConfirmedEvent(this, "email@test.com",
                "User", "123", "2025-08-26", "100.0");

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<p>HTML</p>");

        listener.handleOrderConfirmed(event);

        verify(emailService, times(1)).sendHtmlMail(eq("email@test.com"), anyString(), eq("<p>HTML</p>"));
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
    }
}
