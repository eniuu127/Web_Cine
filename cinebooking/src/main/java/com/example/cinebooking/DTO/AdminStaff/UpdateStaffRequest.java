package com.example.cinebooking.DTO.AdminStaff;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateStaffRequest {
    private String fullName;   // optional
    private String password;   // optional (reset)
    private Boolean enabled;   // optional
}
