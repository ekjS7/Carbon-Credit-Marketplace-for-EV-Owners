package com.example.demo.exception;

public class InvalidEmissionDataException extends RuntimeException {
    public InvalidEmissionDataException(String message) {
        super(message);
    }
}
