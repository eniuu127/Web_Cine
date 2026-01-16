package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
        return seatRepository.findByRoom_RoomIdOrderBySeatCodeAsc(roomId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
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
