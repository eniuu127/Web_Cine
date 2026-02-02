package com.example.cinebooking.DTO.Staff;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaffTicketResponse {
    private boolean ok;
    private String status;     // VALID / NOT_FOUND / CHECKED_IN / CANCELLED ...
    private String code;       // ticketCode
    private String seatCode;
    private String message;
    private String checkedInBy;
}
