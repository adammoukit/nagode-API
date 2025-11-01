package com.transport.nagode.service;


import com.transport.nagode.auth.CustomUserDetails;
import com.transport.nagode.auth.JwtTokenProvider;
import com.transport.nagode.auth.LoginAttemptService;
import com.transport.nagode.authDto.*;
import com.transport.nagode.exceptions.*;
import com.transport.nagode.models.RefreshToken;
import com.transport.nagode.models.User;
import com.transport.nagode.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService; // ✅ AJOUT

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService, UserService userService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest, String deviceInfo, String ipAddress ) {
        try {
            // Authentification via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );



            // Réinitialiser les tentatives de connexion en cas de succès
            loginAttemptService.loginSucceeded(loginRequest.getEmail());

            // Génération des tokens
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


            //Ici j'ai décidé de recuperer l'utilisateur courant à partir de son email
            User user = userService.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new  ResourceNotFoundException("Cet Email n'existe pas !!!!"));

            //Ici c'est notre access Token
            String accessToken = jwtTokenProvider.generateToken(userDetails);

            //Ici c'est notre refresh token
            // ✅ GENERER ET STOCKER REFRESH TOKEN
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(
                    user, deviceInfo, ipAddress
            );


            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new JwtResponse(
                    accessToken,
                    refreshToken.getToken(), // ✅ INCLURE LE REFRESH TOKEN
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities()
            );
        } catch (BadCredentialsException e) {
            // Enregistrer l'échec de connexion
            loginAttemptService.loginFailed(loginRequest.getEmail());
            throw new AuthenticationException("Email ou mot de passe incorrect");
        } catch (Exception e) {
            // Pour les autres erreurs d'authentification
            loginAttemptService.loginFailed(loginRequest.getEmail());
            throw new AuthenticationException("Erreur d'authentification: " + e.getMessage());
        }
    }

    // Ici est resèrvé à la logique de génération du refresh token;
//**
//        * ✅ NOUVELLE METHODE : Rafraîchir le token
//     */
    public TokenRefreshResponse refreshToken(String refreshToken, String deviceInfo, String ipAddress) {
        // Vérifier le refresh token
        RefreshToken storedToken = refreshTokenService.verifyRefreshToken(refreshToken);

        // Générer nouveau access token
        CustomUserDetails userDetails = userService.getUserDetails(storedToken.getUser().getEmail());
        String newAccessToken = jwtTokenProvider.generateToken(userDetails);

        // Optionnel : créer un nouveau refresh token (rotation)
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
                storedToken.getUser(), deviceInfo, ipAddress
        );

        // Révoquer l'ancien refresh token
        refreshTokenService.revokeRefreshToken(storedToken.getToken());

        return new TokenRefreshResponse(newAccessToken, newRefreshToken.getToken());
    }

    /**
     * ✅ AMELIORER le logout pour révoquer le refresh token
     */
    public void logout(String authHeader, String refreshToken) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Révoquer le refresh token si fourni
            if (refreshToken != null) {
                refreshTokenService.revokeRefreshToken(refreshToken);
            }

            // Nettoyer le contexte de sécurité
            SecurityContextHolder.clearContext();
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return jwtTokenProvider.isTokenExpired(token);
        } catch (Exception e) {
            return true;
        }
    }



    //Ici c'est la methode de la logique metier qui va nous permettre de creer l'utilisateur dans la base de données;
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        try {
            // Création de l'utilisateur
            User user = userService.registerUser(registerRequest);

            String token = authenticateUserAfterRegistration(user.getEmail(), registerRequest.getPassword());

            return new RegisterResponse(
                    "Utilisateur créé avec succès",
                    user.getId(),
                    user.getEmail(),
                    "SUCCESS",
                    "Voici le token: "+token
            );

        } catch (DuplicateEmailException | PasswordMismatchException | InvalidRequestException e) {
            // Relancer les exceptions métier spécifiques
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    /**
     * Authentifie l'utilisateur automatiquement après l'inscription
     */
    private String authenticateUserAfterRegistration(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Génération des tokens
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            return jwtTokenProvider.generateToken(userDetails);

        } catch (Exception e) {
            // Log l'erreur mais ne bloque pas l'inscription
            System.err.println("Échec de l'authentification automatique après inscription: " + e.getMessage());
            return null;
        }

    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Vérifier le mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mot de passe actuel incorrect");
        }

        // Mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean validateCurrentPassword(String email, String password) {
        CustomUserDetails userDetails = userService.getUserDetails(email);

        return passwordEncoder.matches(password, userDetails.getPassword());
    }
}