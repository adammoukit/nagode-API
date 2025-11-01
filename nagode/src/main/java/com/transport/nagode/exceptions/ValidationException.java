package com.transport.nagode.exceptions;

public class ValidationException extends BusinessException{
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }
}
