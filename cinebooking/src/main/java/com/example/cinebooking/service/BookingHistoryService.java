package com.example.cinebooking.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinebooking.DTO.Booking.BookingHistoryItemDTO;
import com.example.cinebooking.DTO.Booking.BookingHistoryResponse;
import com.example.cinebooking.domain.entity.Booking;
import com.example.cinebooking.domain.entity.Movie;
import com.example.cinebooking.domain.entity.Room;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingHistoryService {

    private final BookingRepository bookingRepository;

    public BookingHistoryResponse getHistoryByUser(Long userId) {

        List<Booking> bookings =
            bookingRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        List<BookingHistoryItemDTO> items = bookings.stream()
            .map(this::mapToItem)
            .toList();

        BookingHistoryResponse response = new BookingHistoryResponse();
        response.setUserId(userId);
        response.setTotalBookings(items.size());
        response.setBookings(items);

        return response;
    }

    private BookingHistoryItemDTO mapToItem(Booking booking) {
        BookingHistoryItemDTO dto = new BookingHistoryItemDTO();

        dto.setBookingCode(booking.getBookingCode());
        dto.setBookingStatus(booking.getStatus());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setCreatedAt(booking.getCreatedAt());

        Showtime st = booking.getShowtime();
        dto.setShowtimeId(st.getShowtimeId());
        dto.setStartTime(st.getStartTime());

        Movie movie = st.getMovie();
        dto.setMovieId(movie.getMovieId());
        dto.setMovieTitle(movie.getTitle());

        Room room = st.getRoom();
        dto.setRoomId(room.getRoomId());
        dto.setRoomName(room.getRoomName());

        List<String> seatCodes = booking.getTickets().stream()
            .map(t -> t.getSeat().getSeatCode())
            .distinct()
            .sorted()
            .toList();

        dto.setSeatCodes(seatCodes);

        return dto;
    }
}
