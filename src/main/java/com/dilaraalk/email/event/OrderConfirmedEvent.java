package com.dilaraalk.email.event;

import org.springframework.context.ApplicationEvent;

public class OrderConfirmedEvent extends ApplicationEvent{
	
	private final String customerName;
	private final String orderNumber;
	private final String orderDate;
	private final String totalAmount;
	private final String email;

	public OrderConfirmedEvent(Object source, String email, String customerName,
			String orderNumber, String orderDate, String totalAmount) {
		super(source);
		this.email = email;
		this.customerName =customerName;
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
	}
	
	public String getCustomerName() {
		return customerName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	
	public String getOrderDate() {
		return orderDate;
	}
	
	public String getTotalAmount() {
		return totalAmount;
	}
	
	public String getEmail() {
		return email;
	}
}
