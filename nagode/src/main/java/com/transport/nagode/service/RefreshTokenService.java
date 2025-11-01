package com.transport.nagode.service;


import com.transport.nagode.exceptions.InvalidRefreshTokenException;
import com.transport.nagode.models.RefreshToken;
import com.transport.nagode.models.User;
import com.transport.nagode.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.security.jwt.refresh-expiration:2592000000}") // 30 jours par défaut
    private long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Créer un nouveau refresh token pour un utilisateur
     */
    public RefreshToken createRefreshToken(User user, String deviceInfo, String ipAddress) {
        // Révoquer les anciens tokens du même appareil (optionnel)
        revokeUserDeviceTokens(user, deviceInfo);



        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(generateSecureToken());
        refreshToken.setUser(user);

        // ✅ CORRECTION : Convertir les millisecondes en minutes
        long refreshTokenDurationMinutes = refreshTokenDurationMs / (60 * 1000);
        refreshToken.setExpiryDate(LocalDateTime.now().plusMinutes(refreshTokenDurationMinutes));

        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Vérifier si un refresh token est valide
     */
    public RefreshToken verifyRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(RefreshToken::isValid)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token invalide ou expiré"));
    }

    /**
     * Révoquer un refresh token spécifique
     */
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.revokeToken(token);
    }

    /**
     * Révoquer tous les tokens d'un utilisateur
     */
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user.getId());
    }

    /**
     * Révoquer les tokens d'un appareil spécifique
     */
    public void revokeUserDeviceTokens(User user, String deviceInfo) {
        refreshTokenRepository.findByUserAndRevokedFalse(user).stream()
                .filter(token -> deviceInfo.equals(token.getDeviceInfo()))
                .forEach(RefreshToken::revoke);
    }

    /**
     * Générer un token sécurisé
     */
    private String generateSecureToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    /**
     * Nettoyer les tokens expirés (automatique)
     */
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    public void cleanExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
