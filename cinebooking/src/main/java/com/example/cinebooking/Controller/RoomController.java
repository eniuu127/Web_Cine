package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.cinebooking.DTO.Room.RoomDTO;
import com.example.cinebooking.DTO.Seat.SeatDTO;
import com.example.cinebooking.service.RoomService;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // ===================== PUBLIC =====================
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/rooms/{roomId}/seats")
    public ResponseEntity<List<SeatDTO>> getSeatsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getSeatsByRoomId(roomId));
    }

    // ===================== ADMIN =====================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/rooms")
    public ResponseEntity<List<RoomDTO>> adminGetAllRooms() {
        return ResponseEntity.ok(roomService.adminGetAllRooms());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/rooms")
    public ResponseEntity<RoomDTO> adminCreateRoom(@RequestBody RoomDTO req) {
        return ResponseEntity.ok(roomService.adminCreateRoom(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/rooms/{roomId}")
    public ResponseEntity<RoomDTO> adminUpdateRoom(
            @PathVariable Long roomId,
            @RequestBody RoomDTO req) {
        return ResponseEntity.ok(roomService.adminUpdateRoom(roomId, req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/rooms/{roomId}")
    public ResponseEntity<Void> adminDeleteRoom(@PathVariable Long roomId) {
        roomService.adminDeleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // ===== ADMIN – generate seat map nhanh =====
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/rooms/{roomId}/seats/generate")
    public ResponseEntity<List<SeatDTO>> adminGenerateSeats(
            @PathVariable Long roomId,
            @RequestParam Integer rows,
            @RequestParam Integer cols,
            @RequestParam(required = false) String seatType) {

        return ResponseEntity.ok(
                roomService.adminGenerateSeats(roomId, rows, cols, seatType)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/rooms/{roomId}/seats")
    public ResponseEntity<Void> adminClearSeats(@PathVariable Long roomId) {
        roomService.adminClearSeats(roomId);
        return ResponseEntity.noContent().build();
    }
}
