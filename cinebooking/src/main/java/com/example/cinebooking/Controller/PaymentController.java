package com.example.cinebooking.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * TASK 4:
     * Chọn phương thức thanh toán cho booking
     * - Tạo Payment (INIT) nếu chưa có
     * - Gán paymentMethod
     * - Booking vẫn ở trạng thái PENDING
     *
     * FE dùng endpoint này trước khi confirm-paid
     */
    @PostMapping("/select-method")
    public ResponseEntity<PaymentResponse> selectPaymentMethod
            (@RequestBody SelectPaymentMethodRequest request) {
        return ResponseEntity.ok(paymentService.selectMethod(request));
    }


    /**
     * (OPTIONAL – READ ONLY)
     * Lấy thông tin payment theo bookingCode
     * FE có thể dùng để hiển thị trạng thái thanh toán
     */
    @GetMapping("/{bookingCode}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(@PathVariable String bookingCode) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingCode(bookingCode));
    }
}
