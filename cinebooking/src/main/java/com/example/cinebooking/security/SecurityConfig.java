package com.example.cinebooking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        http.sessionManagement(sm ->
                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
            // CORS preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // static resources
            .requestMatchers("/css/**", "/js/**", "/image/**", "/images/**", "/favicon.ico").permitAll()

            // error page (tránh 403 khi forward /error)
            .requestMatchers("/error", "/error/**").permitAll()

            // UI pages (public)
            .requestMatchers(
                "/", "/trangchu",
                "/login",
                "/movies/**",
                "/showtimes/**"
            ).permitAll()
            
            // UI auth page
            .requestMatchers("/auth", "/auth/**").permitAll()   
            // PUBLIC API
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/showtimes/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/seats/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/payment-methods/**").permitAll()

            // USER
            .requestMatchers("/api/holds/**").hasRole("USER")
            .requestMatchers("/api/bookings/**").hasRole("USER")
            .requestMatchers("/api/payments/**").hasRole("USER")

            // STAFF
            .requestMatchers("/api/staff/**").hasAnyRole("STAFF", "ADMIN")

            // ADMIN
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
