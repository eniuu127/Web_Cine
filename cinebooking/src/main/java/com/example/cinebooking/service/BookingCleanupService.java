package com.example.cinebooking.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Payment;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.PaymentRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingCleanupService {

    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;

    public BookingCleanupService(BookingRepository bookingRepo,
                                 PaymentRepository paymentRepo) {
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
    }

    @Transactional
    public void cancelExpiredBookings() {
        List<Booking> expired = bookingRepo.findByStatusAndExpiresAtBefore("PENDING", LocalDateTime.now());
        
        for(Booking booking : expired) {
            // 1. Hủy booking
            booking.setStatus("CANCELED");
            
            // 2. Neu có payment liên quan, hủy payment
            Payment payment = paymentRepo.findByBooking_BookingId(booking.getBookingId()).orElse(null);
            if(payment != null && "INIT".equals(payment.getStatus())) {
                payment.setStatus("CANCELED");
                paymentRepo.save(payment);
            }

            // 3) trả ghế: xoá booking_items bằng orphanRemoval=true
            // chỉ cần clear collection là JPA tự delete
            booking.getTickets().clear();

            // 4) có thể set expiresAt = null cho gọn
            booking.setExpiresAt(null);

            bookingRepo.save(booking);
        }
    }   
}
