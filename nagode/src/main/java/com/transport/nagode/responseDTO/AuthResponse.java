package com.transport.nagode.responseDTO;

import java.util.UUID;

public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(UUID userId, String email, String fullName, String token) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.token = token;
    }

    // Getters et setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
