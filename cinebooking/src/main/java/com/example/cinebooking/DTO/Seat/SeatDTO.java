package com.example.cinebooking.DTO.Seat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatDTO {
    private Long seatId;
    private String seatCode;
    private String seatType;
    private Integer rowIndex;
    private Integer colIndex;
}
