package com.transport.nagode.authDto;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

public class JwtResponse {
    private String token;
    private String refreshToken; // ✅ AJOUT
    private String type = "Bearer";
    private UUID id;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;

//    public JwtResponse(String token, UUID id, String email,
//                       Collection<? extends GrantedAuthority> authorities) {
//        this.token = token;
//
//        this.id = id;
//        this.email = email;
//        this.authorities = authorities;
//    }

    public JwtResponse(String token, String refreshToken, UUID id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    // Getters et setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) { this.authorities = authorities; }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}