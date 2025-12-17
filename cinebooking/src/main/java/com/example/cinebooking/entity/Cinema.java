package com.example.cinebooking.entity;

import jakarta.persistence.*; 
//import all classs : map class java với bảng trong database
// map field với column trong bảng database
import lombok.*;
// import lombok để tự động tạo getter, setter, constructor, giảm code thủ công

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)   
    private String name;

    private String location;


    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    // mappedBy: tên field trong class Room tham chiếu đến Cinema ( khoá ngoại )
    // cascade: khi xóa cinema thì xóa luôn các phòng chiếu liên quan
    // orphanRemoval: khi phòng chiếu không còn liên kết với cinema thì tự động xóa
    private List<Room> rooms = new ArrayList<>();

}
