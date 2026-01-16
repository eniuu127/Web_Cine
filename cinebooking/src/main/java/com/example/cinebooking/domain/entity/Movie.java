package com.example.cinebooking.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "runtime", nullable = false)
    private Integer runtime;

    @Column(name = "poster_url", nullable = false)
    private String posterUrl;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    // hibernate sẽ tự động gán giá trị ngày giờ hiện tại khi bản ghi được tạo
    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Showtime> showtimes = new ArrayList<>();

}
