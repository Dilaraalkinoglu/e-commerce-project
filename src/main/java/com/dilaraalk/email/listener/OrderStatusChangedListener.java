package com.dilaraalk.email.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dilaraalk.email.event.OrderStatusChangedEvent;
import com.dilaraalk.email.service.EmailService;

import jakarta.mail.MessagingException;

@Component
public class OrderStatusChangedListener {

    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;

    public OrderStatusChangedListener(EmailService emailService, SpringTemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    @Async
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) throws MessagingException {
        Context context = new Context();
        context.setVariable("customerName", event.getCustomerName());
        context.setVariable("orderNumber", event.getOrderNumber());
        context.setVariable("newStatus", translateStatus(event.getNewStatus()));

        String body = templateEngine.process("order-status-update", context);
        emailService.sendHtmlMail(event.getEmail(), "Sipariş Durum Güncellemesi #" + event.getOrderNumber(), body);
    }

    private String translateStatus(String status) {
        switch (status) {
            case "PENDING":
                return "Onay Bekliyor";
            case "PAID":
                return "Ödeme Alındı, Hazırlanıyor";
            case "SHIPPED":
                return "Kargoya Verildi";
            case "DELIVERED":
                return "Teslim Edildi";
            case "CANCELLED":
                return "İptal Edildi";
            default:
                return status;
        }
    }
}
