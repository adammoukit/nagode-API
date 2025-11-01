package com.transport.nagode.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    private String messageType; // INFO, WARNING, ALERT, SUCCESS

    @Column(name = "is_read", nullable = false) // ✅ CHANGEMENT IC
    private boolean read = false;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private String actionUrl; // URL pour une action supplémentaire

    // For system-wide notifications
    //private boolean isBroadcast = false;

    // Constructors
    public Notification() {}

    public Notification(String title, String message, String messageType) {
        this.title = title;
        this.message = message;
        this.messageType = messageType;
        this.sentAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }


}
