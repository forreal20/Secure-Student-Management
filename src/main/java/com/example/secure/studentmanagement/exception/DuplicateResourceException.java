package com.example.secure.studentmanagement.exception;

public class DuplicateResourceException
        extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}