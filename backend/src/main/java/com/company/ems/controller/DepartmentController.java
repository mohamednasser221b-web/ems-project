package com.company.ems.controller;

import com.company.ems.entity.Department;
import com.company.ems.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public List<Department> list() {
        return departmentRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('HR_ADMIN')")
    public Department create(@RequestBody Department department) {
        // Note: accepting the raw entity here is a shortcut for this stub;
        // a hardened version uses a dedicated CreateDepartmentRequest DTO so
        // clients can never set id/createdAt directly. Flagged for the code
        // review pass.
        return departmentRepository.save(Department.builder().name(department.getName()).build());
    }

    @GetMapping("/{id}")
    public Department get(@PathVariable UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new com.company.ems.exception.ApiExceptions.NotFoundException("Department not found"));
    }
}
