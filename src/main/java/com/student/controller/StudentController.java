package com.student.controller;

import com.student.model.Student;
import com.student.service.StudentService;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * REST CONTROLLER — The API Gateway (HTTP ↔ Java Bridge)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * @RestController = @Controller + @ResponseBody
 *
 * @Controller:
 *   Marks this class as a Spring MVC controller — it handles HTTP requests.
 *   Spring scans for it via component scanning and registers request mappings.
 *
 * @ResponseBody:
 *   Tells Spring to AUTOMATICALLY serialize return values to JSON (using Jackson)
 *   and write them directly to the HTTP response body. Without this, Spring
 *   would try to resolve return values as view names (like JSP/Thymeleaf templates).
 *
 * @RequestMapping("/api/students"):
 *   Sets a BASE PATH for all endpoints in this controller.
 *   All methods below inherit this prefix:
 *     POST   /api/students       → createStudent()
 *     GET    /api/students       → getAllStudents()
 *     GET    /api/students/{id}  → getStudentById()
 *     PUT    /api/students/{id}  → updateStudent()
 *     DELETE /api/students/{id}  → deleteStudent()
 *
 *   WHY "/api/" PREFIX?
 *   Separates API routes from static content routes. In a full app, you might
 *   serve web pages at "/" and API endpoints at "/api/". This is a common
 *   convention that makes reverse proxy configuration easier (e.g., Nginx
 *   routes /api/* to the backend, /* to the frontend).
 *
 * REQUEST FLOW:
 *   1. Client sends HTTP request (e.g., POST /api/students with JSON body)
 *   2. Embedded Tomcat receives the request
 *   3. Spring's DispatcherServlet finds the matching controller method
 *   4. Jackson deserializes the JSON body into a Student object
 *   5. @Valid triggers bean validation (checks @NotBlank, @Email, etc.)
 *   6. If validation fails → MethodArgumentNotValidException → GlobalExceptionHandler
 *   7. If validation passes → Controller method executes → calls Service
 *   8. Service calls Repository → Repository talks to Database
 *   9. Result flows back: Database → Repository → Service → Controller
 *  10. Jackson serializes the return value to JSON
 *  11. ResponseEntity wraps the JSON with the appropriate HTTP status code
 *  12. Response sent back to the client
 */
@CrossOrigin(origins = "*") // Allow requests from React frontend
@RestController
@RequestMapping("/students")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    private final StudentService studentService;

    /**
     * Constructor injection — Spring injects the StudentService bean.
     */
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // POST /api/students — Create a new student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * @PostMapping → Maps HTTP POST requests to this method.
     *   POST is the HTTP verb for CREATING new resources.
     *   POST is NOT idempotent — calling it twice creates TWO students.
     *
     * @Valid → Triggers bean validation on the Student object.
     *   If any @NotBlank, @Email, @Size constraint fails, Spring throws
     *   MethodArgumentNotValidException BEFORE this method body executes.
     *   Our GlobalExceptionHandler catches it and returns 400 Bad Request.
     *
     * @RequestBody → Tells Spring to deserialize the HTTP request body
     *   (JSON) into a Student Java object using Jackson.
     *   Example JSON body: {"name": "John", "email": "john@x.com", "course": "CS"}
     *   Jackson calls: new Student() → setName("John") → setEmail(...) → ...
     *
     * ResponseEntity<Student> → Wraps the response with:
     *   • HTTP status code (201 CREATED — the standard for successful resource creation)
     *   • Response body (the created Student as JSON, including the generated id)
     *   • Optional headers (e.g., Location header with the URL of the new resource)
     *
     * WHY ResponseEntity INSTEAD OF JUST RETURNING Student?
     *   Returning Student directly always returns HTTP 200 OK. But for a
     *   CREATE operation, the correct status is 201 CREATED. ResponseEntity
     *   gives us full control over the HTTP response (status, headers, body).
     */
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        log.info("POST /api/students — Creating student: {}", student.getEmail());
        Student created = studentService.createStudent(student);
        return new ResponseEntity<>(created, HttpStatus.CREATED);  // 201
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/students — Retrieve all students
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * @GetMapping → Maps HTTP GET requests to this method.
     *   GET is for READING resources. It is SAFE (no side effects) and
     *   IDEMPOTENT (calling it 100 times returns the same result).
     *
     * Returns HTTP 200 OK with a JSON array of all students.
     * If no students exist, returns an empty array [] (NOT null or 404).
     * An empty collection is a valid response — it means "zero results."
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        log.info("GET /api/students — Fetching all students");
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);  // 200
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GET /api/students/{id} — Retrieve a single student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * @PathVariable → Extracts the {id} from the URL path.
     *   For GET /api/students/42, id = 42.
     *
     * The method name in the URL template ({id}) must match the parameter name.
     * Spring automatically converts the string "42" to int 42.
     *
     * If the student doesn't exist, the service throws StudentNotFoundException,
     * which GlobalExceptionHandler catches and returns HTTP 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        log.info("GET /api/students/{} — Fetching student", id);
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);  // 200
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PUT /api/students/{id} — Update an existing student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * @PutMapping → Maps HTTP PUT requests.
     *   PUT replaces the ENTIRE resource with the new data.
     *   PUT is IDEMPOTENT — calling it multiple times with the same data
     *   produces the same result (unlike POST which creates duplicates).
     *
     * Both @PathVariable (for id) and @RequestBody (for student data) are used.
     * @Valid ensures the new data is validated before the update proceeds.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable int id,
            @Valid @RequestBody Student student) {
        log.info("PUT /api/students/{} — Updating student", id);
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);  // 200
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DELETE /api/students/{id} — Delete a student
    // ═══════════════════════════════════════════════════════════════════════════
    /**
     * @DeleteMapping → Maps HTTP DELETE requests.
     *   DELETE is IDEMPOTENT — deleting a resource that's already deleted
     *   is a no-op (our implementation throws 404, which is also acceptable).
     *
     * Returns HTTP 204 NO CONTENT — the standard response for successful
     * deletions. 204 means "success, but there's no body to return."
     * The resource is gone — there's nothing to send back.
     *
     * ResponseEntity.noContent().build() creates a 204 response with no body.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {
        log.info("DELETE /api/students/{} — Deleting student", id);
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();  // 204
    }
}
