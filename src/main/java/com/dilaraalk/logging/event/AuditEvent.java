package com.dilaraalk.logging.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class AuditEvent extends ApplicationEvent {

    private final String username;
    private final String action;
    private final String details;
    private final String ipAddress;

    public AuditEvent(Object source, String username, String action, String details, String ipAddress) {
        super(source);
        this.username = username;
        this.action = action;
        this.details = details;
        this.ipAddress = ipAddress;
    }

}
