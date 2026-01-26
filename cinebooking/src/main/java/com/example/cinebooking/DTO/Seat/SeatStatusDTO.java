package com.example.cinebooking.DTO.Seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatStatusDTO {
    private Long seatId;
    private String seatCode;   // A1, A2...
    private Integer rowIndex;  // 0,1,2...
    private Integer colIndex;  // 0,1,2...
    private String seatType;   // STANDARD/VIP
    private String status;
}
