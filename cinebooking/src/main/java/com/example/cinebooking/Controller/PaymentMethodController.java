package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Payment.PaymentMethodDTO;
import com.example.cinebooking.service.PaymentMethodService;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    public List<PaymentMethodDTO> getActiveMethods() {
        return paymentMethodService.getActiveMethods();
    }
}
