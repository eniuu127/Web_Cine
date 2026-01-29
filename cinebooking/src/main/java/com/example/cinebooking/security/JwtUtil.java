package com.example.cinebooking.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.example.cinebooking.domain.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    // TODO: đổi secret trước khi nộp
    private static final String SECRET = "CINEBOOKING_SUPER_SECRET_KEY_32_CHARS_MIN_123456";
    private static final long EXPIRE_MS = 1000L * 60 * 60 * 24; // 24h

    public static String generate(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRE_MS);

        return Jwts.builder()
                .setSubject(user.getEmail())                       // sub = email
                .claim("userId", user.getUserId())                 // custom claim
                .claim("role", user.getRole())                     // ADMIN/STAFF/USER
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    public static Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Dùng cho /api/auth/me: lấy email từ token
    public static String getSubject(String token) {
        return parse(token).getSubject();
    }

    // (tuỳ chọn) lấy userId/role từ token nếu cần
    public static Long getUserId(String token) {
        Object v = parse(token).get("userId");
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(String.valueOf(v));
    }

    public static String getRole(String token) {
        Object v = parse(token).get("role");
        return v == null ? null : String.valueOf(v);
    }
}
