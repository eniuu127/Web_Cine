package com.example.cinebooking.DTO.Room;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor; import lombok.Getter;
@Getter
@AllArgsConstructor
public class AdminRoomResponse {
    private Long roomId;
    private String roomName;
    private String screenType;
    private Integer seatCount;
    private LocalDateTime createdAt;
}
