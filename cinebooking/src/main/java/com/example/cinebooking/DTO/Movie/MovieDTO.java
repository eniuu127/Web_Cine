package com.example.cinebooking.DTO.Movie;

import lombok.*;

@Getter @Setter
public class MovieDTO {
    private Long movieId;
    private String title;
    private String posterUrl;
    private String trailerUrl;
    private Integer runtime; 
    private String status;
}
