package com.transport.nagode.exceptions;

public class PermissionNotFoundException extends BusinessException {
    public PermissionNotFoundException(String message) {
        super(message, "PERMISSION_NOT_FOUND", 404);
    }
}
