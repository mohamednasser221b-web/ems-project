CREATE EXTENSION IF NOT EXISTS pgcrypto; -- gen_random_uuid()

CREATE TYPE user_role AS ENUM ('HR_ADMIN', 'MANAGER', 'EMPLOYEE');

CREATE TABLE department (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE account (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email          VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           user_role NOT NULL DEFAULT 'EMPLOYEE',
    is_active      BOOLEAN NOT NULL DEFAULT true,
    last_login_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE employee (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id        UUID NOT NULL UNIQUE REFERENCES account(id),
    department_id     UUID NOT NULL REFERENCES department(id),
    manager_id        UUID REFERENCES employee(id),
    full_name         VARCHAR(150) NOT NULL,
    salary            NUMERIC(12,2),
    hire_date         DATE NOT NULL,
    termination_date  DATE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE document (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id   UUID NOT NULL REFERENCES employee(id),
    s3_key        VARCHAR(512) NOT NULL,
    doc_type      VARCHAR(50) NOT NULL,
    uploaded_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audit_log (
    id           BIGSERIAL PRIMARY KEY,
    account_id   UUID REFERENCES account(id),
    entity_type  VARCHAR(50) NOT NULL,
    entity_id    UUID NOT NULL,
    field_name   VARCHAR(100),
    old_value    TEXT,
    new_value    TEXT,
    ip_address   INET,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_employee_department ON employee(department_id);
CREATE INDEX idx_employee_manager ON employee(manager_id);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_account ON audit_log(account_id);
