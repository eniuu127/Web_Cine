package com.example.cinebooking.DTO.Booking;

import lombok.*;
import java.util.List;

@Getter @Setter
public class BookingHistoryResponse {
    private Long userId;
    private Integer totalBookings;
    private List<BookingHistoryItemDTO> bookings;
}
