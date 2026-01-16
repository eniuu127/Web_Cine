package com.example.cinebooking.config;

import com.example.cinebooking.domain.entity.*;
import com.example.cinebooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public void run(String... args) {

        // Tránh seed lại nhiều lần
        if (movieRepository.count() > 0) {
            System.out.println("⏩ Data already exists, skip seeding");
            return;
        }

        /* ===================== MOVIES ===================== */
        Movie movie1 = new Movie();
        movie1.setTitle("Doraemon: Nobita's Sky Utopia");
        movie1.setDescription("Phim hoạt hình gia đình");
        movie1.setRuntime(105);
        movie1.setPosterUrl("https://example.com/doraemon.jpg");
        movie1.setStatus("NOW_SHOWING");
        movie1.setReleaseDate(LocalDate.of(2024, 6, 1));

        Movie movie2 = new Movie();
        movie2.setTitle("Avengers: Endgame");
        movie2.setDescription("Siêu anh hùng Marvel");
        movie2.setRuntime(181);
        movie2.setPosterUrl("https://example.com/endgame.jpg");
        movie2.setStatus("NOW_SHOWING");
        movie2.setReleaseDate(LocalDate.of(2019, 4, 26));

        movieRepository.saveAll(List.of(movie1, movie2));

        /* ===================== ROOMS ===================== */
        Room room1 = new Room();
        room1.setRoomName("Room 1");
        room1.setScreenType("2D");

        Room room2 = new Room();
        room2.setRoomName("Room 2");
        room2.setScreenType("3D");

        roomRepository.saveAll(List.of(room1, room2));

        /* ===================== SEATS ===================== */
        seedSeats(room1, 4, 10); // 4 hàng x 10 ghế = 40
        seedSeats(room2, 3, 10); // 3 hàng x 10 ghế = 30

        /* ===================== SHOWTIMES ===================== */
        Showtime showtime1 = new Showtime();
        showtime1.setMovie(movie1);
        showtime1.setRoom(room1);
        showtime1.setStartTime(
                LocalDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0)
        );
        showtime1.setEndTime(showtime1.getStartTime().plusMinutes(movie1.getRuntime()));
        showtime1.setBasePrice(80000);
        showtime1.setStatus("OPEN");

        Showtime showtime2 = new Showtime();
        showtime2.setMovie(movie2);
        showtime2.setRoom(room2);
        showtime2.setStartTime(
                LocalDateTime.now().plusDays(1).withHour(20).withMinute(0).withSecond(0).withNano(0)
        );
        showtime2.setEndTime(showtime2.getStartTime().plusMinutes(movie2.getRuntime()));
        showtime2.setBasePrice(100000);
        showtime2.setStatus("OPEN");

        showtimeRepository.saveAll(List.of(showtime1, showtime2));

        System.out.println("✅ Seed data DONE: movies, rooms, seats, showtimes");
    }

    /* ===================== SEAT GENERATOR ===================== */
    private void seedSeats(Room room, int rows, int cols) {
        List<Seat> seats = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            char rowChar = (char) ('A' + r);

            for (int c = 0; c < cols; c++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setRowIndex(r);
                seat.setColIndex(c);
                seat.setSeatCode(rowChar + String.valueOf(c + 1)); // A1, A2...
                seat.setSeatType("STANDARD");

                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }
}
