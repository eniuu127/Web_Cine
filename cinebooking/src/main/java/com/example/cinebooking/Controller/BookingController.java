package com.example.cinebooking.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Booking.BookingDetailDTO;
import com.example.cinebooking.DTO.Booking.BookingHistoryResponse;
import com.example.cinebooking.DTO.Booking.CreateBookingRequest;
import com.example.cinebooking.DTO.Booking.CreateBookingResponse;
import com.example.cinebooking.DTO.Payment.PaymentResponse;
import com.example.cinebooking.service.BookingHistoryService;
import com.example.cinebooking.service.BookingService;
import com.example.cinebooking.service.PaymentService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingHistoryService bookingHistoryService;
    private final PaymentService paymentService;

    public BookingController(
            BookingService bookingService,
            BookingHistoryService bookingHistoryService,
            PaymentService paymentService) {

        this.bookingService = bookingService;
        this.bookingHistoryService = bookingHistoryService;
        this.paymentService = paymentService;
    }

    // Tạo booking từ holdId
    // lấy userId từ JWT -> set vào request trước khi gọi service
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            @RequestBody CreateBookingRequest request,
            @RequestAttribute(value = "userId", required = false) Object userIdAttr
    ) {
        Long userId = toLong(userIdAttr);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        // ép theo JWT, không tin userId từ client
        request.setUserId(userId);

        CreateBookingResponse res = bookingService.createBooking(request);
        return ResponseEntity.ok(res);
    }

    // Lấy chi tiết booking (checkout page)
    @GetMapping("/{bookingCode}")
    public ResponseEntity<BookingDetailDTO> getBookingDetail(@PathVariable String bookingCode) {
        BookingDetailDTO res = bookingService.getBookingDetail(bookingCode);
        return ResponseEntity.ok(res);
    }

    // Xác nhận thanh toán thành công
    @PostMapping("/{bookingCode}/confirm-paid")
    public ResponseEntity<PaymentResponse> confirmPaid(@PathVariable String bookingCode) {
        PaymentResponse res = paymentService.confirmPaid(bookingCode);
        return ResponseEntity.ok(res);
    }

    // Lịch sử booking theo user
    @GetMapping("/history")
    public BookingHistoryResponse getMyBookingHistory(Authentication authentication) {
        Long userId = (Long)authentication.getPrincipal();
        return bookingHistoryService.getHistoryByUser(userId);

    }


    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof String s) return Long.valueOf(s);
        return Long.valueOf(v.toString());
    }
}
