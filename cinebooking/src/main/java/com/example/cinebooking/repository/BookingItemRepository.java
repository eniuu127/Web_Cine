package com.example.cinebooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.BookingItem;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    List<BookingItem> findByBooking_BookingId(Long bookingId); 

}
