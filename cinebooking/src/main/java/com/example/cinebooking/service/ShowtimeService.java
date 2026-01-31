package com.example.cinebooking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Seat.SeatStatusDTO;
import com.example.cinebooking.DTO.Showtime.ShowtimeDetailDTO;
import com.example.cinebooking.domain.entity.Movie;
import com.example.cinebooking.domain.entity.Room;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.MovieRepository;
import com.example.cinebooking.repository.RoomRepository;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;
    private final RedisSeatHoldService holdService;
    private final MovieRepository movieRepo;
    private final RoomRepository roomRepo;

    public ShowtimeService(ShowtimeRepository showtimeRepository,
                           SeatRepository seatRepo,
                           TicketRepository ticketRepo,
                           RedisSeatHoldService holdService,
                           MovieRepository movieRepo,
                           RoomRepository roomRepo) {
        this.showtimeRepository = showtimeRepository;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
        this.holdService = holdService;
        this.movieRepo = movieRepo;
        this.roomRepo = roomRepo;
    }

    // ================== MAPPER (tối ưu dùng lại DTO cũ) ==================
    private ShowtimeDetailDTO toDetailDTO(Showtime st) {
        var movie = st.getMovie();
        var room = st.getRoom();

        return new ShowtimeDetailDTO(
                st.getShowtimeId(),
                st.getStartTime(),
                st.getEndTime(),

                movie.getMovieId(),
                movie.getTitle(),
                movie.getPosterUrl(),
                movie.getRuntime(),
                String.valueOf(movie.getStatus()),

                room.getRoomId(),
                room.getRoomName(),
                room.getScreenType(),
                st.getBasePrice()
        );
    }

    // ============= PUBLIC =================

    public ShowtimeDetailDTO getShowtimeDetail(Long id) {
        Showtime st = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found"));
        return toDetailDTO(st);
    }

    /**
     * Lịch chiếu theo ngày -> trả List<ShowtimeDetailDTO> (DTO cũ).
     * FE sẽ group theo movieId để render giống ảnh Beta.
     */
    public List<ShowtimeDetailDTO> getScheduleByDate(LocalDate date) {
        if (date == null) date = LocalDate.now();

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return showtimeRepository
                .findByStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(from, to)
                .stream()
                .map(this::toDetailDTO)
                .toList();
    }

    public List<SeatStatusDTO> getSeatMapByShowtime(Long showtimeId) {
        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        Long roomId = st.getRoom().getRoomId();

        // 1) lấy tất cả ghế trong phòng
        List<Seat> seats = seatRepo.findByRoom_RoomId(roomId);

        // 2) ghế đã bán (DB) - Ticket chỉ tạo khi PAID
        Set<Long> soldSeatIds = ticketRepo.findSeatIdsSoldByShowtimeId(showtimeId);

        // 3) ghế đang HOLD (Redis TTL)
        Set<Long> heldSeatIds = holdService.getHeldSeatIds(showtimeId);

        return seats.stream().map(s -> {
            SeatStatusDTO dto = new SeatStatusDTO();
            dto.setSeatId(s.getSeatId());
            dto.setSeatCode(s.getSeatCode());
            dto.setSeatType(s.getSeatType());
            dto.setRowIndex(s.getRowIndex());
            dto.setColIndex(s.getColIndex());

            if (soldSeatIds.contains(s.getSeatId())) {
                dto.setStatus("SOLD");
            } else if (heldSeatIds.contains(s.getSeatId())) {
                dto.setStatus("HELD");
            } else {
                dto.setStatus("AVAILABLE");
            }
            return dto;
        }).toList();
    }

    // =================== ADMIN ====================

    // Admin list all showtimes (tối ưu: không gọi findById từng cái)
    public List<ShowtimeDetailDTO> adminGetAllShowtimes() {
        return showtimeRepository.findAll().stream()
                .map(this::toDetailDTO)
                .toList();
    }

    /**
     * Admin create showtime (endTime có thể null => auto = start + runtime)
     */
    public ShowtimeDetailDTO adminCreateShowtime(Long movieId, Long roomId, LocalDateTime startTime,
                                                 LocalDateTime endTime, Integer basePrice) {

        validateShowtimeReq(movieId, roomId, startTime, basePrice);

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + movieId));

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId));

        LocalDateTime end = endTime;
        if (end == null) {
            end = startTime.plusMinutes(movie.getRuntime());
        }
        if (!end.isAfter(startTime)) throw bad("endTime must be after startTime");

        // chống trùng phòng/giờ
        ensureNoOverlap(roomId, startTime, end, null);

        Showtime st = new Showtime();
        st.setMovie(movie);
        st.setRoom(room);
        st.setStartTime(startTime);
        st.setEndTime(end);
        st.setBasePrice(basePrice);

        // nếu entity có status thì set OPEN cho đẹp
        try {
            st.getClass().getMethod("setStatus", String.class).invoke(st, "OPEN");
        } catch (Exception ignored) {}

        st = showtimeRepository.save(st);
        return toDetailDTO(st);
    }

    public ShowtimeDetailDTO adminUpdateShowtime(Long showtimeId, Long movieId, Long roomId,
                                                 LocalDateTime startTime, LocalDateTime endTime, Integer basePrice) {

        Showtime st = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found: " + showtimeId));

        validateShowtimeReq(movieId, roomId, startTime, basePrice);

        Movie movie = movieRepo.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + movieId));

        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId));

        LocalDateTime end = endTime;
        if (end == null) {
            end = startTime.plusMinutes(movie.getRuntime());
        }
        if (!end.isAfter(startTime)) throw bad("endTime must be after startTime");

        ensureNoOverlap(roomId, startTime, end, showtimeId);

        st.setMovie(movie);
        st.setRoom(room);
        st.setStartTime(startTime);
        st.setEndTime(end);
        st.setBasePrice(basePrice);

        st = showtimeRepository.save(st);
        return toDetailDTO(st);
    }

    public void adminDeleteShowtime(Long showtimeId) {
        if (!showtimeRepository.existsById(showtimeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Showtime not found: " + showtimeId);
        }
        showtimeRepository.deleteById(showtimeId);
    }

    private void validateShowtimeReq(Long movieId, Long roomId, LocalDateTime startTime, Integer basePrice) {
        if (movieId == null) throw bad("movieId is required");
        if (roomId == null) throw bad("roomId is required");
        if (startTime == null) throw bad("startTime is required");
        if (basePrice == null || basePrice <= 0) throw bad("basePrice must be > 0");
    }

    private void ensureNoOverlap(Long roomId, LocalDateTime start, LocalDateTime end, Long ignoreShowtimeId) {
        List<Showtime> overlaps =
                showtimeRepository.findByRoom_RoomIdAndStartTimeLessThanAndEndTimeGreaterThan(roomId, end, start);

        if (ignoreShowtimeId != null) {
            overlaps = overlaps.stream()
                    .filter(x -> !x.getShowtimeId().equals(ignoreShowtimeId))
                    .toList();
        }
        if (!overlaps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Showtime overlaps existing showtime in this room");
        }
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
