package com.example.cinebooking.Controller;

import com.example.cinebooking.DTO.Booking.BookingHistoryResponse;
import com.example.cinebooking.DTO.Booking.CreateBookingRequest;
import com.example.cinebooking.DTO.Booking.CreateBookingResponse;
import com.example.cinebooking.service.BookingHistoryService;
import com.example.cinebooking.service.BookingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingHistoryService bookingHistoryService;

    public BookingController(BookingService bookingService,
                             BookingHistoryService bookingHistoryService) {
        this.bookingService = bookingService;
        this.bookingHistoryService = bookingHistoryService;
    }

    // POST /api/bookings  (chọn ghế -> hold ghế 5p)
    @PostMapping
    public CreateBookingResponse createBooking(@RequestBody CreateBookingRequest req) {
        return bookingService.createBooking(req);
    }

    // GET /api/bookings/history/{userId}
    @GetMapping("/history/{userId}")
    public BookingHistoryResponse getHistory(@PathVariable Long userId) {
        return bookingHistoryService.getHistoryByUserId(userId);
    }
}
