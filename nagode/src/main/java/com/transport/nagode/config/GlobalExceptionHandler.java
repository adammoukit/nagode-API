package com.transport.nagode.config;

import com.transport.nagode.exceptions.BusinessException;
import com.transport.nagode.responseDTO.ApiResponse;
import com.transport.nagode.responseDTO.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Gestion des exceptions métier
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        logger.warn("Business exception: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        response.setCode(ex.getHttpStatus());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    // Gestion des erreurs de validation (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // ✅ Retourne ApiResponse au lieu de ValidationErrorResponse
        ApiResponse<Map<String, String>> response = ApiResponse.error(
                "Erreurs de validation détectées",
                errors
        );
        response.setCode(400);
        response.setPath(request.getRequestURI());

        logger.warn("Validation errors for {}: {}", request.getRequestURI(), errors);

        return ResponseEntity.badRequest().body(response);
    }

    // Gestion des accès refusés
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        logger.warn("Access denied: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Accès refusé. Vous n'avez pas les permissions nécessaires."
        );
        response.setCode(403);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(403).body(response);
    }

    // Gestion des exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error: ", ex);

        ApiResponse<Object> response = ApiResponse.error(
                "Une erreur interne est survenue. Veuillez réessayer."
        );
        response.setCode(500);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(500).body(response);
    }

    // Gestion des erreurs JWT
    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(
            io.jsonwebtoken.ExpiredJwtException ex, HttpServletRequest request) {

        ApiResponse<Object> response = ApiResponse.error(
                "Session expirée. Veuillez vous reconnecter."
        );
        response.setCode(401);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(401).body(response);
    }

    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    public ResponseEntity<ApiResponse<Object>> handleSignatureException(
            io.jsonwebtoken.security.SignatureException ex, HttpServletRequest request) {

        ApiResponse<Object> response = ApiResponse.error(
                "Token invalide."
        );
        response.setCode(401);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(401).body(response);
    }
}
