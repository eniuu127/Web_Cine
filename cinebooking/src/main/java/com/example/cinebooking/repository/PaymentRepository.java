package com.example.cinebooking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking_BookingId(Long bookingId);
    Optional<Payment> findByBooking_BookingCode(String bookingCode);
}
