package com.example.cinebooking.DTO.Hold;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HoldSeatRequest {
    private Long showtimeId;

    // user đăng nhập
    private Long userId;

    // dùng khi userID = null
    private String guestEmail;

    private List<Long> seatIds;
}
