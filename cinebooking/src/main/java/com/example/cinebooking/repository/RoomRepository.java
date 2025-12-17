package com.example.cinebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cinebooking.entity.Room;

import java.util.List; // vì method trả về nhiều room


public interface RoomRepository extends JpaRepository<Room, Long> {
    // Tùy chỉnh phương thức truy vấn theo cinema id
    List<Room> findByCinemaId(Long cinemaId);
    // tương đương = SELECT * FROM rooms WHERE cinema_id = ?

}
