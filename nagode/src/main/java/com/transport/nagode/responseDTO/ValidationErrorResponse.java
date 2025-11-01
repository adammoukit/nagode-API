package com.transport.nagode.responseDTO;


import java.util.Map;

public class ValidationErrorResponse {
    private String status = "ERROR";
    private String message = "Erreurs de validation";
    private Map<String, String> errors;

    public ValidationErrorResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    // Getters et setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
