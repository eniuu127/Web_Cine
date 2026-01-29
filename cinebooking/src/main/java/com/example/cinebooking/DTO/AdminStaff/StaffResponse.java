package com.example.cinebooking.DTO.AdminStaff;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StaffResponse {
    private Long userId;
    private String email;
    private String fullName;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
