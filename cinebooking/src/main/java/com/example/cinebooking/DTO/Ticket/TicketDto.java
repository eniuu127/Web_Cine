package com.example.cinebooking.DTO.Ticket;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {

    // mã vé dùng để staff check-in
    private String ticketCode;

    // nội dung QR (thường = ticketCode)
    private String qrContent;

    // mã ghế (C4, D5...)
    private String seatCode;

    // trạng thái vé: ISSUED / USED / CANCELLED
    private String status;

    // giá vé của ghế đó
    private Long price;

    private LocalDateTime checkedInAt;
}
