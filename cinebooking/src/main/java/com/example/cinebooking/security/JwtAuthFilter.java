package com.example.cinebooking.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = JwtUtil.parse(token);
                Long userId = toLong(claims.get("userId"));
                String email = claims.getSubject();

                // role có thể là "USER" hoặc "ROLE_USER"
                String roleRaw = (String) claims.get("role");
                String role = normalizeRole(roleRaw); // => USER

                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                var authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // set attribute cho controller dùng
                request.setAttribute("userId", toLong(claims.get("userId")));
                request.setAttribute("role", role);
                request.setAttribute("email", email);
            } catch (Exception ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    // ===== helpers =====
    private String normalizeRole(String roleRaw) {
        if (roleRaw == null) return null;
        if (roleRaw.startsWith("ROLE_")) {
            return roleRaw.substring("ROLE_".length());
        }
        return roleRaw;
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof String s) return Long.valueOf(s);
        return Long.valueOf(v.toString());
    }
}
