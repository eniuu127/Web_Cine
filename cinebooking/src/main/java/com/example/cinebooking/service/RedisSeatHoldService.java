package com.example.cinebooking.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Hold.HoldSeatRequest;
import com.example.cinebooking.DTO.Hold.HoldSeatResponse;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.SeatRepository;
import com.example.cinebooking.repository.ShowtimeRepository;
import com.example.cinebooking.repository.TicketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class RedisSeatHoldService {
    // TTL giữ ghế trong Redis (5 phút)
    public static final Duration HOLD_TTL = Duration.ofMinutes(10);
    
    private static final String HOLD_KEY_PREFIX = "hold:";
    private static final String SEAT_LOCK_PREFIX = "seat_lock:";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final ShowtimeRepository showtimeRepo;
    private final SeatRepository seatRepo;
    private final TicketRepository ticketRepo;  

    public RedisSeatHoldService(StringRedisTemplate redis,
                                ObjectMapper objectMapper,
                                ShowtimeRepository showtimeRepo,
                                SeatRepository seatRepo,
                                TicketRepository ticketRepo) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.showtimeRepo = showtimeRepo;
        this.seatRepo = seatRepo;
        this.ticketRepo = ticketRepo;
    }
    public HoldSeatResponse holdSeats(HoldSeatRequest req) {
        if (req.getShowtimeId() == null) throw new IllegalArgumentException("showtimeId is required");
        if (req.getSeatIds() == null || req.getSeatIds().isEmpty()) throw new IllegalArgumentException("seatIds is required");

        // loại trùng + null
        List<Long> seatIds = req.getSeatIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (seatIds.isEmpty()) throw new IllegalArgumentException("seatIds is required");

        Showtime showtime = showtimeRepo.findById(req.getShowtimeId())
                .orElseThrow(() -> new IllegalArgumentException("Showtime not found"));

        // validate seat thuộc room
        Long roomId = showtime.getRoom().getRoomId();
        List<Seat> seats = seatRepo.findAllById(seatIds);
        if (seats.size() != seatIds.size()) throw new IllegalArgumentException("One or more seats not found");
        for (Seat s : seats) {
            if (!roomId.equals(s.getRoom().getRoomId())) {
                throw new IllegalArgumentException("Seat " + s.getSeatCode() + " not in this showtime room");
            }
        }

        String holdId = UUID.randomUUID().toString().replace("-", "");
        String holdKey = HOLD_KEY_PREFIX + holdId;

        // 1) lock từng ghế bằng NX + TTL
        List<String> acquiredLocks = new ArrayList<>();
        try {
            for (Long seatId : seatIds) {
                  // CHẶN GHẾ ĐÃ SOLD (đã có ticket trong DB)
                if (ticketRepo.existsByShowtime_ShowtimeIdAndSeat_SeatId(showtime.getShowtimeId(), seatId)) {
                    throw new IllegalStateException("Seat already SOLD: seatId=" + seatId);
                }

                // LOCK KEY ĐÚNG PREFIX
                String lockKey = SEAT_LOCK_PREFIX + showtime.getShowtimeId() + ":" + seatId;

                Boolean ok = redis.opsForValue().setIfAbsent(lockKey, holdId, HOLD_TTL);
                if (!Boolean.TRUE.equals(ok)) {
                    throw new IllegalStateException("Seat already held: seatId=" + seatId);
                }
                acquiredLocks.add(lockKey);
            }

            // 2) lưu hold payload (để confirm payment/booking biết danh sách ghế)
            HoldPayload payload = new HoldPayload();
            payload.setHoldId(holdId);
            payload.setShowtimeId(showtime.getShowtimeId());
            payload.setUserId(req.getUserId());
            payload.setGuestEmail(req.getGuestEmail());
            payload.setSeatIds(seatIds);

            String json;
            try {
                json = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize hold payload", e);
            }
            redis.opsForValue().set(holdKey, json, HOLD_TTL);

            HoldSeatResponse res = new HoldSeatResponse();
            res.setHoldId(holdId);
            res.setShowtimeId(showtime.getShowtimeId());
            res.setSeatIds(seatIds);
            res.setExpiresAt(LocalDateTime.now().plusSeconds(HOLD_TTL.getSeconds()));
            return res;

        } catch (RuntimeException ex) {
            // rollback: nhả những lock đã giữ
            if (!acquiredLocks.isEmpty()) {
                redis.delete(acquiredLocks);
            }
            redis.delete(holdKey);
            throw ex;
        }
    }

    public HoldPayload getHoldOrThrow(String holdId) {
        String json = redis.opsForValue().get(HOLD_KEY_PREFIX + holdId);
        if (json == null) throw new IllegalArgumentException("Hold expired or not found");
        try {
            return objectMapper.readValue(json, new TypeReference<HoldPayload>(){});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse hold payload", e);
        }
    }

    public long getHoldTtlSeconds(String holdId) {
        Long ttl = redis.getExpire(HOLD_KEY_PREFIX + holdId);
        if (ttl == null) return -2; // unknown
        return ttl;
    }

    public void assertHoldOwner(HoldPayload payload, Long userId, String guestEmail) {
        if (payload.getUserId() != null) {
            if (!Objects.equals(payload.getUserId(), userId)) {
                throw new IllegalArgumentException("Hold does not belong to this user");
            }
        } else {
            // guest
            if (payload.getGuestEmail() == null || guestEmail == null) {
                throw new IllegalArgumentException("Guest hold requires guestEmail");
            }
            if (!payload.getGuestEmail().equalsIgnoreCase(guestEmail)) {
                throw new IllegalArgumentException("Hold does not belong to this guest");
            }
        }
    }

    public void releaseHold(String holdId) {
        // nếu hold đã hết hạn thì không cần làm gì
        String holdKey = HOLD_KEY_PREFIX + holdId;
        String json = redis.opsForValue().get(holdKey);
        if (json == null) return;

        HoldPayload payload;
        try {
            payload = objectMapper.readValue(json, HoldPayload.class);
        } catch (Exception e) {
            // fallback: vẫn xoá hold key
            redis.delete(holdKey);
            return;
        }

        List<String> lockKeys = payload.getSeatIds().stream()
                .map(seatId -> SEAT_LOCK_PREFIX + payload.getShowtimeId() + ":" + seatId)
                .collect(Collectors.toList());

        redis.delete(lockKeys);
        redis.delete(holdKey);
    }

    public Set<Long> getHeldSeatIds(Long showtimeId) {
        String pattern = SEAT_LOCK_PREFIX + showtimeId + ":*";
        Set<Long> seatIds = new HashSet<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(200)
                .build();

        try (Cursor<byte[]> cursor = redis.getConnectionFactory()
                .getConnection()
                .scan(options)) {

            while (cursor.hasNext()) {
                String key = new String(cursor.next(), StandardCharsets.UTF_8);
                String[] parts = key.split(":");
                if (parts.length == 3) {
                    seatIds.add(Long.valueOf(parts[2]));
                }
            }
        }

        return seatIds;
    }


    // Payload lưu trong Redis
     public static class HoldPayload {
        private String holdId;
        private Long showtimeId;
        private Long userId;
        private String guestEmail;
        private List<Long> seatIds;

        public String getHoldId() { return holdId; }
        public void setHoldId(String holdId) { this.holdId = holdId; }
        public Long getShowtimeId() { return showtimeId; }
        public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getGuestEmail() { return guestEmail; }
        public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
        public List<Long> getSeatIds() { return seatIds; }
        public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }
    }
}