-- DEV/DEMO SEED DATA ONLY.
-- This migration must NOT run against the production database. In a real
-- pipeline this would live in a separate Flyway "location" only pointed at
-- by the dev/staging profile - flagged here as a task for the CI/CD phase
-- (environment-specific migration paths).

INSERT INTO department (id, name) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Engineering'),
    ('22222222-2222-2222-2222-222222222222', 'Sales'),
    ('33333333-3333-3333-3333-333333333333', 'Human Resources');

-- Password for all seed accounts is "ChangeMe123!" - bcrypt hash below.
-- This is a placeholder credential for local dev only and must never be
-- reused anywhere near a real environment.
INSERT INTO account (id, email, password_hash, role) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'hr.admin@example.com',
     '$2a$12$5cN8qk3f1oQeYV0nJmA9UOgq0f0oYQqFQe6b3v0e0e1p1t8x2k7bO', 'HR_ADMIN'),
    ('aaaaaaaa-0000-0000-0000-000000000002', 'manager@example.com',
     '$2a$12$5cN8qk3f1oQeYV0nJmA9UOgq0f0oYQqFQe6b3v0e0e1p1t8x2k7bO', 'MANAGER'),
    ('aaaaaaaa-0000-0000-0000-000000000003', 'employee@example.com',
     '$2a$12$5cN8qk3f1oQeYV0nJmA9UOgq0f0oYQqFQe6b3v0e0e1p1t8x2k7bO', 'EMPLOYEE');

INSERT INTO employee (id, account_id, department_id, manager_id, full_name, salary, hire_date) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001',
     '33333333-3333-3333-3333-333333333333', NULL, 'Hana Ahmed', 90000.00, '2022-01-10'),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000002',
     '11111111-1111-1111-1111-111111111111', NULL, 'Omar Khaled', 75000.00, '2022-03-15'),
    ('bbbbbbbb-0000-0000-0000-000000000003', 'aaaaaaaa-0000-0000-0000-000000000003',
     '11111111-1111-1111-1111-111111111111', 'bbbbbbbb-0000-0000-0000-000000000002',
     'Mona Farouk', 55000.00, '2023-06-01');
