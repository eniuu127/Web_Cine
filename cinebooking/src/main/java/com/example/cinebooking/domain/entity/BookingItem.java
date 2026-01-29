package com.example.cinebooking.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "booking_items",
    uniqueConstraints = @UniqueConstraint(
        name="uk_booking_seat",
        columnNames={"booking_id","seat_id"}
    ),
    indexes = {
        @Index(name="idx_bi_booking", columnList="booking_id"),
        @Index(name="idx_bi_seat", columnList="seat_id")
    }
)
public class BookingItem {

    // đại diện ghế trong đơn đặt
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="booking_item_id")
    private Long bookingItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="booking_id", nullable=false,
        foreignKey=@ForeignKey(name="fk_bi_booking"))
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="seat_id", nullable=false,
        foreignKey=@ForeignKey(name="fk_bi_seat"))
    private Seat seat;

    // optional: lưu giá tại thời điểm đặt (nếu muốn)
    // @Column(name="price", nullable=false)
    // private Integer price;
}

