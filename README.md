# 🎓 Student CRUD — Spring Boot JDBC Application

A **production-grade** Spring Boot REST API for managing students, built entirely with **JDBC (JdbcTemplate)** — no Hibernate/JPA.

## Architecture

```
Client (Postman/curl) → Controller → Service → Repository → PostgreSQL
                        (REST API)   (Rules)   (JDBC/SQL)   (Database)
```

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Spring Boot 2.7.18 | Application framework |
| Spring JDBC (JdbcTemplate) | Database operations (manual SQL, no ORM) |
| PostgreSQL | Relational database |
| HikariCP | Connection pooling |
| Bean Validation | Input validation (`@NotBlank`, `@Email`, `@Size`) |
| SLF4J + Logback | Logging |
| Maven (with wrapper) | Build tool |

---

## 🚀 Quick Start

### Prerequisites
- **JDK 8+** — download from [Adoptium](https://adoptium.net/) (free)
- **PostgreSQL** — download from [postgresql.org](https://www.postgresql.org/download/)
- **Node.js** (v18+) — for the frontend

### 1. Database Setup
Open **psql** or **pgAdmin** and run:
```sql
CREATE DATABASE student_db;
```
> The `students` table is created **automatically** by `schema.sql` on app startup.

Edit `src/main/resources/application.properties` with your PostgreSQL password:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/student_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
```

### 2. Run the Backend (Spring Boot)
```bash
# Open a terminal in the root directory
# Set JAVA_HOME if not already set (e.g., set JAVA_HOME=C:\path\to\your\jdk)

# Run using Maven wrapper (no Maven install required)
.\mvnw.cmd spring-boot:run
```
The API starts on **http://localhost:8080**.

### 3. Run the Frontend (React + Vite)
```bash
# Open a new terminal
cd frontend

# Install dependencies (only needed once)
npm install

# Start the dev server
npm run dev
```
The premium UI starts on **http://localhost:5173**.

---

## 📡 How to Perform CRUD Operations

> All examples use `curl`. You can also use **Postman** — just set the URL, method, headers, and body as shown below.

### ✅ CREATE a Student — `POST /students`

```bash
curl -X POST http://localhost:8080/students ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Anirudh Chauhan\", \"email\": \"anirudh@example.com\", \"course\": \"Computer Science\"}"
```

**Response (201 Created):**
```json
{
    "id": 1,
    "name": "Anirudh Chauhan",
    "email": "anirudh@example.com",
    "course": "Computer Science"
}
```

---

### 📖 READ All Students — `GET /students`

```bash
curl http://localhost:8080/students
```

**Response (200 OK):**
```json
[
    {
        "id": 1,
        "name": "Anirudh Chauhan",
        "email": "anirudh@example.com",
        "course": "Computer Science"
    },
    {
        "id": 2,
        "name": "Priya Sharma",
        "email": "priya@example.com",
        "course": "Data Science"
    }
]
```

---

### 🔍 READ a Student by ID — `GET /students/{id}`

```bash
curl http://localhost:8080/students/1
```

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "Anirudh Chauhan",
    "email": "anirudh@example.com",
    "course": "Computer Science"
}
```

**If not found (404):**
```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Student not found with id: 99",
    "timestamp": "2026-04-29T14:30:00"
}
```

---

### ✏️ UPDATE a Student — `PUT /students/{id}`

```bash
curl -X PUT http://localhost:8080/students/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"Anirudh C.\", \"email\": \"anirudh.updated@example.com\", \"course\": \"Data Science\"}"
```

**Response (200 OK):**
```json
{
    "id": 1,
    "name": "Anirudh C.",
    "email": "anirudh.updated@example.com",
    "course": "Data Science"
}
```

---

### 🗑️ DELETE a Student — `DELETE /students/{id}`

```bash
curl -X DELETE http://localhost:8080/students/1
```

**Response:** `204 No Content` (empty body — the student has been deleted)

---

### ❌ Error Responses

**Validation error (missing/invalid fields):**
```bash
curl -X POST http://localhost:8080/students ^
  -H "Content-Type: application/json" ^
  -d "{\"name\": \"\", \"email\": \"invalid\", \"course\": \"\"}"
```
```json
{
    "status": 400,
    "error": "Validation Failed",
    "message": "name: Name is required and cannot be blank; email: Email must be a valid email address; course: Course is required and cannot be blank",
    "timestamp": "2026-04-29T14:36:00"
}
```

**Duplicate email:**
```json
{
    "status": 409,
    "error": "Conflict",
    "message": "A student with email 'anirudh@example.com' already exists",
    "timestamp": "2026-04-29T14:37:00"
}
```

---

## 📂 Project Structure

```
src/main/java/com/student/
├── StudentCrudJdbcApplication.java     # Entry point (@SpringBootApplication)
├── controller/
│   └── StudentController.java          # REST endpoints (POST, GET, PUT, DELETE)
├── service/
│   ├── StudentService.java             # Business logic interface
│   └── StudentServiceImpl.java         # Business logic implementation
├── repository/
│   ├── StudentRepository.java          # Data access interface
│   └── StudentJdbcRepository.java      # JDBC implementation (JdbcTemplate)
├── model/
│   └── Student.java                    # POJO entity (id, name, email, course)
└── exception/
    ├── StudentNotFoundException.java   # 404 exception
    ├── DuplicateEmailException.java    # 409 exception
    ├── GlobalExceptionHandler.java     # @ControllerAdvice (centralized error handling)
    └── ErrorResponse.java              # Structured error JSON
```

## API Reference

| Method | Endpoint | Description | Success | Failure |
|--------|----------|-------------|---------|---------|
| `POST` | `/students` | Create a student | `201 Created` | `400` / `409` |
| `GET` | `/students` | Retrieve all students | `200 OK` | — |
| `GET` | `/students/{id}` | Retrieve student by ID | `200 OK` | `404` |
| `PUT` | `/students/{id}` | Update a student | `200 OK` | `400` / `404` / `409` |
| `DELETE` | `/students/{id}` | Delete a student | `204 No Content` | `404` |

## Key Design Decisions

- **JDBC (not Hibernate)** — Full SQL control, manual query writing, `JdbcTemplate` for boilerplate reduction
- **Layered architecture** — Controller → Service → Repository (separation of concerns)
- **Interface-based design** — Repository and Service use interfaces for loose coupling
- **Connection pooling** — HikariCP (configured in `application.properties`)
- **Input validation** — `@NotBlank`, `@Email`, `@Size` on the entity + `@Valid` in controller
- **Global error handling** — `@ControllerAdvice` for consistent error responses

## Author
Anirudh Chauhan — Mid-term Project, 6th Semester