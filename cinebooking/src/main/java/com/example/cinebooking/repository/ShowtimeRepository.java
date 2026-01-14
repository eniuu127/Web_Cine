package com.example.cinebooking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.Showtime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    
    // xem danh sách suất chiếu theo phim 
    List<Showtime> findByMovie_MovieIdOrderByStartTimeAsc(Long movieId);

    // admin kiểm tra suất chiếu có bị trùng không 
    List<Showtime> findByRoom_RoomIdAndStartTimeBetweenOrderByStartTimeAsc(
        Long roomId, LocalDateTime from, LocalDateTime to 
    );

    List<Showtime> findByStartTimeBetweenOrderByStartTimeAsc(
        LocalDateTime from, LocalDateTime to
    );
}
