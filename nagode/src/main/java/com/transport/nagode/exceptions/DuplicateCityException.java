package com.transport.nagode.exceptions;

public class DuplicateCityException extends BusinessException {
    public DuplicateCityException(String message) {
        super(message, "DUPLICATE_CITY", 409);
    }
}