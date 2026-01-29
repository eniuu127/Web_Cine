package com.example.cinebooking.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.Movie.MovieDTO;
import com.example.cinebooking.DTO.Showtime.ShowtimeListDTO;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.service.MovieService;

@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;
    private final ShowtimeRepository showtimeRepository;

    public MovieController(MovieService movieService, ShowtimeRepository showtimeRepository) {
        this.movieService = movieService;
        this.showtimeRepository = showtimeRepository;
    }

    // ===================== PUBLIC =====================

    @GetMapping("/movies")
    public ResponseEntity<List<MovieDTO>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * ✅ BƯỚC 2: List suất chiếu theo phim + ngày (DTO nhẹ)
     * URL: GET /api/movies/{movieId}/showtimes?date=2026-01-28
     */
    @GetMapping("/movies/{movieId}/showtimes")
    public ResponseEntity<List<ShowtimeListDTO>> getShowtimesByMovieAndDate(
            @PathVariable Long movieId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        DateTimeFormatter hhmm = DateTimeFormatter.ofPattern("HH:mm");

        List<ShowtimeListDTO> result = showtimeRepository
                .findByMovieMovieIdAndStartTimeBetweenOrderByStartTimeAsc(movieId, from, to)
                .stream()
                .map(st -> new ShowtimeListDTO(
                        st.getShowtimeId(),
                        st.getStartTime().format(hhmm),
                        st.getRoom().getRoomName()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    // ===================== ADMIN =====================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/movies")
    public ResponseEntity<List<MovieDTO>> adminGetAllMovies() {
        return ResponseEntity.ok(movieService.adminGetAllMovies());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/movies")
    public ResponseEntity<MovieDTO> adminCreateMovie(@RequestBody MovieDTO req) {
        return ResponseEntity.ok(movieService.adminCreateMovie(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/movies/{id}")
    public ResponseEntity<MovieDTO> adminUpdateMovie(@PathVariable Long id,
                                                     @RequestBody MovieDTO req) {
        return ResponseEntity.ok(movieService.adminUpdateMovie(id, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/movies/{id}/status")
    public ResponseEntity<MovieDTO> adminSetStatus(@PathVariable Long id,
                                                   @RequestParam String status) {
        return ResponseEntity.ok(movieService.adminSetStatus(id, status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/movies/{id}")
    public ResponseEntity<Void> adminDeleteMovie(@PathVariable Long id) {
        movieService.adminDeleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
