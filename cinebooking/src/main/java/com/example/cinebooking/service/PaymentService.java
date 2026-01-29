package com.example.cinebooking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.DTO.Payment.SelectPaymentMethodRequest;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Payment;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Ticket;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.repository.PaymentRepository;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.TicketRepository;

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
    private final TicketRepository ticketRepo;
    private final SeatRepository seatRepo;
    private final RedisSeatHoldService holdService;

    // (OPTIONAL) nếu bạn đã chuyển logic tạo ticket sang BookingService.finalizePaidInternal
    // thì inject BookingService ở đây và gọi finalizePaidInternal thay cho block saveAll()
    // private final BookingService bookingService;

    public PaymentService(PaymentMethodRepository methodRepo,
                          PaymentRepository paymentRepo,
                          BookingRepository bookingRepo,
                          TicketRepository ticketRepo,
                          SeatRepository seatRepo,
                          RedisSeatHoldService holdService) {
        this.methodRepo = methodRepo;
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
        this.ticketRepo = ticketRepo;
        this.seatRepo = seatRepo;
        this.holdService = holdService;
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

        // nếu payment đã SUCCESS thì không cho đổi method nữa (tránh bừa)
        if (PAYMENT_SUCCESS.equalsIgnoreCase(payment.getStatus())) {
            throw badRequest("Payment already SUCCESS. Cannot change method.");
        }

        payment.setPaymentMethod(method);
        payment.setAmount(booking.getTotalAmount());
        payment = paymentRepo.save(payment);

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
     * Confirm payment SUCCESS -> tạo ticket -> booking PAID -> release hold
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
                // hiếm: booking PAID nhưng payment missing
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

        RedisSeatHoldService.HoldPayload hold;
        try {
            hold = holdService.getHoldOrThrow(booking.getHoldId());
        } catch (RuntimeException ex) {
            throw badRequest("Hold expired or not found");
        }

        // phải có payment INIT
        Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId())
                .orElseThrow(() -> badRequest("Payment not initialized. Please select method first."));

        // idempotent: nếu payment SUCCESS thì trả luôn
        if (PAYMENT_SUCCESS.equalsIgnoreCase(payment.getStatus())) {
            // đảm bảo booking cũng PAID (nếu lệch thì sync nhẹ)
            if (!BOOKING_PAID.equalsIgnoreCase(booking.getStatus())) {
                booking.setStatus(BOOKING_PAID);
                bookingRepo.save(booking);
            }
            return toResponse(booking, payment);
        }

        if (!PAYMENT_INIT.equalsIgnoreCase(payment.getStatus())) {
            throw badRequest("Payment is not in INIT status");
        }

        // ===== TẠO TICKET (SAVE ALL) =====
        // (DB unique (showtime_id, seat_id) vẫn là bảo vệ cuối)
        List<Ticket> tickets = new ArrayList<>();

        for (Long seatId : hold.getSeatIds()) {
            Seat seat = seatRepo.findById(seatId)
                    .orElseThrow(() -> badRequest("Seat not found: " + seatId));

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(booking.getShowtime());
            ticket.setSeat(seat);
            ticket.setPrice(booking.getShowtime().getBasePrice());
            tickets.add(ticket);
        }

        try {
            ticketRepo.saveAll(tickets);
        } catch (DataIntegrityViolationException ex) {
            // có ghế đã sold -> fail payment + cancel booking
            payment.setStatus(PAYMENT_FAILED);
            paymentRepo.save(payment);

            booking.setStatus(BOOKING_CANCELLED);
            bookingRepo.save(booking);

            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already booked");
        }

        // ===== UPDATE PAYMENT + BOOKING =====
        payment.setStatus(PAYMENT_SUCCESS);
        payment.setPaidAt(LocalDateTime.now());
        payment = paymentRepo.save(payment);

        booking.setStatus(BOOKING_PAID);
        bookingRepo.save(booking);

        // ===== RELEASE HOLD =====
        holdService.releaseHold(booking.getHoldId());

        return toResponse(booking, payment);
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
