package com.example.cinebooking.DTO.Event;

import java.time.LocalDateTime;
import java.util.List;

public class TicketIssuedEvent {
    public String bookingCode;
    public String toEmail;
    public String customerName;

    public String movieTitle;
    public String roomName;
    public LocalDateTime startTime;

    public Integer totalAmount;

    public List<TicketItem> tickets;

    public static class TicketItem {
        public String ticketCode;
        public String seatCode;
        public Integer price;
        public String qrContent;
    }
}
