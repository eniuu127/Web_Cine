package com.example.cinebooking.domain.entity;

import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "tickets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_showtime_seat",
        columnNames = {"showtime_id", "seat_id"}),
        // đảm bảo cùng 1 suất chiếu, không có 2 vé cho cùng 1 ghế được bán
        indexes = {
            @Index(
                name = "idx_ticket_booking",
                columnList = "booking_id"
            ),
            @Index(
                name = "idx_ticket_showtime",
                columnList = "showtime_id"
            ) }
)

public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable=false, foreignKey = @ForeignKey(name="fk_ticket_booking"))
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable=false, foreignKey = @ForeignKey(name="fk_ticket_showtime"))
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable=false, foreignKey = @ForeignKey(name="fk_ticket_seat"))
    private Seat seat;

    @Column(name = "price", nullable = false)
    private Integer price;
}
