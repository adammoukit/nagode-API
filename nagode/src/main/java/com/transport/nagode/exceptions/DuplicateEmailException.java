package com.transport.nagode.exceptions;

public class DuplicateEmailException extends BusinessException {
    public DuplicateEmailException(String message) {
        super(message, "DUPLICATE_EMAIL", 409);
    }
}
