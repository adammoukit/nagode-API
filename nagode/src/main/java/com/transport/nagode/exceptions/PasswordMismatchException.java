package com.transport.nagode.exceptions;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(String message) {
        super(message, "PASSWORD_MISMATCH", 400);
    }
}
