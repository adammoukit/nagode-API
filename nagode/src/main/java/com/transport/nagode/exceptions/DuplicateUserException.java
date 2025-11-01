package com.transport.nagode.exceptions;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String message) {
        super(message, "DUPLICATE_USER", 409);
    }
}
