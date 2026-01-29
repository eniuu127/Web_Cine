package com.example.cinebooking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.cinebooking.DTO.Room.RoomDTO;
import com.example.cinebooking.DTO.Seat.SeatDTO;
import com.example.cinebooking.domain.entity.Room;
import com.example.cinebooking.domain.entity.Seat;
import com.example.cinebooking.repository.RoomRepository;
import com.example.cinebooking.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    
    // =============== Public ===================
    public List<RoomDTO> getAllRooms(){
        return roomRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Phòng chiếu không tìm thấy: " + roomId));
        return toDTO(room);
    }
    public List<SeatDTO> getSeatsByRoomId(Long roomId){
        // dam bao room ton tai
        if(!roomRepository.existsById(roomId)) {
            throw new RuntimeException("Phòng chiếu không tìm thấy: " + roomId);
        }
        return seatRepository.findByRoom_RoomId(roomId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // =========== ADMIN =============
     public List<RoomDTO> adminGetAllRooms() {
        return getAllRooms();
    }

    public RoomDTO adminCreateRoom(RoomDTO req) {
        if (req == null) throw bad("Request is required");
        if (req.getRoomName() == null || req.getRoomName().isBlank()) throw bad("roomName is required");
        if (req.getScreenType() == null || req.getScreenType().isBlank()) throw bad("screenType is required");

        Room r = new Room();
        r.setRoomName(req.getRoomName().trim());
        r.setScreenType(req.getScreenType().trim());

        r = roomRepository.save(r);
        return toDTO(r);
    }

    public RoomDTO adminUpdateRoom(Long roomId, RoomDTO req) {
        if (req == null) throw bad("Request is required");

        Room r = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId));

        if (req.getRoomName() != null && !req.getRoomName().isBlank()) {
            r.setRoomName(req.getRoomName().trim());
        }
        if (req.getScreenType() != null && !req.getScreenType().isBlank()) {
            r.setScreenType(req.getScreenType().trim());
        }

        r = roomRepository.save(r);
        return toDTO(r);
    }

    public void adminDeleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }

    /**
     * ADMIN: Generate seat map nhanh cho FE
     * - rows <= 26 (A-Z)
     * - cols: số ghế mỗi hàng
     * - seatType: "STANDARD" nếu null
     *
     * Quy ước seatCode: A1..A12, B1..B12...
     */
    public List<SeatDTO> adminGenerateSeats(Long roomId, Integer rows, Integer cols, String seatType) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId));

        if (rows == null || cols == null) throw bad("rows and cols are required");
        if (rows <= 0 || cols <= 0) throw bad("rows and cols must be > 0");
        if (rows > 26) throw bad("rows max is 26 (A-Z)");

        List<Seat> existing = seatRepository.findByRoom_RoomId(roomId);
        if (!existing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seats already exist. Clear seats first.");
        }

        String st = (seatType == null || seatType.isBlank()) ? "STANDARD" : seatType.trim().toUpperCase();

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            char rowChar = (char) ('A' + i);
            for (int j = 0; j < cols; j++) {
                Seat s = new Seat();
                s.setRoom(room);
                s.setRowIndex(i);
                s.setColIndex(j);
                s.setSeatType(st);
                s.setSeatCode(rowChar + String.valueOf(j + 1));
                seats.add(s);
            }
        }

        seatRepository.saveAll(seats);

        return seatRepository.findByRoom_RoomId(roomId)
                .stream().map(this::toDTO).toList();
    }

    public void adminClearSeats(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + roomId);
        }
        List<Seat> seats = seatRepository.findByRoom_RoomId(roomId);
        seatRepository.deleteAll(seats);
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    // mapper entity -> DTO
    private RoomDTO toDTO(Room room){
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomName(room.getRoomName());
        dto.setScreenType(room.getScreenType());
        return dto;
    }

    private SeatDTO toDTO(Seat seat){
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatCode(seat.getSeatCode());
        dto.setSeatType(seat.getSeatType());
        dto.setRowIndex(seat.getRowIndex());
        dto.setColIndex(seat.getColIndex());
        return dto;
    }
}
