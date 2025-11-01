package com.transport.nagode.exceptions;

public class CityNotFoundException extends BusinessException {
    public CityNotFoundException(String message) {
        super(message, "CITY_NOT_FOUND", 404);
    }
}
