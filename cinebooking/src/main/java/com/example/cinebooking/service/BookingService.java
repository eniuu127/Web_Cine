package com.example.cinebooking.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Booking.*;
import com.example.cinebooking.domain.entity.*;
import com.example.cinebooking.repository.*;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

    private static final int HOLD_MINUTES = 5;

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          TicketRepository ticketRepository,
                          ShowtimeRepository showtimeRepository,
                          SeatRepository seatRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest request) {

        // 0) validate seatIds
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new IllegalArgumentException("seatIds is required");
        }

        // loại trùng seatIds (user có thể click trùng)
        List<Long> uniqueSeatIds = request.getSeatIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (uniqueSeatIds.isEmpty()) {
            throw new IllegalArgumentException("seatIds is required");
        }

        // 1) Lấy showtime
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));

        // 2) Tạo booking (PENDING + expiresAt để giữ ghế)
        Booking booking = new Booking();
        booking.setBookingCode(generateUniqueBookingCode());
        booking.setShowtime(showtime);
        booking.setStatus("PENDING");
        booking.setExpiresAt(LocalDateTime.now().plusMinutes(HOLD_MINUTES));
        booking.setTotalAmount(0);

        // user hoặc guest
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            booking.setUser(user); 
        } else {
            if (request.getGuestEmail() == null || request.getGuestEmail().isBlank()) {
                throw new IllegalArgumentException("guestEmail is required when userId is null");
            }
            booking.setGuestMail(request.getGuestEmail());
        }

        booking = bookingRepository.save(booking);

        // 3) Tạo ticket (mỗi ghế 1 dòng) + tính total
        int total = 0;
        List<String> seatCodes = new ArrayList<>();

        for (Long seatId : uniqueSeatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatId));

            // check ghế thuộc đúng room của showtime
            if (!seat.getRoom().getRoomId().equals(showtime.getRoom().getRoomId())) {
                throw new IllegalArgumentException("Seat " + seat.getSeatCode() + " not in this showtime room");
            }

            int price = showtime.getBasePrice();

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(showtime);  // để UNIQUE(showtime_id, seat_id) hoạt động
            ticket.setSeat(seat);
            ticket.setPrice(price);

            try {
                ticketRepository.save(ticket);
            } catch (DataIntegrityViolationException ex) {
                // ghế đã có người đặt/giữ trước đó -> UNIQUE nổ
                throw new IllegalStateException("Seat already booked: " + seat.getSeatCode());
            }

            total += price;
            seatCodes.add(seat.getSeatCode());
        }

        // 4) Update tổng tiền booking
        booking.setTotalAmount(total);
        bookingRepository.save(booking);

        // 5) trả DTO để chuyển sang màn hình chọn phương thức thanh toán
        CreateBookingResponse res = new CreateBookingResponse();
        res.setBookingCode(booking.getBookingCode()); //  không gọi booking.generateBookingCode()
        res.setStatus(booking.getStatus());
        res.setExpiresAt(booking.getExpiresAt());
        res.setTotalAmount(total);
        res.setSeatCodes(seatCodes);
        return res;
    }

    private String generateUniqueBookingCode() {
        // loop  đến khi không trùng
        for(int i = 0; i < 10; i++){
            String code = UUID.randomUUID().toString().replace("-","")
                    .substring(0,8).toUpperCase();
            if(!bookingRepository.existsByBookingCode(code)) return code;
        }
        // fallback nếu sau 10 lần vẫn trùng (khả năng rất thấp)
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}