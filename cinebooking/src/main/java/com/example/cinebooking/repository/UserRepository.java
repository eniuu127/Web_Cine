package com.example.cinebooking.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.cinebooking.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<User> findByRole(String role, Pageable pageable);
    
    // search staff theo name/email
    @Query("""
    select u from User u
    where u.role = 'STAFF'
        and (lower(u.email) like lower(concat('%', :q, '%'))
        or lower(u.fullName) like lower(concat('%', :q, '%')))
    """)
    Page<User> searchStaff(@Param("q") String q, Pageable pageable);

}
