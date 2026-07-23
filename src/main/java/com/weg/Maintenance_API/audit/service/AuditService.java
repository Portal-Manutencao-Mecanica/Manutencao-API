package com.weg.Maintenance_API.audit.service;

import com.weg.Maintenance_API.audit.entity.AuditLog;
import com.weg.Maintenance_API.audit.repository.AuditLogRepository;
import com.weg.Maintenance_API.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            User actor,
            String action,
            String entityType,
            UUID entityId,
            String endpoint,
            String method,
            String ipAddress,
            String userAgent,
            boolean success,
            String details
    ) {
        auditLogRepository.save(new AuditLog(
                actor == null ? null : actor.getId(),
                actor == null ? null : actor.getEmail(),
                action,
                entityType,
                entityId,
                endpoint,
                method,
                ipAddress,
                userAgent,
                success,
                details
        ));
    }
}
