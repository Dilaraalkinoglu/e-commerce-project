package com.dilaraalk.email.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.dilaraalk.email.event.PasswordResetEvent;
import com.dilaraalk.email.service.EmailService;

import jakarta.mail.MessagingException;

@Component
public class PasswordResetListener {

	private final EmailService emailService;
	private final SpringTemplateEngine templateEngine;

	public PasswordResetListener(EmailService emailService,
			SpringTemplateEngine templateEngine) {
		this.emailService = emailService;
		this.templateEngine = templateEngine;
	}

	@Async
	@EventListener
	public void handlePasswordReset(PasswordResetEvent event) throws MessagingException {
		Context context = new Context();
		context.setVariable("userName", event.getUserName());
		context.setVariable("resetLink", event.getResetLink());

		String body = templateEngine.process("password-reset", context);
		emailService.sendHtmlMail(event.getEmail(), "Şifre Sıfırlama Talebi", body);
	}

}
