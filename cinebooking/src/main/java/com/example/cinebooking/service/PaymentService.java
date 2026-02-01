package com.example.cinebooking.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Payment;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.repository.PaymentRepository;

@Service
public class PaymentService {

    // ===== STATUS CONSTANTS =====
    private static final String BOOKING_PENDING = "PENDING";
    private static final String BOOKING_PAID = "PAID";
    private static final String BOOKING_CANCELLED = "CANCELLED";

    private static final String PAYMENT_INIT = "INIT";
    private static final String PAYMENT_SUCCESS = "SUCCESS";
    private static final String PAYMENT_FAILED = "FAILED";

    private final PaymentMethodRepository methodRepo;
    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;
    private final RedisSeatHoldService holdService;

    // ✅ NEW: giao việc tạo ticket + publish event cho BookingService
    private final BookingService bookingService;

    public PaymentService(PaymentMethodRepository methodRepo,
                          PaymentRepository paymentRepo,
                          BookingRepository bookingRepo,
                          RedisSeatHoldService holdService,
                          BookingService bookingService) {
        this.methodRepo = methodRepo;
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
        this.holdService = holdService;
        this.bookingService = bookingService;
    }

    @Transactional
    public PaymentResponse selectMethod(SelectPaymentMethodRequest req) {

        if (req == null) {
            throw badRequest("Request is required");
        }
        if (isBlank(req.getBookingCode())) {
            throw badRequest("bookingCode is required");
        }
        if (isBlank(req.getMethodCode())) {
            throw badRequest("methodCode is required");
        }

        String bookingCode = req.getBookingCode().trim();
        String methodCode = req.getMethodCode().trim().toUpperCase();

        Booking booking = bookingRepo.findByBookingCode(bookingCode)
                .orElseThrow(() -> badRequest("Booking not found"));

        // chỉ cho chọn method khi booking còn PENDING
        if (!BOOKING_PENDING.equalsIgnoreCase(booking.getStatus())) {
            throw badRequest("Booking is not PENDING");
        }

        // booking phải có holdId và hold còn hiệu lực
        if (isBlank(booking.getHoldId())) {
            throw badRequest("Booking has no holdId");
        }
        try {
            holdService.getHoldOrThrow(booking.getHoldId());
        } catch (RuntimeException ex) {
            // giữ nguyên style của bạn (BAD_REQUEST), nếu muốn đúng nghiệp vụ hơn có thể dùng GONE
            throw badRequest("Hold expired or not found");
        }

        PaymentMethod method = methodRepo.findById(methodCode)
                .orElseThrow(() -> badRequest("Payment method not found: " + methodCode));

        if (!method.isActive()) {
            throw badRequest("Payment method is not active");
        }

        // tìm payment hiện có hoặc tạo mới
        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setBooking(booking);
                    p.setStatus(PAYMENT_INIT);
                    return p;
                });

        // nếu payment đã SUCCESS thì không cho đổi method nữa
        if (PAYMENT_SUCCESS.equalsIgnoreCase(payment.getStatus())) {
            throw badRequest("Payment already SUCCESS. Cannot change method.");
        }

        // ✅ 1) set vào PAYMENT
        payment.setPaymentMethod(method);
        payment.setAmount(booking.getTotalAmount());
        payment = paymentRepo.save(payment);

        // ✅ 2) set luôn vào BOOKING để /api/bookings/{code} trả về paymentMethodName/cCode đúng
        booking.setPaymentMethod(method);
        bookingRepo.save(booking);

        return toResponse(booking, payment);
    }

    /**
     * OPTIONAL READ-ONLY:
     * Dùng cho PaymentController GET /api/payments/{bookingCode}
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingCode(String bookingCode) {
        if (isBlank(bookingCode)) {
            throw badRequest("bookingCode is required");
        }

        Booking booking = bookingRepo.findByBookingCode(bookingCode.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        return toResponse(booking, payment);
    }

    /**
     * SINGLE SOURCE OF TRUTH:
     * Confirm payment SUCCESS -> bookingService.finalizePaidInternal (tạo ticket + booking PAID + release hold + publish mail)
     *
     * Idempotent:
     * - Nếu payment SUCCESS hoặc booking PAID => trả response luôn
     */
    @Transactional
    public PaymentResponse confirmPaid(String bookingCode) {

        if (isBlank(bookingCode)) {
            throw badRequest("bookingCode is required");
        }
        bookingCode = bookingCode.trim();

        Booking booking = bookingRepo.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        // Nếu booking đã PAID thì trả luôn (idempotent)
        if (BOOKING_PAID.equalsIgnoreCase(booking.getStatus())) {
            Payment p = paymentRepo.findByBooking_BookingId(booking.getBookingId()).orElse(null);
            if (p == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment missing for PAID booking");
            }
            return toResponse(booking, p);
        }

        if (BOOKING_CANCELLED.equalsIgnoreCase(booking.getStatus())) {
            throw badRequest("Booking cancelled");
        }

        // booking phải đang PENDING để confirm
        if (!BOOKING_PENDING.equalsIgnoreCase(booking.getStatus())) {
            throw badRequest("Booking is not PENDING");
        }

        // hold phải còn hiệu lực
        if (isBlank(booking.getHoldId())) {
            throw badRequest("Booking has no holdId");
        }

        try {
            holdService.getHoldOrThrow(booking.getHoldId());
        } catch (RuntimeException ex) {
            // ✅ đúng nghiệp vụ hơn: hold hết hạn -> 410 GONE
            throw new ResponseStatusException(HttpStatus.GONE, "Hold expired or not found");
        }

        // phải có payment INIT
        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
                .orElseThrow(() -> badRequest("Payment not initialized. Please select method first."));

        // idempotent: nếu payment SUCCESS thì trả luôn
        if (PAYMENT_SUCCESS.equalsIgnoreCase(payment.getStatus())) {
            // đảm bảo booking cũng PAID (nếu lệch thì finalize nhẹ)
            if (!BOOKING_PAID.equalsIgnoreCase(booking.getStatus())) {
                bookingService.finalizePaidInternal(bookingCode);
                booking = bookingRepo.findByBookingCode(bookingCode)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
            }
            return toResponse(booking, payment);
        }

        if (!PAYMENT_INIT.equalsIgnoreCase(payment.getStatus())) {
            throw badRequest("Payment is not in INIT status");
        }

        // ✅ 1) Mark payment SUCCESS trước (mô phỏng payment gateway ok)
        payment.setStatus(PAYMENT_SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepo.save(payment);

        // ✅ 2) Giao phần tạo ticket + chống double sell + release hold + publish mail cho BookingService
        try {
            bookingService.finalizePaidInternal(bookingCode);
        } catch (ResponseStatusException ex) {
            // nếu conflict / gone... thì payment fail cho đúng
            payment.setStatus(PAYMENT_FAILED);
            paymentRepo.save(payment);
            throw ex;
        } catch (RuntimeException ex) {
            payment.setStatus(PAYMENT_FAILED);
            paymentRepo.save(payment);
            throw ex;
        }

        // reload booking để trả response chuẩn
        Booking updatedBooking = bookingRepo.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        return toResponse(updatedBooking, payment);
    }

    // ===== helpers =====
    private PaymentResponse toResponse(Booking booking, Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.setBookingCode(booking.getBookingCode());
        res.setMethodCode(payment.getPaymentMethod() != null ? payment.getPaymentMethod().getCode() : null);
        res.setAmount(payment.getAmount());
        res.setPaymentStatus(payment.getStatus());  // SUCCESS / INIT / FAILED
        res.setBookingStatus(booking.getStatus());  // PAID / PENDING / CANCELLED
        return res;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
