package com.example.cinebooking.Controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.Staff.StaffCheckinRequest;
import com.example.cinebooking.DTO.Staff.StaffTicketResponse;
import com.example.cinebooking.service.StaffTicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF') or hasAuthority('ROLE_STAFF')")
public class StaffTicketController {

    private final StaffTicketService staffTicketService;

    @GetMapping("/verify")
    public ResponseEntity<StaffTicketResponse> verify(@RequestParam("code") String code) {
        return ResponseEntity.ok(staffTicketService.verify(code));
    }

    @PostMapping("/check-in")
    public ResponseEntity<StaffTicketResponse> checkin(
            @RequestBody StaffCheckinRequest req,
            Principal principal
    ) {
        // principal.getName() thường là email/username sau khi JwtAuthFilter set Authentication
        String staffIdentity = (principal != null) ? principal.getName() : "STAFF";
        return ResponseEntity.ok(staffTicketService.checkin(req.getTicketCode(), staffIdentity));
    }
}
