package com.example.cinebooking.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;

@Service

public class SeatService {

    private final ShowtimeRepository showtimeRepository;
    private final TicketRepository ticketRepository;
    private final RedisSeatHoldService holdService;

    public SeatService(ShowtimeRepository showtimeRepository,  
                    TicketRepository ticketRepository,
                    RedisSeatHoldService holdService) {
        this.showtimeRepository = showtimeRepository;
        this.ticketRepository = ticketRepository;
        this.holdService = holdService;
    }

    public List<SeatStatusDTO> getSeatsWithStatus(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Showtime not found: " + showtimeId));

        List<Seat> seats = showtime.getRoom().getSeats();

        // SOLD: từ DB (ticket thuộc booking PAID)
        Set<Long> soldSeatIds =
            ticketRepository.findSeatIdsSoldByShowtimeId(showtimeId);

        // HELD: từ Redis (SCAN)
        Set<Long> heldSeatIds =
            holdService.getHeldSeatIds(showtimeId);

        List<SeatStatusDTO> result = new ArrayList<>();

        for (Seat seat : seats) {
            Long seatId = seat.getSeatId();
            String status;

            if (soldSeatIds.contains(seatId)) {
                status = "SOLD";
            } else if (heldSeatIds.contains(seatId)) {
                status = "HELD";
            } else {
                status = "AVAILABLE";
            }

            result.add(SeatStatusDTO.builder()
                    .seatId(seatId)
                    .seatCode(seat.getSeatCode())
                    .seatType(seat.getSeatType())
                    .rowIndex(seat.getRowIndex())
                    .colIndex(seat.getColIndex())
                    .status(status)
                    .build());
        }

        result.sort(Comparator
                .comparing(SeatStatusDTO::getRowIndex, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SeatStatusDTO::getColIndex, Comparator.nullsLast(Integer::compareTo)));

        return result;
    }
}

    