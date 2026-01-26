package com.example.cinebooking.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;
    // private final SeatHoldRepository holdRepo;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                            SeatRepository seatRepo,
                            TicketRepository ticketRepo
                            // SeatHoldRepository holdRepo
    ) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        // this.holdRepo = holdRepo;
    }

    public List<SeatStatusDTO> getSeatMapByShowtime(Long showtimeId){
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Long roomId = st.getRoom().getRoomId();

        // 1 lấy tất cả ghế trong phòng
        List<Seat> seats = seatRepo.findByRoom_RoomId(roomId);

        // 2 lấy tất cả ticket đã bán 
        Set<Long> soldSeatIds = ticketRepo.findByShowtime_ShowtimeId(showtimeId)
                .stream()
                .map(t -> t.getSeat().getSeatId())
                .collect(Collectors.toSet());
                
        // 3 lấy tất cả ghế đang hold (nếu có)
        // Set<Long> heldSeatIds = holdRepo
        //        .findByShowtime_ShowtimeIdAndExpiresAtAfter(showtimeId, Instant.now())
        //        .stream()
        //        .map(h -> h.getSeat().getSeatId())
        //        .collect(Collectors.toSet());
     return seats.stream().map(s -> {
            SeatStatusDTO dto = new SeatStatusDTO();
            dto.setSeatId(s.getSeatId());
            dto.setSeatCode(s.getSeatCode());
            dto.setSeatType(s.getSeatType());
            dto.setRowIndex(s.getRowIndex());
            dto.setColIndex(s.getColIndex());

            if (soldSeatIds.contains(s.getSeatId())) {
                dto.setStatus("SOLD");
            } else {
                // if (heldSeatIds.contains(s.getSeatId())) dto.setStatus("HELD");
                // else
                dto.setStatus("AVAILABLE");
            }
            return dto;
        }).toList();
    }
}
