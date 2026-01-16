package com.example.cinebooking.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "seats", 
    uniqueConstraints = @UniqueConstraint(
        name = "uk_room_seat_code",
        columnNames = {"room_id", "seat_code"}
        // đảm bảo trong cùng 1 phòng không có 2 ghế cùng mã ghế
    ))
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "seat_id")
    private Long seatId;

    // nhiều ghế thuộc về 1 phòng chiếu
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable=false, foreignKey = @ForeignKey(name="fk_seat_room"))
    private Room room;

    @Column(name = "seat_code", nullable = false)
    private String seatCode;

    @Column(name = "seat_type", nullable = false)
    private String seatType; // ví dụ: STANDARD, VIP

    @Column(name = "row_index")
    private Integer rowIndex; // chỉ số hàng ghế, ví dụ: 0, 1, 2...
    
    @Column(name = "col_index")
    private Integer colIndex; // chỉ số cột ghế, ví dụ: 0, 1, 2...

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

}
