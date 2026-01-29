package com.example.cinebooking.Controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Auth.LoginRequest;
import com.example.cinebooking.DTO.Auth.LoginResponse;
import com.example.cinebooking.DTO.Auth.MeResponse;
import com.example.cinebooking.DTO.Auth.RegisterRequest;
import com.example.cinebooking.domain.entity.User;
import com.example.cinebooking.repository.UserRepository;
import com.example.cinebooking.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email/password required");
        }

        String email = req.getEmail().trim().toLowerCase();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account disabled");
        }

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // ✅ Token như cũ
        String token = JwtUtil.generate(user);

        // ✅ Trả về fullName để FE hiển thị "Xin chào, <tên>"
        return new LoginResponse(token, user.getUserId(), user.getFullName(), user.getRole());
    }

    // USER tự đăng ký
    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null || req.getFullName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName/email/password required");
        }

        String email = req.getEmail().trim().toLowerCase();
        String fullName = req.getFullName().trim();

        if (fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName required");
        }

        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);

        // ✅ bắt buộc hash
        u.setPasswordHash(encoder.encode(req.getPassword()));

        u.setRole("USER");
        u.setEnabled(true);

        // ✅ (khuyến nghị) set createdAt nếu DB không default
        try {
            u.setCreatedAt(LocalDateTime.now());
        } catch (Exception ignore) {
            // nếu entity bạn không có createdAt setter thì bỏ qua
        }

        u = userRepo.save(u);

        String token = JwtUtil.generate(u);
        return new LoginResponse(token, u.getUserId(), u.getFullName(), u.getRole());
    }

    /**
     * Me endpoint: dùng Authentication (chuẩn Spring) + fallback request attribute.
     * Tránh cast principal sang String (rất hay sai).
     */
    @GetMapping("/me")
    public MeResponse me(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String token = auth.substring(7).trim();
        String email;
        try {
            email = JwtUtil.getSubject(token); // sub = email
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User u = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        return new MeResponse(u.getUserId(), u.getEmail(), u.getFullName(), u.getRole());
    }

}
