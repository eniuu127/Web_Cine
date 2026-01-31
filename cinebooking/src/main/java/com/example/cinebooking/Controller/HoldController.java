package com.example.cinebooking.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // POST /api/holds (giữ ghế 5 phút bằng Redis TTL)
    // lấy userId từ JWT (JwtAuthFilter đã set request attribute "userId")
    @PostMapping
    public HoldSeatResponse hold(
            @RequestBody HoldSeatRequest req,
            @RequestAttribute(value = "userId", required = false) Object userIdAttr
    ) {
        Long userId = toLong(userIdAttr);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        // ép theo JWT, không tin userId từ client
        req.setUserId(userId);
        req.setGuestEmail(null);

        return holdService.holdSeats(req);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof String s) return Long.valueOf(s);
        return Long.valueOf(v.toString());
    }
}
