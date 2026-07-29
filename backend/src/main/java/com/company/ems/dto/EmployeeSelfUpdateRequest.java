package com.company.ems.dto;

import jakarta.validation.constraints.Size;

/**
 * Update DTO used by /employees/me. Only fields an employee is allowed to
 * change about themselves. No salary, department, manager, or role field
 * exists on this type - there is structurally no way to smuggle those values
 * through this endpoint, regardless of what the client sends.
 */
public record EmployeeSelfUpdateRequest(
        @Size(min = 1, max = 150) String fullName
) {
}
