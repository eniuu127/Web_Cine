package com.example.cinebooking.DTO.Booking;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateBookingResponse {
    private String bookingCode;
    private String status;
    private Integer totalAmount;

    // thời điểm hết hạn giữ ghế
    private LocalDateTime expiresAt;

    // danh sách ghế đã đặt
    private List<String> seatCodes;
}
