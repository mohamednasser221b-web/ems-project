package com.company.ems.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Update DTO used ONLY by HR_ADMIN-authorized endpoints.
 * Deliberately the only DTO in the codebase that exposes salary/department/manager
 * as writable fields — this is a structural boundary, not just a runtime check.
 * A field that doesn't exist here cannot be set by a non-admin, no matter what
 * the client sends in the request body.
 */
public record EmployeeAdminUpdateRequest(
        String fullName,
        UUID departmentId,
        UUID managerId,
        BigDecimal salary
) {
}
