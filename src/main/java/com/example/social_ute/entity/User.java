package com.example.social_ute.entity;

import com.example.social_ute.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    
    @Column(unique = true, nullable = false)
    String email;
    
    @Column(nullable = false)
    String password;
    
    @Column(nullable = false)
    @Builder.Default
    boolean isEmailVerified = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
    
    @Column(nullable = false)
    String fullName;
    
    String avatarUrl;
    
    String coverPhotoUrl;
    
    @Column(columnDefinition = "TEXT")
    String bio;
    
    String major;
    
    String schoolYear;
    
    @Column(nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
    
    LocalDateTime lastLoginAt;
    
    LocalDateTime deletedAt;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    Set<Role> roles;
}