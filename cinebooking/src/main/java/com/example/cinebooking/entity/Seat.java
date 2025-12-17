package com.example.cinebooking.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table( 
    name = "seats",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_room_row_seat", 
            columnNames = {"room_id", "seat_code"}
        )
        // đảm bảo trong cùng 1 phòng không có 2 ghế cùng mã ghế
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_code", nullable = false)
    private String seatCode; // mã ghế, ví dụ: A1, B5

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;
}
