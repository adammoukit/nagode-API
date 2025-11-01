package com.transport.nagode.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration:86400000}") // 24h par défaut
    private long jwtExpirationMs;

    @Value("${app.security.jwt.refresh-expiration:604800000}") // 7 jours
    private long refreshTokenExpirationMs;

    // Génération du token avec claims custom
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Claims standards
        claims.put("sub", userDetails.getUsername());
        claims.put("iat", new Date());
        claims.put("iss", "transport-saas-app");

        // Claims custom
        if (userDetails instanceof CustomUserDetails customUser) {
            claims.put("userId", customUser.getId());
            claims.put("roles", customUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        return buildToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Validation avancée du token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT invalide: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Claims JWT vide: {}", e.getMessage());
        }
        return false;
    }



    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🧭 Extraction du username (ou email, car il est stocké dans `sub`)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🧭 Alias plus explicite
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraction du tenant ID
    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenantId", String.class));
    }

    // Extraction des rôles
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object rawRoles = claims.get("roles"); // On récupère la valeur brute
            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(rawRoles, new TypeReference<List<String>>() {});
        });
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }



    // Vérification si le token va bientôt expirer (pour le refresh)
    public boolean isTokenExpiringSoon(String token, long thresholdMs) {
        Date expiration = extractExpiration(token);
        long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
        return timeUntilExpiration <= thresholdMs;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}