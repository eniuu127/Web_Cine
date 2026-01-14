package com.example.cinebooking.DTO.Payment;

import lombok.*;

@Getter @Setter
public class PaymentResponse {
    private String bookingCode;
    private String methodCode;
    private Integer amount;
    private String paymentStatus; // SUCCESS, INIT
    private String bookingStatus; // PAID, PENDING, CANCELED
}
