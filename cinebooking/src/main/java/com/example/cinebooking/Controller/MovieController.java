package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Movie.MovieDTO;
import com.example.cinebooking.service.MovieService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor

public class MovieController {

    private final MovieService movieService;
    
    @GetMapping
    public List<MovieDTO> getAllMovies(){
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public MovieDTO getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }
    
}
