package com.company.ems.service;

import com.company.ems.entity.AuditLog;
import com.company.ems.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void record(UUID accountId, String entityType, UUID entityId,
                        String fieldName, Object oldValue, Object newValue, String ipAddress) {
        AuditLog log = AuditLog.builder()
                .accountId(accountId)
                .entityType(entityType)
                .entityId(entityId)
                .fieldName(fieldName)
                .oldValue(oldValue != null ? oldValue.toString() : null)
                .newValue(newValue != null ? newValue.toString() : null)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(log);
    }
}
