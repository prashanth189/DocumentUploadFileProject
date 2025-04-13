package com.java.DocumentIngestion.exception;

public class DocumentKeyNotFoundException extends RuntimeException{
    public DocumentKeyNotFoundException(String message) {
        super(message);
    }
}
