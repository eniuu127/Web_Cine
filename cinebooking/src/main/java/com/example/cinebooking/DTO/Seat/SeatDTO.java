package com.example.cinebooking.DTO.Seat;

import lombok.*;

@Getter @Setter
// màn hình hiển thị thông tin ghế
public class SeatDTO {
    private Long seatId;
    private String seatCode;
    private String seatType;
    private Integer rowIndex;
    private Integer colIndex;
}
