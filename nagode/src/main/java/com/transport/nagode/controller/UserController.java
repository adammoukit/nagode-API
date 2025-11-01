package com.transport.nagode.controller;

import com.transport.nagode.authDto.RegisterRequest;
import com.transport.nagode.models.User;
import com.transport.nagode.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and #userId == principal.id)")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId,
                                        @RequestBody RegisterRequest updateRequest) {
        try {
            User updatedUser = userService.updateUser(userId, updateRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Utilisateur mis à jour avec succès");
            response.put("userId", updatedUser.getId());
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable UUID userId) {
        try {
            userService.deactivateUser(userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur désactivé avec succès");
            response.put("status", "SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraire l'email du token JWT (vous devrez implémenter cette méthode)
            String email = userService.extractEmailFromAuthHeader(authHeader);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Créer une réponse de profil (vous pouvez créer un DTO pour cela)
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("firstName", user.getFirstName());
            profile.put("lastName", user.getLastName());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("address", user.getAddress());
            profile.put("roles", user.getRoles());
            profile.put("driverLicenseNumber", user.getDriverLicenseNumber());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            response.put("status", "ERROR");
            return ResponseEntity.badRequest().body(response);
        }
    }


}
