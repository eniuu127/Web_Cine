package com.example.cinebooking.domain.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@Entity
@Table(
    name = "payments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_payment_booking",
        columnNames = {"booking_id"}
    )
)
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    // 1 booking - 1 payment
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "booking_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_payment_booking")
    )
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "method_code",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_payment_method")
    )
    private PaymentMethod paymentMethod;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "provider_txn_id", length = 100)
    private String providerTxnId;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
