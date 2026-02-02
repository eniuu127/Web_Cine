package com.example.cinebooking.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cinebooking.DTO.Staff.StaffTicketResponse;
import com.example.cinebooking.domain.entity.Ticket;
import com.example.cinebooking.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffTicketService {

    private final TicketRepository ticketRepo;

    public StaffTicketResponse verify(String code) {
        code = normalize(code);
        if (code.isBlank()) {
            return new StaffTicketResponse(false, "INVALID", null, null, "Thiếu ticketCode.", null);
        }

        Ticket t = ticketRepo.findByTicketCode(code).orElse(null);
        if (t == null) {
            return new StaffTicketResponse(false, "NOT_FOUND", code, null, "Không tìm thấy vé.", null);
        }

        String seatCode = (t.getSeat() != null) ? t.getSeat().getSeatCode() : null;
        String status = (t.getStatus() != null) ? t.getStatus().toUpperCase() : "ISSUED";

        if ("CANCELLED".equals(status)) {
            return new StaffTicketResponse(false, "CANCELLED", code, seatCode, "Vé đã bị huỷ.", t.getCheckedInBy());
        }
        if ("CHECKED_IN".equals(status) || "USED".equals(status)) {
            return new StaffTicketResponse(false, "CHECKED_IN", code, seatCode, "Vé đã check-in trước đó.", t.getCheckedInBy());
        }

        // ✅ hợp lệ (ISSUED)
        return new StaffTicketResponse(true, "VALID", code, seatCode, "Vé hợp lệ.", null);
    }

    @Transactional
    public StaffTicketResponse checkin(String code, String staffIdentity) {
        code = normalize(code);
        if (code.isBlank()) {
            return new StaffTicketResponse(false, "INVALID", null, null, "Thiếu ticketCode.", null);
        }

        Ticket t = ticketRepo.findByTicketCode(code).orElse(null);
        if (t == null) {
            return new StaffTicketResponse(false, "NOT_FOUND", code, null, "Không tìm thấy vé.", null);
        }

        String seatCode = (t.getSeat() != null) ? t.getSeat().getSeatCode() : null;
        String status = (t.getStatus() != null) ? t.getStatus().toUpperCase() : "ISSUED";

        if ("CANCELLED".equals(status)) {
            return new StaffTicketResponse(false, "CANCELLED", code, seatCode, "Vé đã bị huỷ.", t.getCheckedInBy());
        }
        if ("CHECKED_IN".equals(status) || "USED".equals(status)) {
            return new StaffTicketResponse(false, "CHECKED_IN", code, seatCode, "Vé đã check-in trước đó.", t.getCheckedInBy());
        }

        // ✅ cập nhật DB thật
        t.setStatus("CHECKED_IN");
        t.setCheckedInAt(LocalDateTime.now());
        t.setCheckedInBy(staffIdentity);

        ticketRepo.save(t);

        return new StaffTicketResponse(true, "CHECKED_IN", code, seatCode, "Đã check-in thành công.", staffIdentity);
    }

    private String normalize(String raw) {
        raw = (raw == null) ? "" : raw.trim();
        // cho phép QR content kiểu: "TICKET:TKxxxx"
        if (raw.toUpperCase().startsWith("TICKET:")) {
            raw = raw.substring(7).trim();
        }
        return raw;
    }
}
