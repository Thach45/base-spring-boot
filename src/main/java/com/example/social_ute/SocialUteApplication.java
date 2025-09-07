package com.example.social_ute;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialUteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialUteApplication.class, args);
    }

}