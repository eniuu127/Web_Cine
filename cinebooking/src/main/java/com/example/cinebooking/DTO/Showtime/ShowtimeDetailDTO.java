package com.example.cinebooking.DTO.Showtime;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowtimeDetailDTO {

    private Long showtimeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
        private Long movieId;
        private String title;
        private String posterUrl;
        private Integer runtime;
        private String status;
    
        private Long roomId;
        private String roomName;
        private String screenType;
        private Integer basePrice;
}
