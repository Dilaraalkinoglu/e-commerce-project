package com.dilaraalk.email.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dilaraalk.email.event.OrderConfirmedEvent;
import com.dilaraalk.email.service.EmailService;

import jakarta.mail.MessagingException;


@Component
public class OrderConfirmedListener {
	
	private final EmailService emailService;
	private final SpringTemplateEngine templateEngine;
	
	public OrderConfirmedListener(EmailService emailService, SpringTemplateEngine templateEngine) {
		this.emailService = emailService;
		this.templateEngine = templateEngine;
	}
	
	@Async
	@EventListener
	public void handleOrderConfirmed(OrderConfirmedEvent event) throws MessagingException{
		Context context = new Context();
		context.setVariable("customerName", event.getCustomerName());
		context.setVariable("orderNumber", event.getOrderNumber());
		context.setVariable("orderDate", event.getOrderDate());
		context.setVariable("totalAmount", event.getTotalAmount());
		
		String body = templateEngine.process("order-confirmation.html", context);
		emailService.sendHtmlMail(event.getEmail(), "Sipariş Onayı", body);
	}

}
