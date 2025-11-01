package com.transport.nagode.service;


import com.transport.nagode.auth.CustomUserDetails;
import com.transport.nagode.auth.CustomUserDetailsService;
import com.transport.nagode.authDto.RegisterRequest;
import com.transport.nagode.exceptions.*;
import com.transport.nagode.models.City;
import com.transport.nagode.models.Role;
import com.transport.nagode.models.User;

import com.transport.nagode.repository.CityRepository;
import com.transport.nagode.repository.RoleRepository;
import com.transport.nagode.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       CityRepository cityRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cityRepository = cityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    public User registerUser(RegisterRequest registerRequest) throws RoleNotFoundException {
        // Validation des données
        //J'ai commenté cette partie justement parce j'ai déléguer la vidation à Spring dans la class GlobalexceptionHandler
        // Qui capture l'Exception MethodArgumentNotVAlidException, qui est l'Exception qui est est capturée lorsqu'une contrainte
        // de validité est levée dans un DTO avec des contraintes comme: @NotBlank(message), @Email(message), @Pattern(regex, message), @Size(min, message)

        validateRegistrationRequest(registerRequest);

        // Vérification si l'email existe déjà
        if (emailExists(registerRequest.getEmail())) {
            throw new DuplicateEmailException("Un utilisateur avec cet email existe déjà");
        }

        // Vérification de la correspondance des mots de passe
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new PasswordMismatchException("Les mots de passe ne correspondent pas");
        }

        // Création de l'utilisateur
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAddress(registerRequest.getAddress());

        // Gestion de la ville
        if (registerRequest.getCityId() != null) {
            City city = cityRepository.findById(registerRequest.getCityId())
                    .orElseThrow(() -> new CityNotFoundException("Ville non trouvée"));
            user.setCity(city);
        }

        // Gestion des rôles
        Set<Role> roles = resolveRoles(registerRequest.getRoles());
        user.setRoles(roles);

        // Champs spécifiques aux chauffeurs
        user.setDriverLicenseNumber(registerRequest.getDriverLicenseNumber());
        user.setLicenseExpiryDate(registerRequest.getLicenseExpiryDate());

        // Champs de sécurité
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setActive(true);

        return userRepository.save(user);
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidRequestException("L'email est obligatoire");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new InvalidRequestException("Format d'email invalide");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new InvalidRequestException("Le mot de passe doit contenir au moins 6 caractères");
        }
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidRequestException("Le prénom est obligatoire");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidRequestException("Le nom est obligatoire");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new InvalidRequestException("Le numero de telephone est obligatoire");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private Set<Role> resolveRoles(Set<String> roleNames) throws RoleNotFoundException {
        Set<Role> roles = new HashSet<>();

        if (roleNames == null || roleNames.isEmpty()) {
            // Rôle par défaut : PASSENGER (plus logique que DRIVER par défaut)
            Role defaultRole = roleRepository.findByName("ROLE_PASSENGER")
                    .orElseGet(() -> createDefaultPassengerRole());
            roles.add(defaultRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RoleNotFoundException("Rôle non trouvé: " + roleName));
                roles.add(role);
            }
        }

        return roles;
    }
    private Role createDefaultPassengerRole() {
        // Créer le rôle PASSENGER s'il n'existe pas
        Role passengerRole = new Role();
        passengerRole.setName("ROLE_PASSENGER");
        return roleRepository.save(passengerRole);
    }

//    private Set<Role> resolveRoles(Set<String> roleNames) {
//        Set<Role> roles = new HashSet<>();
//
//        if (roleNames == null || roleNames.isEmpty()) {
//            // Rôle par défaut : DRIVER
//            Role defaultRole = roleRepository.findByName("ROLE_DRIVER")
//                    .orElseThrow(() -> new RuntimeException("Rôle DRIVER non trouvé"));
//            roles.add(defaultRole);
//        } else {
//            for (String roleName : roleNames) {
//                Role role = roleRepository.findByName(roleName)
//                        .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleName));
//                roles.add(role);
//            }
//        }
//
//        return roles;
//    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User updateUser(UUID userId, RegisterRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Mise à jour des champs
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getCityId() != null) {
            City city = cityRepository.findById(updateRequest.getCityId())
                    .orElseThrow(() -> new RuntimeException("Ville non trouvée"));
            user.setCity(city);
        }
        if (updateRequest.getDriverLicenseNumber() != null) {
            user.setDriverLicenseNumber(updateRequest.getDriverLicenseNumber());
        }
        if (updateRequest.getLicenseExpiryDate() != null) {
            user.setLicenseExpiryDate(updateRequest.getLicenseExpiryDate());
        }

        return userRepository.save(user);
    }

    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActive(false);
        user.setEnabled(false);
        userRepository.save(user);
    }

    public String extractEmailFromAuthHeader(String authHeader) {
        // Implémentez l'extraction de l'email depuis le token JWT
        // Cela dépend de votre implémentation JWT
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Utilisez votre JwtTokenProvider pour extraire l'email
            // return jwtTokenProvider.extractUsername(token);
        }
        throw new RuntimeException("Token invalide");
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String token = request.getHeader("Authorization");

        if(token != null && token.startsWith("Bearer ")){
            token.substring(7);
        }
        return token;
    }

    public CustomUserDetails getUserDetails(String email){
        try {
            return (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}