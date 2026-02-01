package com.example.cinebooking.messaging.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TicketIssuedEvent {
  public String bookingCode;
  public String customerEmail;
  public String customerName;

  public String movieTitle;
  public String roomName;
  public LocalDateTime showtimeStart;

  public List<String> seatCodes;
  public BigDecimal totalAmount;

  // QR sẽ encode cái "verifyUrl?code=..."; code có thể là bookingCode hoặc ticketCode
  public String verifyUrl;

  public TicketIssuedEvent() {}
}
