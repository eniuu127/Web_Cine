package com.example.cinebooking.repository;

import java.util.Optional;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cinebooking.domain.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long>{
    // hiển thị sơ đồ ghế 1 phòng theo thứ tự mã ghế sắp xếp A1 - A2
    List<Seat> findByRoom_RoomIdOrderBySeatCodeAsc(Long roomId);

    Optional<Seat> findByRoom_RoomIdAndSeatCode(Long roomId, String seatCode);
    
    // kiểm tra mã ghế đã tồn tại trong phòng chưa
    boolean existsByRoom_RoomIdAndSeatCode(Long roomId, String seatCode);
}
