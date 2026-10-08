package com.example.smartexpensetracker.exception;

/**
 * Exception thrown for validation failures, duplicate data, or invalid arguments.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
