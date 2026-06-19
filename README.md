# Digital Repository Platform (DRP)

A web-based Digital Repository Platform (DRP) for storing, organizing, managing, and retrieving digital resources with JWT authentication and role-based access control.

---

## Tech Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* JWT Authentication
* MySQL 8
* Maven

### Frontend

* HTML5
* CSS3
* Vanilla JavaScript
* Live Server (VS Code)

---

## Features

### Authentication & Security

* JWT-based authentication
* Login functionality
* User registration
* Password encryption using BCrypt
* Role-based authorization
* Session persistence using localStorage

### User Features

* Browse resources
* Search resources
* Download resources
* Upload resources
* View profile information

### Admin Features

* User management
* Category management
* Activity logs
* Resource administration

---

## Project Structure

```text
Digital Repository Platform/

├── backend/
│
│   ├── src/main/java/com/drp/
│   │
│   ├── config/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisterUserRequest.java
│   │   │
│   │   └── response/
│   │       └── ApiResponse.java
│   │
│   ├── entity/
│   ├── repository/
│   ├── security/
│   ├── service/
│   └── exception/
│
│
├── frontend/
│
│   ├── login.html
│   ├── register.html
│   ├── browse.html
│   ├── upload.html
│   ├── resource.html
│   ├── profile.html
│   │
│   ├── register.js
│   │
│   └── admin/
│       ├── users.html
│       ├── categories.html
│       └── logs.html
│
└── pom.xml
```

---

## Authentication Flow

```text
Register
    ↓
POST /auth/register
    ↓
User created
    ↓
Default role assigned → USER
    ↓
Login
    ↓
POST /auth/login
    ↓
JWT generated
    ↓
Stored in localStorage
    ↓
Redirect to browse page
```

---

## Backend API Endpoints

### Authentication

| Method | Endpoint       | Access        |
| ------ | -------------- | ------------- |
| POST   | /auth/register | Public        |
| POST   | /auth/login    | Public        |
| GET    | /auth/me       | Authenticated |

### Users

| Method | Endpoint | Access |
| ------ | -------- | ------ |
| GET    | /users   | Admin  |
| POST   | /users   | Admin  |
| PUT    | /users   | Admin  |
| DELETE | /users   | Admin  |

### Categories

| Method | Endpoint    | Access |
| ------ | ----------- | ------ |
| GET    | /categories | Public |
| POST   | /categories | Admin  |
| PUT    | /categories | Admin  |
| DELETE | /categories | Admin  |

### Resources

| Method | Endpoint                 | Access        |
| ------ | ------------------------ | ------------- |
| GET    | /resources               | Public        |
| POST   | /resources               | Authenticated |
| PUT    | /resources               | Authenticated |
| DELETE | /resources               | Authenticated |
| GET    | /resources/{id}/download | Public        |

### Activity Logs

| Method | Endpoint       | Access |
| ------ | -------------- | ------ |
| GET    | /activity-logs | Admin  |

---

## Registration Request Example

```json
POST http://localhost:8080/api/v1/auth/register

{
   "username":"john123",
   "email":"john@example.com",
   "password":"password123"
}
```

---

## Login Request Example

```json
POST http://localhost:8080/api/v1/auth/login

{
   "username":"john123",
   "password":"password123"
}
```

---

## Running the Backend

Open terminal from backend root:

```bash
mvn spring-boot:run
```

Application starts at:

```text
http://localhost:8080
```

API Base URL:

```text
http://localhost:8080/api/v1
```

---

## Running the Frontend

1. Open frontend folder in VS Code

2. Install:

```text
Live Server
```

3. Right click:

```text
login.html
```

4. Select:

```text
Open with Live Server
```

Frontend URL:

```text
http://127.0.0.1:5500/login.html
```

---

## Default Admin Credentials

```text
Username : admin
Password : admin123
```

---

## Frontend UI Pages

### Public Pages

* Login page
* Registration page
* Browse resources page
* Resource details page

### Authenticated Pages

* Upload resource page
* Profile page

### Admin Pages

* Users management
* Categories management
* Activity logs

---

## Future Improvements

* File preview support
* Email verification
* Forgot password
* Resource thumbnails
* Dark/light theme switch
* Pagination enhancements
* Dashboard analytics
* User profile editing

---

## Status

| Module             | Status      |
| ------------------ | ----------- |
| Backend APIs       | Completed   |
| JWT Authentication | Completed   |
| Registration       | Completed   |
| Login              | Completed   |
| Frontend UI        | In Progress |
| Admin Pages        | In Progress |
| Testing            | Pending     |

```
```
