package com.transport.nagode.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cargo")
public class Cargo extends AbstractEntity{
    @Column(nullable = false, unique = true)
    private String trackingNumber; // Ex: "CRG-20241201-001"

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    // For non-registered senders
    private String senderName;
    private String senderPhone;
    private String senderAddress;

    @Column(nullable = false)
    private String recipientName;

    private String recipientPhone;

    @Column(nullable = false)
    private String recipientAddress;

    @ManyToOne
    @JoinColumn(name = "destination_city_id")
    private City destinationCity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoType type;

    @Column(nullable = false)
    private Double weight; // en kg

    private Double volume; // en m³

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoStatus status = CargoStatus.PENDING;

    @Column(nullable = false)
    private Double shippingCost;

    private LocalDateTime pickupDate;

    private LocalDateTime deliveryDate;

    private String specialInstructions; // Fragile, à garder au frais, etc.

    private String currentLocation; // Pour le suivi

    private Double declaredValue; // Valeur déclarée pour l'assurance

    // Constructors
    public Cargo() {}

    public Cargo(String trackingNumber, Trip trip, String recipientName,
                 String recipientAddress, CargoType type, Double weight, Double shippingCost) {
        this.trackingNumber = trackingNumber;
        this.trip = trip;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.type = type;
        this.weight = weight;
        this.shippingCost = shippingCost;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public City getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(City destinationCity) {
        this.destinationCity = destinationCity;
    }

    public CargoType getType() {
        return type;
    }

    public void setType(CargoType type) {
        this.type = type;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CargoStatus getStatus() {
        return status;
    }

    public void setStatus(CargoStatus status) {
        this.status = status;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public LocalDateTime getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDateTime pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Double getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(Double declaredValue) {
        this.declaredValue = declaredValue;
    }
}
