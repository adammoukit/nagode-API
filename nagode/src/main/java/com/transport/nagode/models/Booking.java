package com.transport.nagode.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking extends AbstractEntity{
    @Column(nullable = false, unique = true)
    private String bookingNumber; // Ex: "BK-20241201-001"

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;

    // For non-registered customers
    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;

    @Column(nullable = false)
    private Integer numberOfSeats = 1;

    private String seatNumbers; // "A1,A2" or "12,13"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false)
    private Double totalAmount;



    private LocalDateTime bookingDate;

    private String specialRequirements; // Handicap, enfant, etc.


    // REMPLACER OneToMany par OneToOne
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment; // Un seul paiement par réservation

    // QR Code for ticket scanning
    //private String qrCodeData;

    // Constructors
    public Booking() {}

    public Booking(Trip trip, Integer numberOfSeats, Double totalAmount) {
        this.bookingNumber = generateBookingNumber();
        this.trip = trip;
        this.numberOfSeats = numberOfSeats;
        this.totalAmount = totalAmount;
        this.bookingDate = LocalDateTime.now();
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public String getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(String seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSpecialRequirements() {
        return specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public Payment getPayment() {
        return payment;
    }

    // Méthodes utilitaires mises à jour
    public boolean isFullyPaid() {
        return payment != null && payment.isSuccessful() &&
                payment.getAmount().equals(totalAmount);
    }

    public Double getAmountPaid() {
        return (payment != null && payment.isSuccessful()) ? payment.getAmount() : 0.0;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setBooking(this);
        }
    }

    // Méthode pour obtenir le solde restant (toujours 0 ou totalAmount)
    public Double getRemainingAmount() {
        if (payment != null && payment.isSuccessful()) {
            return 0.0; // Déjà payé
        }
        return totalAmount; // Pas encore payé
    }

    private String generateBookingNumber(){
        return "BK-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+"-"+String.format("%03d", (int)(Math.random() * 1000));
    }





}
