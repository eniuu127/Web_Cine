package com.example.cinebooking.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

    @GetMapping("/test")
    public String testAPI() {
        return "Chào mừng bạn đến với API của CineBooking!";
    }
}
