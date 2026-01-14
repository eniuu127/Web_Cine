package com.example.cinebooking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.domain.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long>{
    List<Movie> findByStatusOrderByCreatedAtDesc(String status);
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
}
