package com.example.cinebooking.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffPingController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
            "ok", true,
            "message", "staff token ok"
        );
    }
}
