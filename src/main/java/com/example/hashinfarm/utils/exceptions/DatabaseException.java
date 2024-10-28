package com.example.hashinfarm.utils.exceptions;

// Custom exception class for database errors
public class DatabaseException extends Exception {

    // Constructor that accepts a message
    public DatabaseException(String message) {
        super(message);  // Calls the constructor of the Exception class
    }

    // Constructor that accepts a message and a cause
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);  // Calls the constructor of the Exception class with cause
    }
}
