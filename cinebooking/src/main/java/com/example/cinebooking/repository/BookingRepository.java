package com.example.cinebooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.cinebooking.domain.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    // tìm đặt chỗ theo mã đặt chỗ
    Optional<Booking> findByBookingCode(String bookingCode);

    // kiểm tra mã đặt chỗ đã tồn tại chưa
    boolean existsByBookingCode(String bookingCode);

    // tìm các booking giữ ghế quá hạn (chưa thanh toán) => huỷ
    List<Booking> findByStatusAndExpiresAtIsNotNullAndExpiresAtBefore(
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

     // ================== DOANH THU (MỚI) ==================
    /**
     * Projection cho chart doanh thu
     * label: "yyyy-MM-dd" (daily) hoặc "yyyy-MM" (monthly)
     */
    interface RevenueRow {
        String getLabel();
        Long getRevenue();
        Long getOrders();
    }

    /**
     * Doanh thu theo NGÀY trong khoảng [fromDt, toDt)
     * Lưu ý: dùng paid_at (thời điểm thanh toán) để tính doanh thu chuẩn.
     */
    @Query(value = """
        SELECT DATE(b.paid_at) AS label,
               COALESCE(SUM(b.total_amount),0) AS revenue,
               COUNT(*) AS orders
        FROM bookings b
        WHERE b.status = 'PAID'
          AND b.paid_at IS NOT NULL
          AND b.paid_at >= :fromDt
          AND b.paid_at <  :toDt
        GROUP BY DATE(b.paid_at)
        ORDER BY DATE(b.paid_at)
        """, nativeQuery = true)
    List<RevenueRow> revenueDaily(
            @Param("fromDt") LocalDateTime fromDt,
            @Param("toDt") LocalDateTime toDt
    );

    /**
     * Doanh thu theo THÁNG (theo năm)
     */
    @Query(value = """
        SELECT DATE_FORMAT(b.paid_at, '%Y-%m') AS label,
               COALESCE(SUM(b.total_amount),0) AS revenue,
               COUNT(*) AS orders
        FROM bookings b
        WHERE b.status = 'PAID'
          AND b.paid_at IS NOT NULL
          AND YEAR(b.paid_at) = :year
        GROUP BY DATE_FORMAT(b.paid_at, '%Y-%m')
        ORDER BY DATE_FORMAT(b.paid_at, '%Y-%m')
        """, nativeQuery = true)
    List<RevenueRow> revenueMonthly(@Param("year") int year);
}
