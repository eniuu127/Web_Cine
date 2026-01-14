package com.example.cinebooking.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.service.PaymentService;


@RestController
public class PaymentController {

    private final PaymentService paymentService;
 
    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }
    
    // post /api/payments/select-method
    @PostMapping("/select-method")
    public PaymentResponse selectMethod(@RequestBody SelectPaymentMethodRequest req) {
        return paymentService.selectMethod(req);
    }

    // post /api/payments/confirm/{bookingCode}
    @PostMapping("/confirm/{bookingCode}")
    public PaymentResponse confirm (@PathVariable String bookingCode) {
        return paymentService.confirmPaid(bookingCode);
    }
}