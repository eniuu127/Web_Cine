package com.example.cinebooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.cinebooking.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
