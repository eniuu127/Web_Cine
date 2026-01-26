package com.example.cinebooking.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
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

    public List<SeatStatusDTO> getSeatsWithStatus(Long showtimeId){
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found" + showtimeId));

        List<Seat> seats = showtime.getRoom().getSeats();
        Set<Long> soldSeatIds = ticketRepository.findSeatIdsSoldByShowtimeId(showtimeId);
        Set<Long> heldSeatIds = ticketRepository.findSeatIdsHeldByShowtimeId(showtimeId);
        
        List<SeatStatusDTO> result = new ArrayList<>();
        for(Seat s : seats){
            Long seatId = s.getSeatId();
            String status;
            if(soldSeatIds.contains(seatId)) status = "SOLD";
            else if(heldSeatIds.contains(seatId)) status = "HELD";
            else status = "AVAILABLE";

            String rowLabel = (s.getRowIndex() == null)
                ? null
                : String.valueOf((char)('A' + s.getRowIndex()));

            Integer seatNumber = (s.getColIndex() == null)
                ? null
                : s.getColIndex() + 1;

            result.add(SeatStatusDTO.builder()
                    .seatId(s.getSeatId())
                    .seatCode(s.getSeatCode())
                    .seatType(s.getSeatType())
                    .rowIndex(s.getRowIndex())
                    .colIndex(s.getColIndex())
                    .status(status)
                    .build());
    }    
    result.sort(Comparator
                .comparing(SeatStatusDTO::getRowIndex, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SeatStatusDTO::getColIndex, Comparator.nullsLast(Integer::compareTo))
        );
    return result;
    }
}

    