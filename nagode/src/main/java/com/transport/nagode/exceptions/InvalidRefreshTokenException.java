package com.transport.nagode.exceptions;

public class InvalidRefreshTokenException extends BusinessException{
    public InvalidRefreshTokenException(String message) {
        super(message, "INVALID_REFRESH_TOKEN", 401);
    }
}
