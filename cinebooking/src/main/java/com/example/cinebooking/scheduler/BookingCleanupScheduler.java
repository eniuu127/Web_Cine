package com.example.cinebooking.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.service.RedisSeatHoldService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final RedisSeatHoldService redisSeatHoldService;

    // chạy mỗi 60 giây (đủ nhẹ + ổn cho demo)
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredPendingBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> expired = bookingRepository
                .findByStatusAndExpiresAtIsNotNullAndExpiresAtBefore("PENDING", now);

        if (expired.isEmpty()) return;

        for (Booking b : expired) {
            // 1) đóng trạng thái trong DB
            b.setStatus("CANCELLED");

            // 2) nhả hold trong Redis (xoá hold + seat locks)
            try {
                String holdId = b.getHoldId();
                if (holdId != null && !holdId.isBlank()) {
                    redisSeatHoldService.releaseHold(holdId);
                }
            } catch (Exception ignore) {
                // tránh job crash
            }
        }
        // @Transactional: Hibernate tự flush
    }
}

