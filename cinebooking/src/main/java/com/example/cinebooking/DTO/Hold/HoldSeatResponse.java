package com.example.cinebooking.DTO.Hold;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HoldSeatResponse {
    private String holdId;
    private LocalDateTime expiresAt;
    private Long showtimeId;
    private List<Long> seatIds;
}
