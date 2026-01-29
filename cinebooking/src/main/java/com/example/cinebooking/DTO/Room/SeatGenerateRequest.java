package com.example.cinebooking.DTO.Room;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class SeatGenerateRequest {
    private Integer rows;        // ví dụ 10
    private Integer cols;        // ví dụ 12
    private String seatType;     // STANDARD (default)
}
