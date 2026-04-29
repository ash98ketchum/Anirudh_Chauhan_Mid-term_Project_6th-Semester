package com.student.exception;

import java.time.LocalDateTime;

/**
 * Structured error response DTO returned to the client when an error occurs.
 * This ensures ALL error responses have a consistent JSON format:
 *
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Student not found with id: 99",
 *   "timestamp": "2026-04-29T14:30:00"
 * }
 *
 * Without this, Spring would return its default error format, which exposes
 * internal details (stack traces, class names) that are security risks.
 */
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
