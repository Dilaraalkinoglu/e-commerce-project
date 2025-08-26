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

class OrderEventListenerTest {

    private EmailService emailService;
    private SpringTemplateEngine templateEngine;
    private OrderEventListener listener;

    @BeforeEach
    void setUp() {
        emailService = mock(EmailService.class);
        templateEngine = mock(SpringTemplateEngine.class);
        listener = new OrderEventListener(emailService, templateEngine);
    }

    @Test
    void handleOrderConfirmedEvent_shouldCallSendHtmlMail() throws MessagingException {
        OrderConfirmedEvent event = new OrderConfirmedEvent(this, "email@test.com",
                "User", "123", "2025-08-26", "100.0");

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<p>HTML</p>");

        listener.handleOrderConfirmedEvent(event);

        verify(emailService, times(1)).sendHtmlMail(eq("email@test.com"), contains("123"), eq("<p>HTML</p>"));
        verify(templateEngine, times(1)).process(anyString(), any(Context.class));
    }
}
