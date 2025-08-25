package com.dilaraalk.email.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dilaraalk.email.event.OrderConfirmedEvent;
import com.dilaraalk.email.event.PasswordResetEvent;
import com.dilaraalk.email.service.EmailService;

import jakarta.mail.MessagingException;



@Profile("dev")
@RestController
@RequestMapping("/__dev__/mail")
public class DevMailTestController {

    private final EmailService emailService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    private final ApplicationEventPublisher publisher;

    @Autowired
    public DevMailTestController(EmailService emailService, ApplicationEventPublisher publisher) {
        this.emailService = emailService;
        this.publisher = publisher;
    }
    
    @PostMapping("/test-password-reset-event")
    public String testPasswordResetEvent() {
        publisher.publishEvent(new PasswordResetEvent(
                this,
                "test@example.com",
                "Dilara",
                "http://localhost:8080/reset-password?token=123456"
        ));
        return "PasswordResetEvent yayınlandı!";
    }

    
    @PostMapping("/test-order-event")
    public String testOrderEvent() {
        publisher.publishEvent(new OrderConfirmedEvent(
                this,
                "test@example.com",  // Maili kime göndereceğiz
                "Dilara",            // Müşteri adı
                "12345",             // Sipariş numarası
                "22.08.2025",        // Tarih
                "200 TL"             // Toplam
        ));
        return "OrderConfirmedEvent yayınlandı!";
    }



    @PostMapping("/test")
    public String sendTest(@RequestParam String to) {
        emailService.sendText(to, "Smoke Test", "Bu bir dev smoke test e-postasıdır.");
        return "OK";
    }
    
    @GetMapping("/__dev__/mail/test-template")
    public ResponseEntity<String> testTemplateMail() throws MessagingException {
        String subject = "Sipariş Onayı Test";
        String to = "test@example.com";

        Context context = new Context();
        context.setVariable("customerName", "Dilara");
        context.setVariable("orderNumber", "12345");
        context.setVariable("orderDate", "22.08.2025");
        context.setVariable("totalAmount", "200 TL");

        String body = templateEngine.process("order-confirmation.html", context);

        emailService.sendHtmlMail(to, subject, body);

        return ResponseEntity.ok("Test mail gönderildi!");
    }

}

