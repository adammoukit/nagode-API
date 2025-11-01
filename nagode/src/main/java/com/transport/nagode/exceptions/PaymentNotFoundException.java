package com.transport.nagode.exceptions;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException(String message) {
        super(message, "PAYMENT_NOT_FOUND", 404);
    }

}
