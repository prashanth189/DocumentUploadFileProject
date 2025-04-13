package com.java.DocumentIngestion.exception;

public class DatabaseAccessException extends RuntimeException{
    public DatabaseAccessException(String message) {
        super(message);
    }
}
