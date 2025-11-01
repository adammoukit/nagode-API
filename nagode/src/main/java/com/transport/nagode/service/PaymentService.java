package com.transport.nagode.service;

import com.transport.nagode.exceptions.PaymentNotFoundException;
import com.transport.nagode.models.Payment;
import com.transport.nagode.models.PaymentStatus;
import com.transport.nagode.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment findByPaymentReference(String paymentReference){
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(()-> new PaymentNotFoundException("Aucun paiement n'existe pour la reference : "+paymentReference));

        return payment;
    }

    public Payment getPaymentByBookingId(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Aucun paiement trouvé pour la réservation: " + bookingId
                ));
    }


    //Ici la méthode pour proceder au paiement.
    public void processPaymentIfExists(UUID bookingId) {
        paymentRepository.findByBookingId(bookingId)
                .ifPresent(payment -> {
                    // Traitement seulement si le paiement existe
                    //processPayment(payment);
                });
    }

    // ✅ VÉRIFIER l'existence d'un paiement
    public boolean isBookingPaid(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .map(Payment::isSuccessful)
                .orElse(false);
    }


    // ✅ LISTER les paiements par statut
    public List<Payment> findPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    // ✅ MÉTHODE avec transformation Optional
    public PaymentStatus getPaymentStatus(UUID bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .map(Payment::getStatus)
                .orElse(PaymentStatus.PENDING); // Statut par défaut si pas de paiement
    }

    // ✅ SUPPRIMER un paiement avec vérification
    public void cancelPayment(UUID bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Impossible d'annuler - paiement non trouvé"
                ));

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }

}
