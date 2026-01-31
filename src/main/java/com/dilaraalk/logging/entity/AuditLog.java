package com.dilaraalk.logging.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // işlemi yapan veya yapmaya çalışan
    private String username;

    // eylem türü: LOGIN_FAILURE, LOGIN_SUCCESS, ORDER_STATUS_CHANGE
    private String action;

    // hata mesajı veya detaylı bilgi
    private String details;

    // isteğin geldiği IP
    private String ipAddress;

    private LocalDateTime timestamp;

}
