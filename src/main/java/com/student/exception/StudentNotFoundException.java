package com.student.exception;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * CUSTOM EXCEPTION: Student Not Found
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * This exception is thrown when a requested student does not exist in the database.
 *
 * WHY A CUSTOM EXCEPTION?
 * ───────────────────────
 * Java has generic exceptions like IllegalArgumentException, but they don't
 * convey domain-specific meaning. When you see "StudentNotFoundException",
 * you immediately know:
 *   • WHAT happened: A student wasn't found
 *   • WHERE to look: The student lookup logic
 *   • WHAT to return: HTTP 404 Not Found
 *
 * Using generic exceptions would require reading the error message to understand
 * the problem. Custom exceptions are SELF-DOCUMENTING.
 *
 * WHY EXTEND RuntimeException (not Exception)?
 * ─────────────────────────────────────────────
 * Java has two types of exceptions:
 *
 * 1. CHECKED EXCEPTIONS (extend Exception):
 *    • The compiler FORCES you to handle them (try-catch or throws declaration).
 *    • Used for recoverable situations (FileNotFoundException — try another file).
 *    • Problem: They clutter method signatures and force unnecessary catch blocks.
 *
 * 2. UNCHECKED EXCEPTIONS (extend RuntimeException):
 *    • The compiler does NOT force you to handle them.
 *    • They propagate UP the call stack until someone catches them.
 *    • Used for programming errors or unrecoverable situations.
 *    • Spring's @ControllerAdvice catches them globally — no need for try-catch
 *      in every controller method.
 *
 * We use RuntimeException because:
 *   • "Student not found" should bubble up to the global exception handler.
 *   • We don't want every service/controller method to declare "throws StudentNotFoundException".
 *   • Spring MVC is designed to work with unchecked exceptions.
 *
 * Analogy: A checked exception is like a fire drill (planned, everyone must
 *          participate). An unchecked exception is like an actual fire alarm —
 *          it goes off and the building's fire system (ControllerAdvice) handles it.
 */
public class StudentNotFoundException extends RuntimeException {

    /**
     * Constructor that creates a descriptive error message.
     *
     * @param id The id of the student that was not found.
     *
     * super(message) → Passes the message to RuntimeException's constructor,
     * which stores it internally. It can be retrieved later with getMessage().
     */
    public StudentNotFoundException(int id) {
        super("Student not found with id: " + id);
    }

    /**
     * Constructor with a custom message for more flexible usage.
     *
     * @param message A custom error message.
     */
    public StudentNotFoundException(String message) {
        super(message);
    }
}
