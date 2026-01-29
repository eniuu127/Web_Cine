package com.example.cinebooking.DTO.Room;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class AdminRoomRequest {
    private String roomName;
    private String screenType; // 2D/3D/IMAX...
}
