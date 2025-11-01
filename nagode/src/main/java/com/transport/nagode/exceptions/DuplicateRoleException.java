package com.transport.nagode.exceptions;

public class  DuplicateRoleException extends BusinessException {
    public DuplicateRoleException(String message) {
        super(message, "DUPLICATE_ROLE", 409);
    }
}
