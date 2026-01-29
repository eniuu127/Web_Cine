package com.example.cinebooking.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.cinebooking.domain.entity.User;
import com.example.cinebooking.repository.UserRepository;

@Configuration
public class AuthSeeder {

    @Bean
    CommandLineRunner seedAuthUsers(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            createIfMissing(userRepo, encoder, "admin@cine.com", "123456", "Admin", "ADMIN");
            createIfMissing(userRepo, encoder, "staff@cine.com", "123456", "Staff", "STAFF");
            createIfMissing(userRepo, encoder, "user@cine.com", "123456", "User", "USER");
        };
    }

    private void createIfMissing(UserRepository userRepo, PasswordEncoder encoder,
                                 String email, String rawPass, String fullName, String role) {

        String norm = email.trim().toLowerCase();
        if (userRepo.existsByEmail(norm)) return;

        User u = new User();
        u.setEmail(norm);
        u.setFullName(fullName);
        u.setPasswordHash(encoder.encode(rawPass));
        u.setRole(role);
        u.setEnabled(true);

        userRepo.save(u);
    }
}
