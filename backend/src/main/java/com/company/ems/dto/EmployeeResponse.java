package com.company.ems.dto;

import com.company.ems.entity.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String fullName,
        String email,
        String departmentName,
        UUID managerId,
        String managerName,
        BigDecimal salary,
        LocalDate hireDate
) {
    public static EmployeeResponse from(Employee e, boolean includeSalary) {
        return new EmployeeResponse(
                e.getId(),
                e.getFullName(),
                e.getAccount().getEmail(),
                e.getDepartment().getName(),
                e.getManager() != null ? e.getManager().getId() : null,
                e.getManager() != null ? e.getManager().getFullName() : null,
                // Salary is stripped out entirely for roles that shouldn't see it -
                // this happens in the service layer before this DTO is built.
                includeSalary ? e.getSalary() : null,
                e.getHireDate()
        );
    }
}
