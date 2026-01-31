package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Movie.MovieDTO;
import com.example.cinebooking.domain.entity.Movie;
import com.example.cinebooking.repository.MovieRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    // ============= Public cho User ============
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findByStatusOrderByCreatedAtDesc("NOW_SHOWING")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + id));
        return toDTO(movie);
    }

    // ======================== Admin ==============
    public List<MovieDTO> adminGetAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MovieDTO adminCreateMovie(MovieDTO req) {
        validateUpsert(req);

        Movie m = new Movie();
        m.setTitle(req.getTitle().trim());
        m.setPosterUrl(req.getPosterUrl().trim());
        m.setRuntime(req.getRuntime());
        m.setStatus(normalizeStatus(req.getStatus()));
         m.setTrailerUrl(req.getTrailerUrl());
        m = movieRepository.save(m);
        return toDTO(m);
    }

    public MovieDTO adminUpdateMovie(Long id, MovieDTO req) {
        validateUpsert(req);

        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + id));

        m.setTitle(req.getTitle().trim());
        m.setPosterUrl(req.getPosterUrl().trim());
        m.setRuntime(req.getRuntime());
        m.setStatus(normalizeStatus(req.getStatus()));
        m.setTrailerUrl(req.getTrailerUrl());
        m = movieRepository.save(m);
        return toDTO(m);
    }

    public MovieDTO adminSetStatus(Long id, String status) {
        Movie m = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + id));

        m.setStatus(normalizeStatus(status));
        m = movieRepository.save(m);
        return toDTO(m);
    }

    public void adminDeleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + id);
        }
        movieRepository.deleteById(id);
    }

    private void validateUpsert(MovieDTO req) {
        if (req == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is required");
        if (req.getTitle() == null || req.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        if (req.getPosterUrl() == null || req.getPosterUrl().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "posterUrl is required");
        if (req.getRuntime() == null || req.getRuntime() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "runtime must be > 0");
        if (req.getStatus() == null || req.getStatus().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        return status.trim().toUpperCase();
    }

    private MovieDTO toDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setMovieId(movie.getMovieId());
        dto.setTitle(movie.getTitle());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setRuntime(movie.getRuntime());
        dto.setStatus(movie.getStatus());
        return dto;
    }
}
