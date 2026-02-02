package com.example.cinebooking.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Ticket.TicketDto;
import com.example.cinebooking.domain.entity.Ticket;
import com.example.cinebooking.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketQueryService {

    private final TicketRepository ticketRepo;

    // Lấy tất cả tickets theo bookingCode (dùng cho trang "Vé của tôi")
    public List<TicketDto> getTicketsByBookingCode(String bookingCode) {

        // ✅ nếu bạn muốn CHỈ lấy vé đã PAID:
        // List<Ticket> tickets = ticketRepo.findByBooking_BookingCodeAndBooking_Status(bookingCode, "PAID");

        // ✅ còn nếu muốn lấy tất cả tickets theo bookingCode:
        List<Ticket> tickets = ticketRepo.findByBooking_BookingCode(bookingCode);

        return tickets.stream().map(this::toDto).toList();
    }

    private TicketDto toDto(Ticket t) {
    TicketDto dto = new TicketDto();
    dto.setTicketCode(t.getTicketCode());
    dto.setSeatCode(t.getSeat().getSeatCode());
    dto.setPrice(t.getPrice() == null ? 0L : t.getPrice().longValue());


    // ✅ QUAN TRỌNG
    dto.setStatus(t.getStatus());
    dto.setCheckedInAt(t.getCheckedInAt());

    return dto;
}

}
