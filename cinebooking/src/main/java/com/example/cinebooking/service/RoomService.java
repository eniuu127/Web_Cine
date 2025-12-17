package com.example.cinebooking.service;

import org.springframework.stereotype.Service;

import com.example.cinebooking.entity.Cinema;
import com.example.cinebooking.entity.Room;
import com.example.cinebooking.entity.Seat;
import com.example.cinebooking.repository.CineRepository;
import com.example.cinebooking.repository.RoomRepository;
import com.example.cinebooking.repository.SeatRepository;

import jakarta.transaction.Transactional;
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final CineRepository cineRepository;
    private final SeatRepository seatRepository;

    public RoomService(RoomRepository roomRepository,
                        CineRepository cineRepository,
                        SeatRepository seatRepository
    ) {
        this.roomRepository = roomRepository;
        this.cineRepository = cineRepository;
        this.seatRepository = seatRepository;
    }
    // Các phương thức xử lý nghiệp vụ liên quan đến Room sẽ được thêm vào đây
    // Ví dụ: tạo phòng chiếu, lấy danh sách phòng chiếu, cập nhật thông tin phòng chiếu, v.v.
    
    @Transactional
    public Room createRoomAndGenerateSeats(Long cinemaId, String roomName, 
                                            Integer rowCount, int seatsPerRow)
        {
            Cinema cinema = cineRepository.findById(cinemaId).orElseThrow();
            // tìm cinema theo id, nếu không tìm thấy thì ném exception

            //tạp room
            Room room = new Room();
            room.setName(roomName);
            room.setRowCount(rowCount);
            room.setSeatPerRow(seatsPerRow);
            room.setCinema(cinema);
            Room savedRoom = roomRepository.save(room);


            // thuật toán sinh ghế : A1, A2, A3...B1, B2, B3...
            for(int r = 0; r < rowCount; r++){
                char rowChar = (char) ('A' + r); // chuyển số hàng thành ký tự A, B, C...

                // sinh ghế trong hàng
                for(int j = 1; j <= seatsPerRow; j++){
                    Seat seat = new Seat();
                    seat.setRoom(savedRoom);
                    seat.setSeatCode(rowChar + String.valueOf(j));
                    seatRepository.save(seat);
                }
            }
                return savedRoom;
        }
}
