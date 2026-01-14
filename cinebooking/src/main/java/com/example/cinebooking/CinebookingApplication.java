package com.example.cinebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CinebookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CinebookingApplication.class, args);
	}

}
