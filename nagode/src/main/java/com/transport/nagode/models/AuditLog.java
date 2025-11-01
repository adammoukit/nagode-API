package com.transport.nagode.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends AbstractEntity{
    @Column(nullable = false)
    private String eventType; // LOGIN_SUCCESS, LOGIN_FAILURE, etc.

    private String username;

    @Column(nullable = false)
    private String ipAddress;

    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    private boolean success;

    public AuditLog() {
    }

    public AuditLog(String eventType, String username, String ipAddress, String userAgent, String details, LocalDateTime timestamp, boolean success) {
        this.eventType = eventType;
        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.details = details;
        this.timestamp = timestamp;
        this.success = success;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
