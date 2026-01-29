package com.example.cinebooking.Controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.AdminStaff.CreateStaffRequest;
import com.example.cinebooking.DTO.AdminStaff.SetStaffStatusRequest;
import com.example.cinebooking.DTO.AdminStaff.StaffResponse;
import com.example.cinebooking.DTO.AdminStaff.UpdateStaffRequest;
import com.example.cinebooking.service.AdminStaffService;

@RestController
@RequestMapping("/api/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    public AdminStaffController(AdminStaffService adminStaffService) {
        this.adminStaffService = adminStaffService;
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
}
