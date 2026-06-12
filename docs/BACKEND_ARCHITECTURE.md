# Digital Repository Platform — Backend Architecture

## 1. Overview

The Digital Repository Platform (DRP) backend is a **Spring Boot 3** REST API that manages digital assets (documents, reports, research papers, etc.) with role-based access, search, and activity auditing.

| Layer | Technology |
|-------|------------|
| Runtime | Java 17+ |
| Framework | Spring Boot 3.x |
| Persistence | Spring Data JPA |
| Database | MySQL 8 |
| Security | Spring Security + BCrypt |
| Build | Maven |
| Frontend (later) | HTML + CSS (consumes REST API) |

---

## 2. Architectural Style

**Layered (N-tier) architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│  Presentation Layer   │  REST Controllers (@RestController) │
├─────────────────────────────────────────────────────────────┤
│  Security Layer       │  JWT/Session filters, RBAC          │
├─────────────────────────────────────────────────────────────┤
│  Service Layer        │  Business logic, validation, DTOs │
├─────────────────────────────────────────────────────────────┤
│  Repository Layer     │  Spring Data JPA interfaces         │
├─────────────────────────────────────────────────────────────┤
│  Domain Layer         │  JPA Entities                       │
├─────────────────────────────────────────────────────────────┤
│  Infrastructure       │  File storage, MySQL, config        │
└─────────────────────────────────────────────────────────────┘
```

**Request flow:**

```
Client → Controller → Service → Repository → Database
                    ↓
              FileStorageService (uploads)
                    ↓
              ActivityLogService (audit)
```

---

## 3. Package Structure

```
com.drp
├── DrpApplication.java
├── config
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── FileStorageConfig.java
├── controller
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ResourceController.java
│   ├── CategoryController.java
│   └── ActivityLogController.java
├── dto
│   ├── request
│   │   ├── LoginRequest.java
│   │   ├── RegisterUserRequest.java
│   │   ├── ResourceUploadRequest.java
│   │   └── ResourceSearchRequest.java
│   └── response
│       ├── ApiResponse.java
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── ResourceResponse.java
│       └── ActivityLogResponse.java
├── entity
│   ├── User.java
│   ├── Role.java (enum)
│   ├── Resource.java
│   ├── Category.java
│   └── ActivityLog.java
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── repository
│   ├── UserRepository.java
│   ├── ResourceRepository.java
│   ├── CategoryRepository.java
│   └── ActivityLogRepository.java
├── security
│   ├── JwtTokenProvider.java (optional — Phase 2)
│   ├── CustomUserDetailsService.java
│   └── SecurityUtils.java
└── service
    ├── AuthService.java
    ├── UserService.java
    ├── ResourceService.java
    ├── CategoryService.java
    ├── ActivityLogService.java
    └── FileStorageService.java
```

---

## 4. Domain Model (ER Diagram)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │  resources   │       │  categories  │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │──┐    │ id (PK)      │    ┌──│ id (PK)      │
│ username     │  │    │ title        │    │  │ name         │
│ email        │  └───▶│ uploaded_by  │    │  │ description  │
│ password     │       │ category_id  │◀───┘  │ created_at   │
│ role         │       │ file_name    │       └──────────────┘
│ active       │       │ file_path    │
│ created_at   │       │ file_type    │
└──────────────┘       │ file_size    │
       │               │ description  │
       │               │ tags         │
       │               │ created_at   │
       │               │ updated_at   │
       │               └──────────────┘
       │
       ▼
┌──────────────┐
│ activity_logs│
├──────────────┤
│ id (PK)      │
│ user_id (FK) │
│ action       │
│ entity_type  │
│ entity_id    │
│ details      │
│ timestamp    │
└──────────────┘
```

### Entity Relationships

| Relationship | Type | Description |
|-------------|------|-------------|
| User → Resource | One-to-Many | User uploads many resources |
| Category → Resource | One-to-Many | Category groups resources |
| User → ActivityLog | One-to-Many | User actions are logged |

---

## 5. Roles & Permissions

| Action | ADMIN | USER |
|--------|-------|------|
| Login / view own profile | ✓ | ✓ |
| Search & download resources | ✓ | ✓ |
| Upload resource | ✓ | ✓ |
| Update/delete own resource | ✓ | ✓ |
| Manage all resources | ✓ | ✗ |
| Create/update/delete users | ✓ | ✗ |
| Manage categories | ✓ | ✗ |
| View activity logs | ✓ | ✗ |

---

## 6. REST API Design

Base path: `/api/v1`

### Auth
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/auth/login` | Authenticate user | Public |
| POST | `/auth/register` | Register (admin-only in prod) | Admin |
| GET | `/auth/me` | Current user profile | Authenticated |

### Users (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get user by ID |
| POST | `/users` | Create user |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Deactivate user |

### Categories
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/categories` | List categories | All |
| POST | `/categories` | Create category | Admin |
| PUT | `/categories/{id}` | Update category | Admin |
| DELETE | `/categories/{id}` | Delete category | Admin |

### Resources
| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/resources` | List/search resources | All |
| GET | `/resources/{id}` | Get resource metadata | All |
| GET | `/resources/{id}/download` | Download file | All |
| POST | `/resources` | Upload resource (multipart) | Authenticated |
| PUT | `/resources/{id}` | Update metadata | Owner/Admin |
| DELETE | `/resources/{id}` | Delete resource | Owner/Admin |

### Activity Logs (Admin)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/activity-logs` | List logs (paginated) |

### Search Query Parameters (GET `/resources`)
- `keyword` — search title, description, tags
- `categoryId` — filter by category
- `fileType` — filter by MIME/extension
- `page`, `size` — pagination
- `sortBy`, `sortDir` — sorting

---

## 7. Security Design

1. **Authentication**: HTTP Basic or JWT (recommended for HTML frontend)
2. **Password storage**: BCrypt via `PasswordEncoder`
3. **Authorization**: `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints
4. **CORS**: Configured for frontend origin during integration phase
5. **File upload limits**: Max size configured in `application.properties`
6. **Allowed file types**: PDF, DOC/DOCX, TXT, images (configurable whitelist)

---

## 8. File Storage Strategy

- Files stored on local filesystem under `uploads/` (configurable path)
- Database stores **metadata only** (path, name, size, type)
- File naming: `{uuid}_{originalFilename}` to avoid collisions
- On delete: remove DB record + physical file

Future extension: AWS S3 / Azure Blob via `FileStorageService` interface.

---

## 9. Activity Logging

Every significant action writes to `activity_logs`:

| Action | Entity | Example details |
|--------|--------|-----------------|
| LOGIN | User | username |
| UPLOAD | Resource | title, file name |
| UPDATE | Resource | changed fields |
| DELETE | Resource | title |
| CREATE_USER | User | username |
| CREATE_CATEGORY | Category | name |

Implemented via `ActivityLogService.log(user, action, entityType, entityId, details)`.

---

## 10. Configuration (`application.properties`)

Key settings:
- `spring.datasource.*` — MySQL connection
- `spring.jpa.hibernate.ddl-auto=update` (dev) / `validate` (prod)
- `drp.file.upload-dir=./uploads`
- `drp.file.max-size=10MB`
- `spring.servlet.multipart.max-file-size=10MB`

---

## 11. Development Phases

| Phase | Scope | Status |
|-------|-------|--------|
| **1** | Backend architecture (this document) | ✓ Done |
| **2** | Entities, repositories, services, controllers, JWT security | ✓ Done |
| **3** | HTML/CSS frontend pages | Next |
| **4** | Integration & manual testing | Later |

---

## 12. Library Management System — Fit for This Use Case?

**Short answer: Partial overlap, but a dedicated DRP is the better fit.**

| Aspect | Library Management System | Digital Repository Platform |
|--------|---------------------------|----------------------------|
| Primary asset | Books (physical/digital copies) | Any digital file (docs, reports, papers) |
| Core workflow | Issue, return, due dates, fines | Upload, categorize, search, download |
| Metadata | ISBN, author, edition, shelf | Title, tags, category, file type |
| Access model | Borrowing (temporary) | Persistent access (permission-based) |
| Unique features | Circulation, reservations | Versioning, bulk upload, activity audit |

**What you can reuse from LMS concepts:**
- Catalog/search patterns
- User roles (librarian ≈ admin, member ≈ user)
- Category/genre taxonomy

**What does not map well:**
- Book copies, circulation, overdue logic
- ISBN-centric data model

**Recommendation:** Build DRP as designed above. Do not fork a full LMS — you would remove more than you keep. Borrow **design patterns**, not the LMS codebase.
