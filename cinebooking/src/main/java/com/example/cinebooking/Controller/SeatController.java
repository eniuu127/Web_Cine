package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
import com.example.cinebooking.service.SeatService;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;
    
    public SeatController(SeatService seatService){
        this.seatService = seatService;
    }

    @GetMapping("/by-showtime/{showtimeId}")
    public List<SeatStatusDTO> getSeatsByShowtime(@PathVariable Long showtimeId){
        return seatService.getSeatsWithStatus(showtimeId);
    }

    // done api [{"seatId":1,"seatCode":"A1","rowIndex":0,"colIndex":0,"seatType":"STANDARD","status":"AVAILABLE"},
}
