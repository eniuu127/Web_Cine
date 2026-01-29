package com.example.cinebooking.DTO.Showtime;

public class ShowtimeListDTO {
    private Long showtimeId;
    private String startTime; // "HH:mm"
    private String roomName;

    public ShowtimeListDTO(Long showtimeId, String startTime, String roomName) {
        this.showtimeId = showtimeId;
        this.startTime = startTime;
        this.roomName = roomName;
    }

    public Long getShowtimeId() { 
        return showtimeId; 
    }
    public String getStartTime() { 
        return startTime; 
    }
    public String getRoomName() { 
        return roomName; 
    }
}

