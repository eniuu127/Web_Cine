package com.example.cinebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinebooking.entity.Cinema;

// Spring tự tạo class implementation - không dùng class
public interface CineRepository extends JpaRepository<Cinema, Long> {

}


// import JpaRepository để thừa kế các phương thức CRUD (Create, Read, Update, Delete) cho entity Cinema

// ví dụ :
// Cinema cinema = new Cinema();
// cinema.setName("CGV");
// cinema.setAddress("Hà Nội");
// cinemaRepository.save(cinema);

//  DB tự insert:
// INSERT INTO cinemas (name, address) VALUES ('CGV', 'Hà Nội');
