package com.transport.nagode.authDto;

import java.util.UUID;

public class RegisterResponse {
    private String message;
    private UUID userId;
    private String email;
    private String status;
    private String token;

    public RegisterResponse() {
    }

    public RegisterResponse(String message, UUID userId, String email, String status, String token) {
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}