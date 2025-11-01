package com.transport.nagode.exceptions;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(String message) {
        super(message, "INVALID_REQUEST", 400);
    }
}
