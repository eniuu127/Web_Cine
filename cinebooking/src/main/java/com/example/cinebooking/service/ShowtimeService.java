package com.example.cinebooking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cinebooking.DTO.Showtime.ShowtimeDTO;
import com.example.cinebooking.domain.entity.Showtime;
import com.example.cinebooking.repository.ShowtimeRepository;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository) {
        this.showtimeRepository = showtimeRepository;
    }

    public List<ShowtimeDTO> getShowtimeByMovie(Long movieId){
        return showtimeRepository.findByMovie_MovieIdOrderByStartTimeAsc(movieId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private ShowtimeDTO toDTO(Showtime s){
        ShowtimeDTO dto = new ShowtimeDTO();
        dto.setShowtimeId(s.getShowtimeId());
        dto.setStartTime(s.getStartTime());
        dto.setBasePrice(s.getBasePrice());
        dto.setStatus(s.getStatus());
        return dto;
    }
}
