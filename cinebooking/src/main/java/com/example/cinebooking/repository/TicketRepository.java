package com.example.cinebooking.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.cinebooking.domain.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // lấy danh sách ghế theo booking id
    List<Ticket> findByBooking_BookingId(Long bookingId);

    // kiểm tra ghế đã bị đặt trong showtime chưa 
    boolean existsByShowtime_ShowtimeIdAndSeat_SeatId(Long showtimeId, Long seatId);

    // lấy tất cả ticket theo showtime id (để hiển thị ghế đã bán / đang hold )
    List<Ticket> findByShowtime_ShowtimeId(Long showtimeId);
    
    // api trả về danh sách id ghế đã được đặt trong suất chiếu
    // SOLD = booking done
    @Query("""
        select t.seat.seatId
        from Ticket t
        where t.showtime.showtimeId = :showtimeId
        and t.booking.status = 'PAID'
    """)
    Set<Long> findSeatIdsSoldByShowtimeId(@Param("showtimeId") Long showtimeId);

}
