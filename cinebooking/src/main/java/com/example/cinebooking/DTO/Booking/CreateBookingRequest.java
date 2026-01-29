package com.example.cinebooking.DTO.Booking;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateBookingRequest {
    // redis đã giữ ghế trước đó
    private String holdId;

    // user đăng nhập
    private Long userId;

}
