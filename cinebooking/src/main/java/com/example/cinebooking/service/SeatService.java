package com.example.cinebooking.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Seat.SeatDTO;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;

@Service
public class SeatService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    public SeatService(ShowtimeRepository showtimeRepository, 
                    SeatRepository seatRepository, 
                    TicketRepository ticketRepository) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<SeatDTO> getSeatsByShowtime(Long showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));

        // ghế thuộc phòng của showtime
        List<Seat> seats = seatRepository.findByRoom_RoomIdOrderBySeatCodeAsc(showtime.getRoom().getRoomId());

        // lấy danh sách ghế đã được đặt cho suất chiếu này
        Set<Long> bookedSeatIds = new HashSet<>(
            ticketRepository.findBookedSeatIdsByShowtimeId(showtimeId));
        
        return seats.stream().map(seat -> {
            SeatDTO dto = new SeatDTO();
            dto.setSeatId(seat.getSeatId());
            dto.setSeatCode(seat.getSeatCode());
            dto.setSeatType(seat.getSeatType());
            dto.setBooked(bookedSeatIds.contains(seat.getSeatId()));
            return dto;
        }).collect(Collectors.toList()); 
    }    
}

    