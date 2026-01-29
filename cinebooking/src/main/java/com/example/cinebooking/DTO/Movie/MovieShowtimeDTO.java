package com.example.cinebooking.DTO.Movie;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MovieShowtimeDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long roomId;
    private String roomName;

    public MovieShowtimeDTO(Long id, LocalDateTime startTime, LocalDateTime endTime,
                            Long roomId, String roomName) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
        this.roomName = roomName;
    }
}
