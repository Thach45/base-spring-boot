package com.example.social_ute.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.social_ute.entity.User;
import com.example.social_ute.enums.Role;
import com.example.social_ute.repository.UserRepository;

import java.util.HashSet;


@Slf4j
@RequiredArgsConstructor
@Configuration
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AppInitConfig {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository)  {
        return args -> {
            if(userRepository.findByEmail( "admin@gmail.com").isEmpty()) {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = User.builder()
                        .password(passwordEncoder.encode("admin"))
                        .email("admin@gmail.com")
                        .fullName("Admin")
                        .avatarUrl("Admin")
                        .coverPhotoUrl("Admin")
                        .bio("Admin")
                        .major("Admin")
                        .schoolYear("Admin")
//                        .roles(roles)
                        .build();
                userRepository.save(user);
                System.out.println("Admin user created with email: admin@gmail.com and password: admin");
            }
            AppInitConfig.log.warn("Application started successfully");
        };
    }
}
