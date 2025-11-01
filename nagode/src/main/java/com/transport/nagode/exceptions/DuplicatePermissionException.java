package com.transport.nagode.exceptions;

public class DuplicatePermissionException extends BusinessException {
    public DuplicatePermissionException(String message) {
        super(message, "DUPLICATE_PERMISSION", 409);
    }
}
