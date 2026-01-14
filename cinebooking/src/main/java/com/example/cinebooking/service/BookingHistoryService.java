package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Booking.BookingHistoryItemDTO;
import com.example.cinebooking.DTO.Booking.BookingHistoryResponse;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Ticket;
import com.example.cinebooking.repository.BookingRepository;
import com.example.cinebooking.repository.UserRepository;

@Service
public class BookingHistoryService {

    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;

    public BookingHistoryService(BookingRepository bookingRepo, UserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
    }

    public BookingHistoryResponse getHistoryByUserId(Long userId) {

        // check user tồn tại để trả lỗi rõ ràng (không bắt buộc nhưng chuyên nghiệp)
        if (!userRepo.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        List<Booking> bookings = bookingRepo.findByUser_UserIdOrderByCreatedAtDesc(userId);

        List<BookingHistoryItemDTO> items = bookings.stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        BookingHistoryResponse res = new BookingHistoryResponse();
        res.setUserId(userId);
        res.setTotalBookings(items.size());
        res.setBookings(items);
        return res;
    }

    private BookingHistoryItemDTO toItemDTO(Booking b) {
        BookingHistoryItemDTO dto = new BookingHistoryItemDTO();

        dto.setBookingCode(b.getBookingCode());
        dto.setBookingStatus(b.getStatus());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setCreatedAt(b.getCreatedAt());

        // showtime info
        if (b.getShowtime() != null) {
            dto.setShowtimeId(b.getShowtime().getShowtimeId());
            dto.setStartTime(b.getShowtime().getStartTime());

            // movie
            if (b.getShowtime().getMovie() != null) {
                dto.setMovieId(b.getShowtime().getMovie().getMovieId());
                dto.setMovieTitle(b.getShowtime().getMovie().getTitle());
            }

            // room
            if (b.getShowtime().getRoom() != null) {
                dto.setRoomId(b.getShowtime().getRoom().getRoomId());
                dto.setRoomName(b.getShowtime().getRoom().getRoomName());
            }
        }

        // seat codes từ Ticket
        List<String> seatCodes = b.getTickets().stream()
                .map(Ticket::getSeat)
                .filter(seat -> seat != null)
                .map(seat -> seat.getSeatCode())
                .collect(Collectors.toList());

        dto.setSeatCodes(seatCodes);

        return dto;
    }
}
