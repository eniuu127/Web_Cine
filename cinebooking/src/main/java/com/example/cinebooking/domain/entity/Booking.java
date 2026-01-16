package com.example.cinebooking.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_booking_code",
        columnNames = "booking_code"
    )
)

public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "booking_code", nullable = false)
    private String bookingCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "showtime_id", 
        nullable=false,
        foreignKey = @ForeignKey(name="fk_booking_showtime")
    )
    private Showtime showtime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id", 
        nullable=false,
        foreignKey = @ForeignKey(name="fk_booking_user")
    )
    private User user;

    @OneToOne(mappedBy = "booking", fetch = FetchType.LAZY)
    private Payment payment;

    @Column(name = "guest_mail", nullable = false)
    private String guestMail;

    @Column (nullable = false)
    private String status; // ví dụ: PENDING, CONFIRMED, CANCELLED

    // thời điểm hết hạn giữ ghế
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();



}
