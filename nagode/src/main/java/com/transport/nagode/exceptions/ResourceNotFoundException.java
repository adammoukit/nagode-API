package com.transport.nagode.exceptions;

public class ResourceNotFoundException extends BusinessException{
    public ResourceNotFoundException(String message) {
        super(message,  "NOT_FOUND", 404);
    }
}
