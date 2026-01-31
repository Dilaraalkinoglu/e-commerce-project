package com.dilaraalk.logging.listener;

import java.time.LocalDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dilaraalk.logging.entity.AuditLog;
import com.dilaraalk.logging.event.AuditEvent;
import com.dilaraalk.logging.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    @Async // Loglama işlemi ana akışı yavaşlatmasın
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        AuditLog auditLog = AuditLog.builder()
                .username(event.getUsername())
                .action(event.getAction())
                .details(event.getDetails())
                .ipAddress(event.getIpAddress())
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }

}
