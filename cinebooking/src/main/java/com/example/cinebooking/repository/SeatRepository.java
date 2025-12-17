package com.example.cinebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cinebooking.entity.Seat;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Tùy chỉnh phương thức truy vấn theo room id
    List<Seat> findByRoomId(Long roomId);
    // tương đương = SELECT * FROM seats WHERE room_id = ?

    boolean existsByRoomIdAndRowAndNumber(Long roomId, String row, Integer number);
    // kiểm tra xem ghế có tồn tại trong phòng chiếu hay không
}
