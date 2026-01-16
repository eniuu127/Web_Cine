package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Movie.MovieDTO;
import com.example.cinebooking.domain.entity.Movie;
import com.example.cinebooking.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    
    public List<MovieDTO> getAllMovies(){
        return movieRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(Long id){
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found : " + id));
        return toDTO(movie); 
    }
    
    // mapper entity -> DTO
    private MovieDTO toDTO(Movie movie){
        MovieDTO dto = new MovieDTO();
        dto.setMovieId(movie.getMovieId());
        dto.setTitle(movie.getTitle());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setRuntime(movie.getRuntime());
        dto.setStatus(movie.getStatus());
        return dto;
    }
}
