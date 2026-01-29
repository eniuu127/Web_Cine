package com.example.cinebooking.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Hold.HoldSeatRequest;
import com.example.cinebooking.DTO.Hold.HoldSeatResponse;
import com.example.cinebooking.service.RedisSeatHoldService;

@RestController
@RequestMapping("/api/holds")
public class HoldController {

    private final RedisSeatHoldService holdService;

    public HoldController(RedisSeatHoldService holdService) {
        this.holdService = holdService;
    }

    // POST /api/holds  (giữ ghế 5 phút bằng Redis TTL)
    @PostMapping
    public HoldSeatResponse hold(@RequestBody HoldSeatRequest req) {
        return holdService.holdSeats(req);
    }
}
