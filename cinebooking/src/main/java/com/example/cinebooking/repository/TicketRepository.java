package com.example.cinebooking.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.cinebooking.domain.entity.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketCode(String ticketCode);

    // lấy danh sách vé theo bookingId
    List<Ticket> findByBooking_BookingId(Long bookingId);

    // lấy danh sách vé theo bookingCode (cái này bạn đang cần cho trang Vé của tôi)
    List<Ticket> findByBooking_BookingCode(String bookingCode);

    boolean existsByShowtime_ShowtimeIdAndSeat_SeatIdAndBooking_BookingIdNotAndBooking_Status(
        Long showtimeId,
        Long seatId,
        Long bookingId,
        String status
    );

    List<Ticket> findByShowtime_ShowtimeId(Long showtimeId);

    @Query("""
        select t.seat.seatId
        from Ticket t
        where t.showtime.showtimeId = :showtimeId
          and t.booking.status = 'PAID'
    """)
    Set<Long> findSeatIdsSoldByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("""
        select t.seat.seatId
        from Ticket t
        where t.showtime.showtimeId = :showtimeId
          and t.booking.status = 'PAID'
          and t.booking.bookingId <> :bookingId
    """)
    Set<Long> findSeatIdsSoldByShowtimeIdExcludeBooking(
        @Param("showtimeId") Long showtimeId,
        @Param("bookingId") Long bookingId
    );
}
