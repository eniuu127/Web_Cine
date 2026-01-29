package com.example.cinebooking.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.cinebooking.domain.entity.Movie;
import com.example.cinebooking.domain.entity.PaymentMethod;
import com.example.cinebooking.domain.entity.Room;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.domain.entity.User;
import com.example.cinebooking.repository.MovieRepository;
import com.example.cinebooking.repository.PaymentMethodRepository;
import com.example.cinebooking.repository.RoomRepository;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public void run(String... args) {

        /* ===================== USERS ===================== */
        if (userRepository.count() == 0) {
            User u1 = new User();
            u1.setFullName("Test User 1");
            u1.setEmail("u1@gmail.com");
            u1.setPasswordHash("123456"); // fake data (đồ án) — không cần hash thật
            u1.setRole("CUSTOMER");

            userRepository.save(u1);

            System.out.println("✅ Seed user DONE: u1@gmail.com (id auto)");
        }
        /* ===================== PAYMENT METHODS ===================== */
        
        seedPaymentMethod("MOMO", "MoMo Wallet", true, 1);
        seedPaymentMethod("VNPAY", "VNPay Gateway", true, 2);
        seedPaymentMethod("CASH", "Cash", true, 3);
        seedPaymentMethod("BANK", "Bank Transfer", true, 4);

        System.out.println(" Seed payment methods DONE");

        // Nếu đã có core data + users thì mới skip
        boolean hasCoreData = movieRepository.count() > 0;
        boolean hasUsers = userRepository.count() > 0;

        if (hasCoreData && hasUsers) {
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
        //movie2.setPosterUrl("https://example.com/endgame.jpg");
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
    
        private void seedPaymentMethod(String code, String name, boolean active, int sortOrder) {
            String normalized = code.trim().toUpperCase();

            if (paymentMethodRepository.existsById(normalized)) return;

            PaymentMethod m = new PaymentMethod();
            m.setCode(normalized);
            m.setName(name);
            m.setActive(active);
            m.setSortOrder(sortOrder);

            paymentMethodRepository.save(m);
        }
}
