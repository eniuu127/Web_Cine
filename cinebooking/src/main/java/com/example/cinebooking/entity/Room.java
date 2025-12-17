package com.example.cinebooking.entity;

import jakarta.persistence.*; 
//import all classs : map class java với bảng trong database
// map field với column trong bảng database
import lombok.*;
// import lombok để tự động tạo getter, setter, constructor, giảm code thủ công

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer rowCount; // số hàng ghế A-J

    @Column(nullable = false)
    private Integer seatPerRow; // số ghế mỗi hàng

    @ManyToOne(optional = false)
    // nhiều room thuộc về 1 cinema + optional = false: mỗi room bắt buộc phải có cinema
    
    @JoinColumn(name = "cinema_id")
    private Cinema cinema;

    // để phòng{id} hiển thị ghế thì
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Seat> seats = new java.util.ArrayList<>();
}
