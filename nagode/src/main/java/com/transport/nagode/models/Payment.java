package com.transport.nagode.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
public class Payment extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String paymentReference; // Ex: "PAY-20241201-001"

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true) // UNIQUE pour un seul paiement par booking
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private LocalDateTime processedAt;

    // For online payments
    private String transactionId; // ID from payment gateway
    private String gatewayResponse;
    private String gatewayName; // Stripe, PayPal, etc.

    // For card payments
    private String cardLastFour;
    private String cardBrand;

    // For bank transfers
    private String bankReference;
    private LocalDateTime transferDate;

    // Retry mechanism for failed payments
    private Integer retryCount = 0;
    private String failureReason;

    // Audit fields
    private String processedBy; // System, Admin username, etc.


    // Constructors
    public Payment() {}

    public Payment(Booking booking, Double amount, PaymentMethod paymentMethod) {
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = LocalDateTime.now();
        this.paymentReference = generatePaymentReference();
    }

    // Business methods
    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    public boolean canRetry() {
        return status == PaymentStatus.FAILED && retryCount < 3;
    }

    public void markAsCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
        this.retryCount = 0;
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.retryCount++;
        this.processedAt = LocalDateTime.now();
    }

    private String generatePaymentReference() {
        return "PAY-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "-" + String.format("%03d", (int)(Math.random() * 1000));
    }

    // Getters and Setters
    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }


    // Méthodes métier ajustées
    public boolean isFullPayment() {
        return booking != null && amount != null &&
                amount.equals(booking.getTotalAmount());
    }

    public void validateFullPayment() {
        if (!isFullPayment()) {
            throw new IllegalStateException("Le paiement doit couvrir la totalité du montant de la réservation");
        }
    }
}
