package com.example.cinebooking.DTO.Booking;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class BookingHistoryItemDTO {

    private String bookingCode;
    private String bookingStatus;
    private Integer totalAmount;
    private LocalDateTime createdAt;

    private Long showtimeId;
    private LocalDateTime startTime;

    private Long movieId;
    private String movieTitle;

    private Long roomId;
    private String roomName;

    
    private List<String> seatCodes; // A1 A2
}
