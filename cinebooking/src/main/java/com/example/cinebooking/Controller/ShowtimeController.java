package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Showtime.ShowtimeDTO;
import com.example.cinebooking.service.ShowtimeService;


@RestController
@RequestMapping("api/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }
    @GetMapping("/by-movie/{movieId}")
    public List<ShowtimeDTO> getByMovie(@PathVariable Long movieId) {
        return showtimeService.getShowtimeByMovie(movieId);
    }
    
}
