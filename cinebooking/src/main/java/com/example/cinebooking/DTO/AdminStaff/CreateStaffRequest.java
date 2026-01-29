package com.example.cinebooking.DTO.AdminStaff;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateStaffRequest {
    private String email;
    private String password;   // raw
    private String fullName;
    private Boolean enabled;   // optional (default true)
}
