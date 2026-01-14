package com.example.cinebooking.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.cinebooking.service.BookingCleanupService;

@Component
public class BookingCleanupScheduler {

    private final BookingCleanupService bookingCleanupService;
    public BookingCleanupScheduler(BookingCleanupService bookingCleanupService) {
        this.bookingCleanupService = bookingCleanupService;
    }

    // chay moi 1 phut
    @Scheduled(fixedRate = 60_000)
    public void cleanupExpiredBookings(){
        bookingCleanupService.cancelExpiredBookings();
    }
}
