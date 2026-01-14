package com.example.cinebooking.DTO.Booking;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateBookingRequest {
    // suất chiếu được chọn
    private Long showtimeId;

    // user đăng nhập
    private Long userId;

    // dùng khi userID = null
    private String guestEmail;

    // user chọn danh sách ghế
    private List<Long> seatIds;
    
}
