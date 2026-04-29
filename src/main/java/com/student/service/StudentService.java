package com.student.service;

import com.student.model.Student;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * STUDENT SERVICE INTERFACE — The Business Logic Contract
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHY A SERVICE LAYER?
 * ────────────────────
 * The Service layer sits BETWEEN the Controller and Repository:
 *
 *   Client → Controller → SERVICE → Repository → Database
 *
 * It is responsible for BUSINESS LOGIC — rules that go beyond simple CRUD:
 *   • "Check if the email is already taken before saving"
 *   • "Only allow students to enroll in active courses"
 *   • "Send a welcome email after registration"
 *   • "Log the operation for auditing"
 *
 * SEPARATION OF CONCERNS:
 *   • Controller: Handles HTTP (request parsing, response formatting)
 *   • Service: Contains business rules (validation, orchestration)
 *   • Repository: Handles database operations (SQL, connection management)
 *
 * Without a Service layer, business logic leaks into Controllers, making
 * them bloated and untestable. The Service layer keeps things organized.
 *
 * Analogy: In a restaurant:
 *   • Controller = Waiter (takes orders, serves food, talks to customer)
 *   • Service = Chef (knows the recipes, decides how to cook, quality checks)
 *   • Repository = Pantry (stores and retrieves raw ingredients)
 *   The waiter doesn't cook (no business logic in controllers).
 *   The chef doesn't talk to customers (service doesn't handle HTTP).
 *   The pantry doesn't know recipes (repository doesn't have business rules).
 */
public interface StudentService {

    Student createStudent(Student student);

    List<Student> getAllStudents();

    Student getStudentById(int id);

    Student updateStudent(int id, Student student);

    void deleteStudent(int id);
}
