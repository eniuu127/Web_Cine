package com.example.cinebooking.DTO.Seat;

import lombok.*;

@Getter @Setter
// màn hình hiển thị thông tin ghế
public class SeatDTO {
    private Long seatId;
    private String seatCode;
    private String seatType;

    private boolean booked; // trạng thái ghế đã được đặt hay chưa
    
}
