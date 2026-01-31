package com.example.cinebooking.DTO.Movie;

import java.time.LocalDate;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class AdminMovieRequest {
    private String title;
    private String description;
    private Integer runtime;
    private String posterUrl;
    private String trailerUrl;
    private String status;      // NOW_SHOWING / COMING_SOON / STOPPED ...
    private LocalDate releaseDate;
}
