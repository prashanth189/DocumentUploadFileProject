package com.java.DocumentIngestion.exception;

public class DocumentNotFoundException  extends RuntimeException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}
