package com.example.cinebooking.domain.entity;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "showtimes",
    indexes = {
        @Index(
            name = "idx_showtime_movie_start",
            columnList = "movie_id, start_time"
        ),
        @Index(
            name = "idx_showtime_room_start",
            columnList = "room_id, start_time"
        )
    }
    // tối ưu truy vấn tìm suất chiếu theo phim và thời gian bắt đầu,
    // hoặc theo phòng chiếu và thời gian bắt đầu
)

public class Showtime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long showtimeId;

    // nhiều suất chiếu thuộc về 1 phim
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable=false, foreignKey = @ForeignKey(name="fk_showtime_movie"))
    private Movie movie;

    // nhiều suất chiếu thuộc về 1 phòng chiếu
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable=false, foreignKey = @ForeignKey(name="fk_showtime_room"))
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime; 

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime; 

    @Column(name = "base_price", nullable = false)
    private Integer basePrice;

    private String status; // ví dụ: open, cancel

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "showtime", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}
