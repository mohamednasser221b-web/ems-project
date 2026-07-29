package com.company.ems.service;

import com.company.ems.dto.EmployeeAdminUpdateRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.dto.EmployeeSelfUpdateRequest;
import com.company.ems.dto.PageResponse;
import com.company.ems.entity.Department;
import com.company.ems.entity.Employee;
import com.company.ems.entity.UserRole;
import com.company.ems.exception.ApiExceptions.ForbiddenException;
import com.company.ems.exception.ApiExceptions.NotFoundException;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.security.AccountPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;

    public PageResponse<EmployeeResponse> search(AccountPrincipal principal, UUID departmentId,
                                                  String search, Pageable pageable) {
        UserRole role = principal.getAccount().getRole();
        boolean canSeeSalary = role == UserRole.HR_ADMIN;

        Page<Employee> page;
        if (role == UserRole.MANAGER) {
            // Structural scoping: a manager's query is ALWAYS constrained to
            // their own department, regardless of what departmentId they pass in.
            Employee self = employeeRepository.findByAccountId(principal.getAccountId())
                    .orElseThrow(() -> new NotFoundException("Employee profile not found"));
            page = employeeRepository.searchWithinDepartment(self.getDepartment().getId(), search, pageable);
        } else {
            page = employeeRepository.search(departmentId, search, pageable);
        }

        Page<EmployeeResponse> mapped = page.map(e -> EmployeeResponse.from(e, canSeeSalary));
        return PageResponse.of(mapped);
    }

    public EmployeeResponse getById(AccountPrincipal principal, UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        assertCanView(principal, employee);
        boolean canSeeSalary = principal.getAccount().getRole() == UserRole.HR_ADMIN;
        return EmployeeResponse.from(employee, canSeeSalary);
    }

    public EmployeeResponse getSelf(AccountPrincipal principal) {
        Employee employee = employeeRepository.findByAccountId(principal.getAccountId())
                .orElseThrow(() -> new NotFoundException("Employee profile not found"));
        return EmployeeResponse.from(employee, principal.getAccount().getRole() == UserRole.HR_ADMIN);
    }

    @Transactional
    public EmployeeResponse updateSelf(AccountPrincipal principal, EmployeeSelfUpdateRequest request, String ip) {
        Employee employee = employeeRepository.findByAccountId(principal.getAccountId())
                .orElseThrow(() -> new NotFoundException("Employee profile not found"));

        if (request.fullName() != null && !request.fullName().equals(employee.getFullName())) {
            auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                    "fullName", employee.getFullName(), request.fullName(), ip);
            employee.setFullName(request.fullName());
        }

        employeeRepository.save(employee);
        return EmployeeResponse.from(employee, false);
    }

    @Transactional
    public EmployeeResponse updateAsAdmin(AccountPrincipal principal, UUID id,
                                          EmployeeAdminUpdateRequest request, String ip) {
        // Defense-in-depth: even though this method is only reachable via an
        // endpoint gated with @PreAuthorize("hasRole('HR_ADMIN')"), assert it
        // again here so this service method is never accidentally safe to call
        // from a future, less-careful controller path.
        if (principal.getAccount().getRole() != UserRole.HR_ADMIN) {
            throw new ForbiddenException("Only HR_ADMIN may perform this update");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        if (request.fullName() != null && !request.fullName().equals(employee.getFullName())) {
            auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                    "fullName", employee.getFullName(), request.fullName(), ip);
            employee.setFullName(request.fullName());
        }

        if (request.departmentId() != null) {
            Department dept = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new NotFoundException("Department not found"));
            auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                    "departmentId", employee.getDepartment().getId(), dept.getId(), ip);
            employee.setDepartment(dept);
        }

        if (request.managerId() != null) {
            Employee manager = employeeRepository.findById(request.managerId())
                    .orElseThrow(() -> new NotFoundException("Manager not found"));
            auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                    "managerId", employee.getManager() != null ? employee.getManager().getId() : null,
                    manager.getId(), ip);
            employee.setManager(manager);
        }

        if (request.salary() != null) {
            auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                    "salary", employee.getSalary(), request.salary(), ip);
            employee.setSalary(request.salary());
        }

        employeeRepository.save(employee);
        return EmployeeResponse.from(employee, true);
    }

    @Transactional
    public void terminate(AccountPrincipal principal, UUID id, String ip) {
        if (principal.getAccount().getRole() != UserRole.HR_ADMIN) {
            throw new ForbiddenException("Only HR_ADMIN may terminate an employee");
        }

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        // Soft delete only - the row and its full history remain queryable
        // for audit/compliance. We never issue a hard DELETE on this table.
        employee.setTerminationDate(LocalDate.now());
        employeeRepository.save(employee);

        auditService.record(principal.getAccountId(), "Employee", employee.getId(),
                "terminationDate", null, employee.getTerminationDate(), ip);
    }

    private void assertCanView(AccountPrincipal principal, Employee target) {
        UserRole role = principal.getAccount().getRole();
        if (role == UserRole.HR_ADMIN) return;

        if (role == UserRole.MANAGER) {
            Employee self = employeeRepository.findByAccountId(principal.getAccountId())
                    .orElseThrow(() -> new NotFoundException("Employee profile not found"));
            if (!self.getDepartment().getId().equals(target.getDepartment().getId())) {
                throw new ForbiddenException("Cannot view employees outside your department");
            }
            return;
        }

        // EMPLOYEE role: only their own record, via this path.
        if (!target.getAccount().getId().equals(principal.getAccountId())) {
            throw new ForbiddenException("Cannot view another employee's record");
        }
    }
}
