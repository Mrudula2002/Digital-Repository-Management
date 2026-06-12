# Digital Repository Platform (DRP)

A web-based system for storing, organizing, and retrieving digital resources with role-based access control.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring Security
- **Database:** MySQL 8
- **Frontend (planned):** HTML + CSS (REST API consumer)
- **Build:** Maven

## Project Structure

```
Project DRS/
├── docs/
│   └── BACKEND_ARCHITECTURE.md   ← Start here
├── src/main/java/com/drp/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── security/
│   └── service/
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## Development Phases

| Phase | Scope | Status |
|-------|-------|--------|
| 1 | Backend architecture | Done |
| 2 | Entities, repositories, services, REST APIs, JWT security | Done |
| 3 | HTML/CSS frontend | Next |
| 4 | Integration & testing | Later |

## Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8 running locally

## Quick Start

1. Start MySQL and update `src/main/resources/application.properties` if needed.
2. Run from IntelliJ (open `DrpApplication`) or via Maven:

```bash
mvn spring-boot:run
```

3. On first startup, a default admin is created:
   - Username: `admin`
   - Password: `admin123`

API base URL: `http://localhost:8080/api/v1`

## Phase 2 — REST API Summary

All responses use `ApiResponse<T>`: `{ "success", "message", "data" }`.

Authenticated requests need header: `Authorization: Bearer <token>`

| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/auth/login` | Public |
| GET | `/auth/me` | Authenticated |
| GET/POST/PUT/DELETE | `/users/**` | Admin |
| GET | `/categories` | Public |
| POST/PUT/DELETE | `/categories/**` | Admin |
| GET | `/resources` (search: `keyword`, `categoryId`, `fileType`, `page`, `size`) | Public |
| GET | `/resources/{id}/download` | Public |
| POST/PUT/DELETE | `/resources/**` | Authenticated |
| GET | `/activity-logs` | Admin |

### Postman example — login

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{ "username": "admin", "password": "admin123" }
```

### Postman example — upload resource

```http
POST http://localhost:8080/api/v1/resources
Authorization: Bearer <token>
Content-Type: multipart/form-data

title: Sample Report
description: Q1 summary
categoryId: 1
file: <select PDF file>
```

## Documentation

See [Backend Architecture](docs/BACKEND_ARCHITECTURE.md) for full design details.
