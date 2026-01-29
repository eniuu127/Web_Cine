package com.example.cinebooking.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.AdminStaff.CreateStaffRequest;
import com.example.cinebooking.DTO.AdminStaff.SetStaffStatusRequest;
import com.example.cinebooking.DTO.AdminStaff.StaffResponse;
import com.example.cinebooking.DTO.AdminStaff.UpdateStaffRequest;
import com.example.cinebooking.domain.entity.User;
import com.example.cinebooking.repository.UserRepository;

@Service
public class AdminStaffService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public AdminStaffService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    /**
     * LIST STAFF (+ optional search)
     * GET /api/admin/staff?q=&page=&size=
     */
    @Transactional(readOnly = true)
    public Page<StaffResponse> listStaff(String q, int page, int size) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 5), 100);

        PageRequest pr = PageRequest.of(page, size);

        Page<User> p;
        if (q != null && !q.isBlank()) {
            // nếu bạn KHÔNG thêm searchStaff() vào repo thì comment dòng này và dùng findByRole
            p = userRepo.searchStaff(q.trim(), pr);
        } else {
            p = userRepo.findByRole("STAFF", pr);
        }

        return p.map(this::toResponse);
    }

    /**
     * CREATE STAFF
     * POST /api/admin/staff
     * - role fix cứng = STAFF
     */
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest req) {
        if (req == null) throw badRequest("Request is required");
        if (isBlank(req.getEmail())) throw badRequest("email is required");
        if (isBlank(req.getPassword())) throw badRequest("password is required");
        if (isBlank(req.getFullName())) throw badRequest("fullName is required");

        String email = req.getEmail().trim().toLowerCase();
        if (userRepo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        User u = new User();
        u.setEmail(email);
        u.setFullName(req.getFullName().trim());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole("STAFF"); // ✅ FIX CỨNG
        u.setEnabled(req.getEnabled() == null ? true : req.getEnabled());

        // nếu entity bạn có createdAt
        trySetCreatedAt(u);

        u = userRepo.save(u);
        return toResponse(u);
    }

    /**
     * UPDATE STAFF (info + optional reset password + enabled)
     * PUT /api/admin/staff/{id}
     */
    @Transactional
    public StaffResponse updateStaff(Long staffId, UpdateStaffRequest req) {
        if (staffId == null) throw badRequest("staffId is required");
        if (req == null) throw badRequest("Request is required");

        User u = userRepo.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        // ✅ CHỈ CHO SỬA STAFF
        if (!"STAFF".equalsIgnoreCase(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target user is not STAFF");
        }

        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            u.setFullName(req.getFullName().trim());
        }

        if (req.getEnabled() != null) {
            u.setEnabled(req.getEnabled());
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(encoder.encode(req.getPassword()));
        }

        u = userRepo.save(u);
        return toResponse(u);
    }

    /**
     * ENABLE/DISABLE STAFF
     * PATCH /api/admin/staff/{id}/status
     */
    @Transactional
    public StaffResponse setStaffStatus(Long staffId, SetStaffStatusRequest req) {
        if (staffId == null) throw badRequest("staffId is required");
        if (req == null || req.getEnabled() == null) throw badRequest("enabled is required");

        User u = userRepo.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        if (!"STAFF".equalsIgnoreCase(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target user is not STAFF");
        }

        u.setEnabled(req.getEnabled());
        u = userRepo.save(u);

        return toResponse(u);
    }

    /**
     * DELETE STAFF (optional)
     * DELETE /api/admin/staff/{id}
     */
    @Transactional
    public void deleteStaff(Long staffId) {
        if (staffId == null) throw badRequest("staffId is required");

        User u = userRepo.findById(staffId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        if (!"STAFF".equalsIgnoreCase(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target user is not STAFF");
        }

        userRepo.deleteById(staffId);
    }

    // ===== helpers =====
    private StaffResponse toResponse(User u) {
        return new StaffResponse(
                u.getUserId(),
                u.getEmail(),
                u.getFullName(),
                u.getEnabled(),
                getCreatedAtSafe(u)
        );
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static ResponseStatusException badRequest(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    // Nếu entity có createdAt (LocalDateTime) -> set, còn không thì bỏ qua
    private void trySetCreatedAt(User u) {
        try {
            var m = u.getClass().getMethod("setCreatedAt", LocalDateTime.class);
            m.invoke(u, LocalDateTime.now());
        } catch (Exception ignored) {}
    }

    private LocalDateTime getCreatedAtSafe(User u) {
        try {
            var m = u.getClass().getMethod("getCreatedAt");
            Object v = m.invoke(u);
            return (LocalDateTime) v;
        } catch (Exception ignored) {
            return null;
        }
    }
}
