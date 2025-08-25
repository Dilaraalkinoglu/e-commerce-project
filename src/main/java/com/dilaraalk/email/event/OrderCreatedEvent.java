package com.dilaraalk.email.event;

import org.springframework.context.ApplicationEvent;

public class OrderCreatedEvent extends ApplicationEvent{
	
	private final String customerEmail;
    private final String customerName;
    private final String orderNumber;
    private final String orderDate;
    private final String totalAmount;
    
    public OrderCreatedEvent(Object source, String customerEmail, String customerName,
    		String orderNumber, String orderDate, String totalAmount) {
		super(source);
		this.customerEmail = customerEmail;
		this.customerName = customerName;
		this.orderNumber = orderNumber;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
	}
    
    public String getCustomerEmail() {
        return customerEmail;
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

}
