package com.example.cinebooking.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Getter @Setter
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @Column(name ="code", length = 30)
    private String code; // momo or vnpay

    @Column(name ="name", length = 100, nullable = false)
    private String name;

    @Column(name ="is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
