package com.company.ems.controller;

import com.company.ems.dto.PageResponse;
import com.company.ems.entity.AuditLog;
import com.company.ems.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR_ADMIN')")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public PageResponse<AuditLog> search(@RequestParam(required = false) String entityType,
                                          @RequestParam(required = false) UUID entityId,
                                          @RequestParam(required = false) UUID accountId,
                                          Pageable pageable) {
        if (entityType != null && entityId != null) {
            return PageResponse.of(auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable));
        }
        if (accountId != null) {
            return PageResponse.of(auditLogRepository.findByAccountId(accountId, pageable));
        }
        return PageResponse.of(auditLogRepository.findAll(pageable));
    }
}
