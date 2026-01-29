package com.example.cinebooking.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
}
