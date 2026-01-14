package com.example.cinebooking.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cinebooking.domain.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long>{
    Optional<Room> findByRoomName(String roomName);
    
    boolean existsByRoomName(String roomName);

}
