package com.company.ems.repository;

import com.company.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByAccountId(UUID accountId);

    // Only active (non-terminated) employees are returned by default list views.
    @Query("""
           SELECT e FROM Employee e
           WHERE e.terminationDate IS NULL
             AND (:departmentId IS NULL OR e.department.id = :departmentId)
             AND (:search IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
           """)
    Page<Employee> search(@Param("departmentId") UUID departmentId,
                           @Param("search") String search,
                           Pageable pageable);

    // Scoped view for a MANAGER: only employees in their own department.
    @Query("""
           SELECT e FROM Employee e
           WHERE e.terminationDate IS NULL
             AND e.department.id = :departmentId
             AND (:search IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :search, '%')))
           """)
    Page<Employee> searchWithinDepartment(@Param("departmentId") UUID departmentId,
                                           @Param("search") String search,
                                           Pageable pageable);
}
