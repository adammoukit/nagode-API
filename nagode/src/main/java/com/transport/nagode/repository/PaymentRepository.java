package com.transport.nagode.repository;

import com.transport.nagode.models.Payment;
import com.transport.nagode.models.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByBookingId(UUID bookingId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM Payment p WHERE p.transactionId = :transactionId")
    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatusAndPaymentDateBefore(PaymentStatus status, LocalDateTime date);
}
