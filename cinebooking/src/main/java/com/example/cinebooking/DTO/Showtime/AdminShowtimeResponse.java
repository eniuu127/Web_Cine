package com.example.cinebooking.DTO.Showtime;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor; import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminShowtimeResponse {
    private Long showtimeId;
    private Long movieId;
    private String movieTitle;
    private Long roomId;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer basePrice;
    private String status;
}
