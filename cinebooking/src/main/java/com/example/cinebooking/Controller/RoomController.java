package com.example.cinebooking.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinebooking.DTO.Room.RoomDTO;
import com.example.cinebooking.DTO.Seat.SeatDTO;
import com.example.cinebooking.service.RoomService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor

public class RoomController {

    private final RoomService roomService;
    @GetMapping
    public List<RoomDTO> getAllRooms(){
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public RoomDTO getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }
    
    @GetMapping("/{id}/seats")
    public List<SeatDTO> getSeatsByRoomId(@PathVariable Long id) {
        return roomService.getSeatsByRoomId(id);
    }
}
