package com.transport.nagode.exceptions;

public class AuthenticationException extends BusinessException{
    public AuthenticationException(String message) {
        super(message, "AUTH_ERROR", 401 );
    }
}
