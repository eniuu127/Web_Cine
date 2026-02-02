package com.example.cinebooking.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "tickets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_showtime_seat",
        columnNames = {"showtime_id", "seat_id"}
    ),
    indexes = {
        @Index(name = "idx_ticket_booking", columnList = "booking_id"),
        @Index(name = "idx_ticket_showtime", columnList = "showtime_id"),
        @Index(name = "uk_ticket_code", columnList = "ticket_code", unique = true)
    }
)
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "ticket_code", nullable = false, length = 32)
    private String ticketCode;

    @Column(name = "qr_content", nullable = false, length = 255)
    private String qrContent;

    // ISSUED / CHECKED_IN / CANCELLED ...
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ISSUED";

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "checked_in_by", length = 120)
    private String checkedInBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable=false,
        foreignKey = @ForeignKey(name="fk_ticket_booking"))
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable=false,
        foreignKey = @ForeignKey(name="fk_ticket_showtime"))
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable=false,
        foreignKey = @ForeignKey(name="fk_ticket_seat"))
    private Seat seat;

    @Column(name = "price", nullable = false)
    private Integer price;
}
