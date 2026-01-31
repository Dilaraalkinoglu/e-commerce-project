package com.dilaraalk.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.logging.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
