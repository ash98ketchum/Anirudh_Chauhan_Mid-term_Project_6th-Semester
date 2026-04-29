package com.student.repository;

import com.student.model.Student;

import java.util.List;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * STUDENT REPOSITORY INTERFACE — The Data Access Contract
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * WHY AN INTERFACE?
 * ─────────────────
 * This interface defines WHAT operations are available, without specifying HOW
 * they are implemented. This is the "Dependency Inversion Principle" (the 'D'
 * in SOLID):
 *
 *   • The Service layer depends on this INTERFACE, not on a concrete class.
 *   • Tomorrow, if you want to switch from JDBC to MongoDB, you just create
 *     a new implementation (StudentMongoRepository) without changing a single
 *     line in the Service layer.
 *
 * Analogy: Think of this as a restaurant menu. The menu (interface) says
 *          "we serve pasta." The kitchen (implementation) decides HOW to make
 *          the pasta. If you hire a new chef (switch database), the menu
 *          stays the same — customers (Service layer) don't need to know.
 *
 * WHERE ELSE ARE INTERFACES USED?
 *   • Java Collections: List<T> interface → ArrayList, LinkedList implementations
 *   • JDBC: Connection interface → PostgreSQL, MySQL, Oracle implement it
 *   • Strategy Pattern: Define algorithm interfaces, swap implementations at runtime
 */
public interface StudentRepository {

    /**
     * Saves a new student to the database.
     *
     * @param student The student object to persist (id is NOT set; DB generates it).
     * @return The saved student WITH the database-generated id populated.
     */
    Student save(Student student);

    /**
     * Retrieves ALL students from the database.
     *
     * @return A List of all Student objects. Returns an EMPTY list (not null)
     *         if no students exist. Returning null would force every caller to
     *         do null checks — returning an empty list is safer and cleaner.
     */
    List<Student> findAll();

    /**
     * Finds a single student by their id.
     *
     * @param id The primary key to search for.
     * @return An Optional<Student> — either contains the student (if found)
     *         or is empty (if not found).
     *
     * WHY Optional<Student> INSTEAD OF Student?
     *   • Returning null is a common source of NullPointerException bugs.
     *   • Optional forces the caller to explicitly handle the "not found" case.
     *   • Example:
     *       Optional<Student> result = repository.findById(99);
     *       result.ifPresent(s -> System.out.println(s.getName()));
     *       // vs. Student s = repository.findById(99); s.getName(); // 💥 NPE if null
     */
    Optional<Student> findById(int id);

    /**
     * Updates an existing student's data.
     *
     * @param id      The id of the student to update.
     * @param student The new data to apply (name, email, course).
     * @return The updated student object.
     */
    Student update(int id, Student student);

    /**
     * Deletes a student by their id.
     *
     * @param id The id of the student to delete.
     * @return true if a row was deleted, false if no student with that id exists.
     */
    boolean delete(int id);

    /**
     * Checks if a student with the given email already exists.
     * Used to enforce email uniqueness at the application level
     * (in addition to the database UNIQUE constraint).
     *
     * @param email The email to check.
     * @return true if the email is already taken, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a student with the given email exists, EXCLUDING a specific student id.
     * Used during UPDATE to allow a student to keep their own email without
     * triggering a "duplicate email" error.
     *
     * @param email The email to check.
     * @param excludeId The student id to exclude from the check.
     * @return true if another student (with a different id) has this email.
     */
    boolean existsByEmailAndIdNot(String email, int excludeId);
}
