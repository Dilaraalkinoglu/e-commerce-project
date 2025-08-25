package com.dilaraalk.email.event;

import org.springframework.context.ApplicationEvent;

public class PasswordResetEvent extends ApplicationEvent{

	private final String email;
	private final String userName;
	private final String resetLink;
	
	public PasswordResetEvent(Object source, String email,
			String userName, String resetLink) {
		super(source);
		this.email = email;
		this.userName = userName;
		this.resetLink = resetLink;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getResetLink() {
		return resetLink;
	}
	
}
