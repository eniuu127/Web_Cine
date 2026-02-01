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

    // ✅ PHẢI có bean JwtAuthFilter (vì JwtAuthFilter không @Component)
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
            .requestMatchers("/admin/**","/css/**", "/js/**", "/image/**",
             "/images/**", "/favicon.ico","/static/**").permitAll()

            // error page
            .requestMatchers("/error", "/error/**").permitAll()

            // UI pages (public)
            .requestMatchers(
                "/", "/trangchu",
                "/login",
                "/movies/**",
                "/showtimes/**",
                "/checkout/**",
                "/tickets/**",
                "/my-bookings",
                "/prices",
                "/seatmap/**" 
            ).permitAll()

            // UI auth page
            // .requestMatchers("/auth", "/auth/**").permitAll()

            // PUBLIC API/AUTH
            // PUBLIC 
            .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()

            // /api/auth/me phải cần đăng nhập
            .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/showtimes/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/seats/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/payment-methods/**").permitAll()

            // USER
            .requestMatchers("/api/holds/**").hasRole("USER")
            .requestMatchers("/api/bookings/**").hasRole("USER")
            .requestMatchers("/api/payments/**").hasRole("USER")
            .requestMatchers(HttpMethod.GET, "/my-bookings").hasAnyRole("USER","ADMIN")


            // STAFF
            .requestMatchers("/api/staff/**").hasAnyRole("STAFF", "ADMIN")

            // ADMIN
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            .anyRequest().authenticated()
        );

        // ✅ dùng bean filter (đừng tạo new filter ở đây)
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
