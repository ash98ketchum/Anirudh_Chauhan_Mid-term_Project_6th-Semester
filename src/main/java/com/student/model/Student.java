package com.student.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * STUDENT ENTITY — The Data Model (POJO)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * This is a Plain Old Java Object (POJO). It represents a single row in the
 * 'students' database table. Each field maps to a column.
 *
 * ┌──────────────────────────────────────────────────────────────────────────┐
 * │  WHY IS THIS NOT AN @Entity?                                            │
 * ├──────────────────────────────────────────────────────────────────────────┤
 * │  @Entity is a JPA/Hibernate annotation. It tells the ORM framework to   │
 * │  automatically map this class to a database table and manage its        │
 * │  lifecycle (persist, merge, remove, etc.).                              │
 * │                                                                         │
 * │  Since we are using JDBC directly (via JdbcTemplate), we DON'T need     │
 * │  JPA annotations. We write SQL ourselves. This class is just a simple   │
 * │  "data container" — a bucket that holds field values.                   │
 * │                                                                         │
 * │  In JDBC world, we manually:                                            │
 * │    1. Write SQL: "INSERT INTO students (name, email, course) VALUES..." │
 * │    2. Set parameters: statement.setString(1, student.getName())         │
 * │    3. Map results: student.setName(resultSet.getString("name"))         │
 * │                                                                         │
 * │  In JPA world, you'd just call: entityManager.persist(student)          │
 * │  and the framework generates the SQL for you.                           │
 * │                                                                         │
 * │  TRADE-OFF:                                                             │
 * │    • JDBC = More control, more code, more performance tuning options    │
 * │    • JPA  = Less code, magic behind the scenes, less control            │
 * └──────────────────────────────────────────────────────────────────────────┘
 *
 * VALIDATION ANNOTATIONS (from Jakarta Bean Validation):
 * These annotations define RULES that are checked BEFORE the data reaches
 * the service/repository layer. If validation fails, Spring automatically
 * returns a 400 Bad Request response with details about what went wrong.
 *
 * Analogy: Think of validation like airport security. Before passengers
 * (data) board the plane (enter your database), they must pass through
 * security checks (validation annotations). Invalid passengers are
 * rejected at the gate, not after they're on the plane.
 */
public class Student {

    /**
     * id → Primary key from the database.
     * Not validated because:
     *   - For CREATE: The database generates it (SERIAL auto-increment).
     *   - For UPDATE: It comes from the URL path, not the request body.
     */
    private int id;

    /**
     * @NotBlank → The field must not be null, not empty (""), and not just whitespace ("   ").
     *   Difference from @NotNull: @NotNull allows empty strings; @NotBlank does not.
     *   Difference from @NotEmpty: @NotEmpty allows whitespace-only strings; @NotBlank does not.
     *
     * @Size(min=2, max=100) → The string must be between 2 and 100 characters.
     *   Why min=2? Prevents single-character names (likely typos).
     *   Why max=100? Matches the VARCHAR(100) in the database schema.
     *   If someone submits a 200-character name, the validation rejects it BEFORE
     *   it even hits the database (fail fast).
     */
    @NotBlank(message = "Name is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * @Email → Validates that the string is a well-formed email address.
     *   Uses a regex pattern internally to check format (user@domain.tld).
     *   Note: It does NOT verify the email actually exists — only format.
     *
     * @NotBlank → Email is required. Combined with @Email, this ensures:
     *   - The field is present (not null/empty/whitespace)
     *   - The value is a valid email format
     */
    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email must be a valid email address (e.g., john@example.com)")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    /**
     * @NotBlank → Course is required.
     * @Size → Between 2 and 100 characters to match the database column.
     */
    @NotBlank(message = "Course is required and cannot be blank")
    @Size(min = 2, max = 100, message = "Course must be between 2 and 100 characters")
    private String course;

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSTRUCTORS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * NO-ARG CONSTRUCTOR
     * Required by frameworks (Jackson, Spring) to create instances via reflection.
     * When Jackson deserializes JSON → Java, it:
     *   1. Calls this no-arg constructor to create an empty Student object
     *   2. Calls setters to fill in the values from JSON
     *
     * Think of it as: "Create an empty student form, then fill in the fields."
     */
    public Student() {
    }

    /**
     * ALL-ARGS CONSTRUCTOR (with id)
     * Used when reading from the database (all fields including id are known).
     */
    public Student(int id, String name, String email, String course) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
    }

    /**
     * CONSTRUCTOR WITHOUT ID
     * Used when creating a NEW student (id is generated by the database).
     * The client sends name, email, and course — but not the id.
     */
    public Student(String name, String email, String course) {
        this.name = name;
        this.email = email;
        this.course = course;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // GETTERS AND SETTERS
    // ═══════════════════════════════════════════════════════════════════════════
    // WHY GETTERS/SETTERS?
    //   • Encapsulation: Fields are private (hidden). External code accesses
    //     them ONLY through these methods. This lets us add logic later
    //     (e.g., trim whitespace in setName()) without changing external code.
    //   • Framework requirement: Jackson, JdbcTemplate, and validation
    //     frameworks use getters/setters to read/write field values.
    // ═══════════════════════════════════════════════════════════════════════════

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // toString()
    // ═══════════════════════════════════════════════════════════════════════════
    // Provides a human-readable string representation of the object.
    // Incredibly useful for:
    //   • Debugging: System.out.println(student) shows field values
    //   • Logging: log.debug("Processing: {}", student)
    //   • Testing: Easy to see object state in test output
    //
    // Without toString(), printing a Student object shows:
    //   com.student.model.Student@1a2b3c4d  (unhelpful memory address)
    //
    // With toString(), it shows:
    //   Student{id=1, name='John Doe', email='john@example.com', course='CS'}
    // ═══════════════════════════════════════════════════════════════════════════

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                '}';
    }
}
