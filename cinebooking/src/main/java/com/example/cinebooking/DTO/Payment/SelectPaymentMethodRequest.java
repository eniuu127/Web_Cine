package com.example.cinebooking.DTO.Payment;

import lombok.*;

@Getter @Setter
public class SelectPaymentMethodRequest {
    private String bookingCode;
    private String methodCode;
}
