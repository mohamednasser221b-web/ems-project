package com.company.ems.controller;

import com.company.ems.dto.EmployeeAdminUpdateRequest;
import com.company.ems.dto.EmployeeResponse;
import com.company.ems.dto.EmployeeSelfUpdateRequest;
import com.company.ems.dto.PageResponse;
import com.company.ems.security.AccountPrincipal;
import com.company.ems.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public PageResponse<EmployeeResponse> search(@AuthenticationPrincipal AccountPrincipal principal,
                                                  @RequestParam(required = false) UUID department,
                                                  @RequestParam(required = false) String search,
                                                  Pageable pageable) {
        return employeeService.search(principal, department, search, pageable);
    }

    // /me MUST be declared before /{id} - otherwise Spring would try to parse
    // "me" as a UUID path variable and this route would never match.
    @GetMapping("/me")
    public EmployeeResponse getSelf(@AuthenticationPrincipal AccountPrincipal principal) {
        return employeeService.getSelf(principal);
    }

    @PatchMapping("/me")
    public EmployeeResponse updateSelf(@AuthenticationPrincipal AccountPrincipal principal,
                                       @Valid @RequestBody EmployeeSelfUpdateRequest request,
                                       HttpServletRequest httpRequest) {
        return employeeService.updateSelf(principal, request, httpRequest.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@AuthenticationPrincipal AccountPrincipal principal,
                                    @PathVariable UUID id) {
        return employeeService.getById(principal, id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public EmployeeResponse updateAsAdmin(@AuthenticationPrincipal AccountPrincipal principal,
                                          @PathVariable UUID id,
                                          @Valid @RequestBody EmployeeAdminUpdateRequest request,
                                          HttpServletRequest httpRequest) {
        return employeeService.updateAsAdmin(principal, id, request, httpRequest.getRemoteAddr());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR_ADMIN')")
    public void terminate(@AuthenticationPrincipal AccountPrincipal principal,
                          @PathVariable UUID id,
                          HttpServletRequest httpRequest) {
        employeeService.terminate(principal, id, httpRequest.getRemoteAddr());
    }
}
