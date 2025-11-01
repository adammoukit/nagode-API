package com.transport.nagode.exceptions;

public class AuthorizationException extends BusinessException{
    public AuthorizationException(String message) {
        super(message, "AUTHZ_ERROR", 403);
    }
}
