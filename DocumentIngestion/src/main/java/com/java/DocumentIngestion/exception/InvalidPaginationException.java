package com.java.DocumentIngestion.exception;

public class InvalidPaginationException extends RuntimeException{
    public InvalidPaginationException(String message) {
        super(message);
    }
}
