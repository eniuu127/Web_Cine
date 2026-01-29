package com.example.cinebooking.DTO.Showtime;

import java.time.LocalDateTime;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class AdminShowtimeRequest {
    private Long movieId;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // optional (auto nếu null)
    private Integer basePrice;
    private String status; // OPEN / CANCELLED
}
