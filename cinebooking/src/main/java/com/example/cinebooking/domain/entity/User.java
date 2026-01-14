package com.example.cinebooking.domain.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_email",
        columnNames = "email"
        // đảm bảo không có 2 người dùng cùng email
    )
)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String role; // ví dụ: ADMIN, CUSTOMER

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
