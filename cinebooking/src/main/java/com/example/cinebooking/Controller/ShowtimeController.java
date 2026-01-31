package com.example.cinebooking.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
import com.example.cinebooking.DTO.Showtime.AdminShowtimeRequest;
import com.example.cinebooking.DTO.Showtime.ShowtimeDetailDTO;
import com.example.cinebooking.service.ShowtimeService;

@RestController
@RequestMapping("/api")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    // ===================== PUBLIC =====================
     @GetMapping("/showtimes/by-date")
    public ResponseEntity<List<ShowtimeDetailDTO>> getShowtimesByDate(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(showtimeService.getScheduleByDate(date));
    }
    
    @GetMapping("/showtimes/{id}")
    public ResponseEntity<ShowtimeDetailDTO> getShowtimeDetail(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getShowtimeDetail(id));
    }

    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<List<SeatStatusDTO>> getSeatMap(@PathVariable Long id) {
        return ResponseEntity.ok(showtimeService.getSeatMapByShowtime(id));
    }

    // ===================== ADMIN =====================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/showtimes")
    public ResponseEntity<List<ShowtimeDetailDTO>> adminGetAllShowtimes() {
        return ResponseEntity.ok(showtimeService.adminGetAllShowtimes());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/showtimes")
    public ResponseEntity<ShowtimeDetailDTO> adminCreateShowtime(
            @RequestBody AdminShowtimeRequest req) {

        return ResponseEntity.ok(
                showtimeService.adminCreateShowtime(
                        req.getMovieId(),
                        req.getRoomId(),
                        req.getStartTime(),
                        req.getEndTime(),
                        req.getBasePrice()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/showtimes/{id}")
    public ResponseEntity<ShowtimeDetailDTO> adminUpdateShowtime(
            @PathVariable Long id,
            @RequestBody AdminShowtimeRequest req) {

        return ResponseEntity.ok(
                showtimeService.adminUpdateShowtime(
                        id,
                        req.getMovieId(),
                        req.getRoomId(),
                        req.getStartTime(),
                        req.getEndTime(),
                        req.getBasePrice()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/showtimes/{id}")
    public ResponseEntity<Void> adminDeleteShowtime(@PathVariable Long id) {
        showtimeService.adminDeleteShowtime(id);
        return ResponseEntity.noContent().build();
    }
}
