package com.example.cinebooking.DTO.Auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
}
