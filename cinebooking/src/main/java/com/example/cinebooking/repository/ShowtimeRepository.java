package com.example.cinebooking.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.Showtime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // xem danh sách suất chiếu theo phim
    List<Showtime> findByMovie_MovieIdOrderByStartTimeAsc(Long movieId);

    // admin kiểm tra suất chiếu có bị trùng không
    List<Showtime> findByRoom_RoomIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long roomId, LocalDateTime from, LocalDateTime to
    );

    // tìm kiếm theo thời gian
    List<Showtime> findByStartTimeBetweenOrderByStartTimeAsc(
            LocalDateTime from, LocalDateTime to
    );

    /**
     * Lịch chiếu theo ngày (range from->to), FETCH movie+room để tránh N+1.
     * Dùng from = date.atStartOfDay(), to = date.plusDays(1).atStartOfDay()
     */
    @EntityGraph(attributePaths = {"movie", "room"})
    List<Showtime> findByStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(
            LocalDateTime from,
            LocalDateTime to
    );

    // admin kiểm tra overlap
    List<Showtime> findByRoom_RoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId, LocalDateTime end, LocalDateTime start
    );

    List<Showtime> findByMovieMovieIdAndStartTimeGreaterThanEqualAndStartTimeLessThan(
            Long movieId, LocalDateTime from, LocalDateTime to
    );

    List<Showtime> findByMovieMovieIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long movieId, LocalDateTime from, LocalDateTime to
    );
}
