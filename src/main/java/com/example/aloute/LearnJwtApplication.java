package com.example.aloute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LearnJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnJwtApplication.class, args);
	}

}
