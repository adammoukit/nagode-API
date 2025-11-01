package com.transport.nagode.controller;

import com.transport.nagode.authDto.*;
import com.transport.nagode.responseDTO.ApiResponse;
import com.transport.nagode.service.AuthService;
import com.transport.nagode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }



    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(request);

        JwtResponse jwtResponse = authService.authenticateUser(loginRequest, deviceInfo, ipAddress);


        ApiResponse<JwtResponse> apiResponse = ApiResponse.success("Connexion réussi!!", jwtResponse);
        apiResponse.setCode(200);
        apiResponse.setPath(request.getServletPath());
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        authService.logout(authHeader, refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Déconnexion réussie");
        response.put("status", "SUCCESS");
        response.put("Sécurité", "Le context de sécurité de spring à été supprimer!!!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-password")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        boolean isValid = authService.validateCurrentPassword(email, password);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

//    private String extractEmailFromAuthHeader(String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            return "email"; // À adapter selon votre implémentation JWT
//        }
//        throw new RuntimeException("Token manquant ou invalide");
//    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.registerUser(registerRequest);

        ApiResponse<RegisterResponse> apiResponse = ApiResponse.success("Utilisateur créé avec succès", response);
        apiResponse.setCode(201);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        authService.changePassword(email, currentPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Mot de passe modifié avec succès");
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@PathVariable String email) {
        boolean exists = userService.emailExists(email);

        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("available", !exists);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {

        String deviceInfo = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIpAddress(httpRequest);

        TokenRefreshResponse response = authService.refreshToken(
                request.getRefreshToken(), deviceInfo, ipAddress
        );

        ApiResponse<TokenRefreshResponse> apiResponse = ApiResponse.success(
                "Token rafraîchi avec succès", response
        );
        apiResponse.setCode(200);
        apiResponse.setPath(httpRequest.getServletPath());

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * ✅ METHODE UTILITAIRE : Obtenir l'IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}