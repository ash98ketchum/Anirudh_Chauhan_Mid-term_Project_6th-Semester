package com.student.service;

import com.student.exception.DuplicateEmailException;
import com.student.exception.StudentNotFoundException;
import com.student.model.Student;
import com.student.repository.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * SERVICE IMPLEMENTATION — Where Business Logic Lives
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * @Service is a specialization of @Component (like @Repository). It:
 *   1. Marks this class for component scanning (Spring creates a singleton bean)
 *   2. Conveys semantic meaning: "this is a service class with business logic"
 *
 * This class ORCHESTRATES the flow:
 *   • Validates business rules (duplicate email check)
 *   • Delegates data operations to the Repository
 *   • Throws domain exceptions (StudentNotFoundException)
 *   • Logs operations for auditing/debugging
 *
 * DEPENDENCY INJECTION:
 *   This class depends on StudentRepository (the interface, NOT the concrete
 *   class). Spring automatically injects StudentJdbcRepository because it's
 *   the only class that implements StudentRepository. This is the "Program
 *   to an interface, not an implementation" principle.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    /**
     * Constructor injection — Spring injects the StudentRepository bean.
     * Since StudentJdbcRepository is annotated with @Repository, Spring
     * knows to inject that implementation.
     */
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Creates a new student after checking for duplicate emails.
     *
     * BUSINESS RULE: Email must be unique across all students.
     * We check this at the service level for a clear error message.
     * The database UNIQUE constraint is the final safety net.
     */
    @Override
    public Student createStudent(Student student) {
        log.debug("Creating student: {}", student);

        // Business rule: no duplicate emails
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }

        Student saved = studentRepository.save(student);
        log.info("Student created successfully: {}", saved);
        return saved;
    }

    /**
     * Returns all students. If the table is empty, returns an empty list.
     */
    @Override
    public List<Student> getAllStudents() {
        log.debug("Retrieving all students");
        List<Student> students = studentRepository.findAll();
        log.debug("Retrieved {} students", students.size());
        return students;
    }

    /**
     * Finds a student by id or throws StudentNotFoundException.
     *
     * The repository returns Optional<Student>. We use .orElseThrow()
     * to convert an empty Optional into a meaningful exception.
     * This exception is then caught by GlobalExceptionHandler and
     * returned as an HTTP 404 response.
     */
    @Override
    public Student getStudentById(int id) {
        log.debug("Retrieving student with id: {}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    /**
     * Updates a student after verifying:
     *   1. The student exists (throws 404 if not)
     *   2. The new email isn't taken by ANOTHER student (throws 409 if so)
     */
    @Override
    public Student updateStudent(int id, Student student) {
        log.debug("Updating student with id: {} → {}", id, student);

        // Verify student exists
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        // Verify email uniqueness (excluding the current student)
        if (studentRepository.existsByEmailAndIdNot(student.getEmail(), id)) {
            throw new DuplicateEmailException(student.getEmail());
        }

        Student updated = studentRepository.update(id, student);
        log.info("Student updated successfully: {}", updated);
        return updated;
    }

    /**
     * Deletes a student by id. Throws 404 if the student doesn't exist.
     */
    @Override
    public void deleteStudent(int id) {
        log.debug("Deleting student with id: {}", id);

        // Verify student exists before deleting (for a clear 404 message)
        studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));

        studentRepository.delete(id);
        log.info("Student deleted successfully with id: {}", id);
    }
}
