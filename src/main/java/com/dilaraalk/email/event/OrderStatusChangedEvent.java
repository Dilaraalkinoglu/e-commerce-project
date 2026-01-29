package com.dilaraalk.email.event;

import org.springframework.context.ApplicationEvent;

public class OrderStatusChangedEvent extends ApplicationEvent {

    private final String email;
    private final String customerName;
    private final String orderNumber;
    private final String newStatus;

    public OrderStatusChangedEvent(Object source, String email, String customerName, String orderNumber,
            String newStatus) {
        super(source);
        this.email = email;
        this.customerName = customerName;
        this.orderNumber = orderNumber;
        this.newStatus = newStatus;
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getNewStatus() {
        return newStatus;
    }

}
