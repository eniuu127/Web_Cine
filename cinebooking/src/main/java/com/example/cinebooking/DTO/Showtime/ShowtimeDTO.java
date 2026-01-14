package com.example.cinebooking.DTO.Showtime;

import java.time.LocalDateTime;

import lombok.*;

@Getter @Setter
public class ShowtimeDTO {
    private Long showtimeId;
    private LocalDateTime startTime;
    private Integer basePrice;
    private String status;
}
// hiển thị danh sách suất chiếu của 1 phim khi user đã chọn phim