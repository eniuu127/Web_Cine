package com.example.cinebooking.DTO.Booking;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class BookingDetailDTO {

    private final String bookingCode;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;

    private final Long showtimeId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private final Long movieId;
    private final String movieTitle;
    private final String posterUrl;

    private final Long roomId;
    private final String roomName;

    private final String paymentMethodCode; // null nếu chưa chọn
    private final String paymentMethodName;

    private final Integer total;

    private final List<SeatDTO> seats;

    public BookingDetailDTO(
            String bookingCode,
            String status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            Long showtimeId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long movieId,
            String movieTitle,
            String posterUrl,
            Long roomId,
            String roomName,
            String paymentMethodCode,
            String paymentMethodName,
            Integer total,
            List<SeatDTO> seats
    ) {
        this.bookingCode = bookingCode;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.showtimeId = showtimeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
        this.roomId = roomId;
        this.roomName = roomName;
        this.paymentMethodCode = paymentMethodCode;
        this.paymentMethodName = paymentMethodName;
        this.total = total;
        this.seats = seats;
    }

    @Getter
    public static class SeatDTO {
        private final Long seatId;
        private final String seatCode;

        public SeatDTO(Long seatId, String seatCode) {
            this.seatId = seatId;
            this.seatCode = seatCode;
        }
    }
}
