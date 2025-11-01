package com.transport.nagode.auth;

import com.transport.nagode.models.Role;
import com.transport.nagode.models.User;
import com.transport.nagode.repository.UserRepository;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountLockedException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Vérification des tentatives de login
        if (loginAttemptService.isBlocked(username)) {
            try {
                throw new AccountLockedException("Compte temporairement bloqué suite à trop de tentatives");
            } catch (AccountLockedException e) {
                throw new RuntimeException(e);
            }
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        // Vérifications de sécurité
        if (!user.isEnabled()) {
            throw new DisabledException("Compte désactivé");
        }
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Compte verrouillé");
        }
        if (!user.isAccountNonExpired()) {
            try {
                throw new AccountExpiredException("Compte expiré");
            } catch (AccountExpiredException e) {
                throw new RuntimeException(e);
            }
        }
        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("Identifiants expirés");
        }

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                getAuthorities(user.getRoles()),
                user.getId()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }
}
