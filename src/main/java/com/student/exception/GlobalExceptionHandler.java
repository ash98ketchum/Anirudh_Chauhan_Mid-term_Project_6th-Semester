package com.student.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════════════════════════
 * GLOBAL EXCEPTION HANDLER — The Safety Net for ALL Errors
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * @ControllerAdvice is a Spring annotation that makes this class a GLOBAL
 * exception handler. It intercepts exceptions thrown by ANY controller in
 * the application before they reach the client.
 *
 * WITHOUT @ControllerAdvice:
 *   • Every controller method needs its own try-catch blocks.
 *   • Error responses are inconsistent (different formats, missing fields).
 *   • Stack traces leak to clients (security risk).
 *
 * WITH @ControllerAdvice:
 *   • ONE centralized place handles ALL exceptions.
 *   • Consistent error response format (our ErrorResponse class).
 *   • Clean controller methods (no try-catch clutter).
 *
 * HOW IT WORKS:
 *   1. Controller throws an exception (e.g., StudentNotFoundException)
 *   2. Spring intercepts it BEFORE sending a response to the client
 *   3. Spring looks for a @ExceptionHandler method in @ControllerAdvice
 *      that matches the exception type
 *   4. That handler method builds a proper ResponseEntity and returns it
 *
 * Analogy: Think of this as a hospital's emergency room. No matter what
 *          department (controller) a patient (request) comes from, if
 *          something goes wrong (exception), they're sent to the ER
 *          (ControllerAdvice) for proper treatment (error response).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles StudentNotFoundException → HTTP 404 Not Found.
     *
     * @ExceptionHandler tells Spring: "When a StudentNotFoundException
     * is thrown anywhere in the app, call THIS method to handle it."
     *
     * The exception parameter is automatically injected by Spring.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException ex) {
        log.warn("Student not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),       // 404
                "Not Found",
                ex.getMessage()                      // "Student not found with id: 99"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateEmailException → HTTP 409 Conflict.
     *
     * HTTP 409 means "the request conflicts with the current state of the
     * resource" — perfect for duplicate email scenarios.
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        log.warn("Duplicate email: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),         // 409
                "Conflict",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Handles validation errors (from @Valid + @NotBlank, @Email, etc.)
     * → HTTP 400 Bad Request.
     *
     * When @Valid fails, Spring throws MethodArgumentNotValidException.
     * We extract ALL field errors and combine them into a readable message.
     *
     * Example output: "name: Name is required; email: Email must be valid"
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", details);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),       // 400
                "Validation Failed",
                details
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Catch-all handler for any UNEXPECTED exceptions.
     * This prevents stack traces from leaking to clients.
     *
     * IMPORTANT: Log the full exception (with stack trace) for debugging,
     * but send only a generic message to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);  // Full stack trace in logs
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 500
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
