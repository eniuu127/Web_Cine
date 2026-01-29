package com.example.cinebooking.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.Booking.BookingDetailDTO;
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

    //Tạo booking từ holdId
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            @RequestBody CreateBookingRequest request) {

        CreateBookingResponse res = bookingService.createBooking(request);
        return ResponseEntity.ok(res);
    }

   
     //Lấy chi tiết booking (checkout page)
    @GetMapping("/{bookingCode}")
    public ResponseEntity<BookingDetailDTO> getBookingDetail(
            @PathVariable String bookingCode) {

        BookingDetailDTO res = bookingService.getBookingDetail(bookingCode);
        return ResponseEntity.ok(res);
    }

     // Xác nhận thanh toán thành công
    @PostMapping("/{bookingCode}/confirm-paid")
    public ResponseEntity<PaymentResponse> confirmPaid(
            @PathVariable String bookingCode) {

        PaymentResponse res = paymentService.confirmPaid(bookingCode);
        return ResponseEntity.ok(res);
    }

    // Lịch sử booking theo user 
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getBookingHistory(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                bookingHistoryService.getHistoryByUserId(userId)
        );
    }
}
