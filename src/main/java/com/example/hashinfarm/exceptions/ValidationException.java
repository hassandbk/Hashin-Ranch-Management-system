package com.example.hashinfarm.exceptions;

// Custom exception class for validation errors
public class ValidationException extends Exception {

    // Constructor that accepts a message
    public ValidationException(String message) {
        super(message); // Calls the constructor of the Exception class
    }

    // Constructor that accepts a message and a cause
    public ValidationException(String message, Throwable cause) {
        super(message, cause); // Calls the constructor of the Exception class with cause
    }
}

