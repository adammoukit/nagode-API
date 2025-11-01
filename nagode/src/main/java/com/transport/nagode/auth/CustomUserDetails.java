package com.transport.nagode.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails extends User {
    private final UUID id;
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, UUID id) {
        super(username, password, authorities);
        this.id = id;
    }

    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, UUID id) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
