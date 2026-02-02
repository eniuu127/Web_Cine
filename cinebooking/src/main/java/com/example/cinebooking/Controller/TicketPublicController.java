package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.Ticket.TicketDto;
import com.example.cinebooking.service.TicketQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketPublicController {

    private final TicketQueryService ticketQueryService;

    // GET /api/tickets/by-booking/7DFEDF62
    @GetMapping("/by-booking/{bookingCode}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // khách login mới xem
    public List<TicketDto> getByBooking(@PathVariable String bookingCode) {
        return ticketQueryService.getTicketsByBookingCode(bookingCode);
    }

    
}
