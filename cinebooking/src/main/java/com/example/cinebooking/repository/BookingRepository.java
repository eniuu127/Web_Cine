package com.example.cinebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    // tìm đặt chỗ theo mã đặt chỗ
    Optional<Booking> findByBookingCode(String bookingCode);

    // kiểm tra mã đặt chỗ đã tồn tại chưa
    boolean existsByBookingCode(String bookingCode);

    // tìm các booking giữ ghế quá hạn (chưa thanh toán) => huỷ
    List<Booking> findByStatusAndExpiresAtBefore(
        String status, LocalDateTime time
    );
     
    // lich su hien thi theo userId va trang thai
    @EntityGraph(attributePaths = {
        "showtime",
        "showtime.movie",
        "showtime.room",
        "tickets",
        "tickets.seat"
    }) 
    // hiển thị lịch sử mua vé của người dùng theo userId, sắp xếp theo ngày tạo mới nhất
    List<Booking> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
