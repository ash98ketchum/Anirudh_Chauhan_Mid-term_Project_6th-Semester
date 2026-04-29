package com.student.exception;

/**
 * Custom exception for duplicate email conflicts.
 * Thrown when a student tries to register with an email already in use.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A student with email '" + email + "' already exists");
    }
}
