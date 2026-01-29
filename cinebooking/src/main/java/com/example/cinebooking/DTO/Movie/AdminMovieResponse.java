package com.example.cinebooking.DTO.Movie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor; import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminMovieResponse {
    private Long movieId;
    private String title;
    private Integer runtime;
    private String posterUrl;
    private String status;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;
}

