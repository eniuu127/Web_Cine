package com.example.cinebooking.DTO.Booking;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingDetailDTO {

    private String bookingCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private Long showtimeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long movieId;
    private String movieTitle;
    private String posterUrl;

    private Long roomId;
    private String roomName;

    private String paymentMethodCode; // null nếu chưa chọn
    private String paymentMethodName;

    private Integer total;

    private List<SeatDTO> seats;

    @Getter
    @AllArgsConstructor
    public static class SeatDTO {
        private Long seatId;
        private String seatCode;
    }
}
