package com.example.cinebooking.Controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.AdminStaff.CreateStaffRequest;
import com.example.cinebooking.DTO.AdminStaff.SetStaffStatusRequest;
import com.example.cinebooking.DTO.AdminStaff.StaffResponse;
import com.example.cinebooking.DTO.AdminStaff.UpdateStaffRequest;
import com.example.cinebooking.service.AdminStaffService;
import com.example.cinebooking.service.BookingService;

@RestController
@RequestMapping("/api/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStaffController {

    private final AdminStaffService adminStaffService;
    private final BookingService bookingService;

    public AdminStaffController(AdminStaffService adminStaffService, BookingService bookingService) {
        this.adminStaffService = adminStaffService;
        this.bookingService = bookingService;
    }

    // LIST + SEARCH
    // GET /api/admin/staff?q=&page=&size=
    @GetMapping
    public ResponseEntity<Page<StaffResponse>> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(adminStaffService.listStaff(q, page, size));
    }

    // CREATE STAFF
    @PostMapping
    public ResponseEntity<StaffResponse> create(@RequestBody CreateStaffRequest req) {
        return ResponseEntity.ok(adminStaffService.createStaff(req));
    }

    // UPDATE STAFF
    @PutMapping("/{staffId}")
    public ResponseEntity<StaffResponse> update(
            @PathVariable Long staffId,
            @RequestBody UpdateStaffRequest req) {

        return ResponseEntity.ok(adminStaffService.updateStaff(staffId, req));
    }

    // ENABLE/DISABLE STAFF
    @PatchMapping("/{staffId}/status")
    public ResponseEntity<StaffResponse> setStatus(
            @PathVariable Long staffId,
            @RequestBody SetStaffStatusRequest req) {

        return ResponseEntity.ok(adminStaffService.setStaffStatus(staffId, req));
    }

    // DELETE STAFF (optional)
    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> delete(@PathVariable Long staffId) {
        adminStaffService.deleteStaff(staffId);
        return ResponseEntity.noContent().build();
    }
    // ===== ADMIN REVENUE (reuse - no new controller) =====
    // GET /api/admin/staff/revenue/daily?from=2026-02-01&to=2026-02-07
    @GetMapping("/revenue/daily")
    public ResponseEntity<Map<String, Object>> revenueDaily(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(bookingService.revenueDaily(from, to));
    }

    // GET /api/admin/staff/revenue/monthly?year=2026
    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<String, Object>> revenueMonthly(@RequestParam("year") int year) {
        return ResponseEntity.ok(bookingService.revenueMonthly(year));
    }

}
