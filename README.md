# Employee Management System

Backend: Spring Boot 3 / Java 21, PostgreSQL, Redis, JWT auth, S3 for files.
Frontend: React + TypeScript + MUI.

## Roles
- `HR_ADMIN` — full CRUD, sees salary, manages departments, reads audit logs
- `MANAGER` — read/limited-update within own department only, no salary edits
- `EMPLOYEE` — read/update own profile only via `/employees/me`

## Local run
```
docker compose up
```
(compose file added in the containerization phase)

## Known follow-ups (intentionally left as-is for this stage)
- Password reset endpoint is a stub — needs the mail service wired up.
- Refresh token rotation / server-side invalidation not implemented — a
  stolen refresh token is valid until it expires. Needs a token store
  (Redis) to support revocation.
- Department creation endpoint accepts the raw `Department` entity — should
  be replaced with a dedicated request DTO before this is called "reviewed."
- Profile picture upload endpoint (S3 presigned URL flow) not yet built.
- No rate limiting on `/auth/login` yet — needed before this is internet-facing.

These are deliberate gaps, not accidents — they're the first items on the
security/code review backlog for the DevSecOps phase.
# ems-project
