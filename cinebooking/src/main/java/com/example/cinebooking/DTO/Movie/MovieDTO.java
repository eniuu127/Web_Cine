package com.example.cinebooking.DTO.Movie;

import lombok.*;

@Getter @Setter
public class MovieDTO {
    private Long movieId;
    private String title;
    private String posterUrl;
    private Integer duration; 
    private String status;
}
