package com.java.DocumentIngestion.exception;

public class InvalidSearchQueryException extends RuntimeException{
    public InvalidSearchQueryException(String message) {
        super(message);
    }
}
