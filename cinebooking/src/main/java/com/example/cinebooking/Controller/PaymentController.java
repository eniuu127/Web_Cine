package com.example.cinebooking.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentController(PaymentService paymentService,
                             PaymentMethodRepository paymentMethodRepository) {
        this.paymentService = paymentService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    /**
     * ✅ Checkout needs this:
     * GET /api/payment-methods  (public)
     */
    @GetMapping(path = "/../payment-methods") // maps to /api/payment-methods
    public ResponseEntity<List<PaymentMethodItem>> getPaymentMethods() {

        List<PaymentMethodItem> items = paymentMethodRepository
                .findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(pm -> new PaymentMethodItem(
                        getCode(pm),
                        getName(pm)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    // DTO trả về cho FE
    public static class PaymentMethodItem {
        public String code;
        public String name;

        public PaymentMethodItem(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    // ⚠️ tuỳ entity của bạn đặt field gì
    private String getCode(PaymentMethod pm) {
        // ưu tiên code nếu có, không có thì dùng id
        try {
            var m = pm.getClass().getMethod("getCode");
            Object v = m.invoke(pm);
            return v == null ? null : v.toString();
        } catch (Exception ignore) { }

        try {
            var m = pm.getClass().getMethod("getId");
            Object v = m.invoke(pm);
            return v == null ? null : v.toString();
        } catch (Exception ignore) { }

        return null;
    }

    private String getName(PaymentMethod pm) {
        try {
            var m = pm.getClass().getMethod("getName");
            Object v = m.invoke(pm);
            return v == null ? null : v.toString();
        } catch (Exception ignore) { }

        try {
            var m = pm.getClass().getMethod("getMethodName");
            Object v = m.invoke(pm);
            return v == null ? null : v.toString();
        } catch (Exception ignore) { }

        return null;
    }

    /**
     * TASK 4:
     * Chọn phương thức thanh toán cho booking
     * - Tạo Payment (INIT) nếu chưa có
     * - Gán paymentMethod
     * - Booking vẫn ở trạng thái PENDING
     */
    @PostMapping("/select-method")
    public ResponseEntity<PaymentResponse> selectPaymentMethod(@RequestBody SelectPaymentMethodRequest request) {
        return ResponseEntity.ok(paymentService.selectMethod(request));
    }

    /**
     * (OPTIONAL – READ ONLY)
     * Lấy thông tin payment theo bookingCode
     */
    @GetMapping("/{bookingCode}")
    public ResponseEntity<PaymentResponse> getPaymentByBooking(@PathVariable String bookingCode) {
        return ResponseEntity.ok(paymentService.getPaymentByBookingCode(bookingCode));
    }
}
